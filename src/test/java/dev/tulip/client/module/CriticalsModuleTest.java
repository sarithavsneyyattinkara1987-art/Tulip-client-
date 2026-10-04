package dev.tulip.client.module;

import com.google.gson.JsonPrimitive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

class CriticalsModuleTest {
    @Test
    void criticalsModuleUsesCombatCategoryAndRejectsMissingTargets() {
        CriticalsModule module = new CriticalsModule();

        assertEquals(ModuleCategory.COMBAT, module.getCategory());
        assertFalse(CriticalsModule.canCritical(null, null));
        assertEquals("Mode", module.getSettings().getFirst().getName());
    }

    @Test
    void choiceSettingsCycleAndPersistValidOptions() {
        AtomicReference<String> selected = new AtomicReference<>("Vanilla");
        ModuleSetting setting = ModuleSetting.choice("Mode", selected::get,
                List.of("Vanilla", "Watchdog Old", "Mospixel"), selected::set);

        setting.cycleChoice();
        assertEquals("Watchdog Old", selected.get());
        setting.loadValue(new JsonPrimitive("Mospixel"));
        assertEquals("Mospixel", selected.get());
        assertEquals("Mospixel", setting.saveValue().getAsString());
        setting.loadValue(new JsonPrimitive("Invalid"));
        assertEquals("Mospixel", selected.get());
    }

    @Test
    void keybindSettingsPersistTheirKeyCode() {
        ModuleSetting setting = ModuleSetting.keybind("Throw Key", 75);

        assertTrue(setting.isKeybind());
        assertEquals(75, setting.saveValue().getAsInt());
        setting.loadValue(new JsonPrimitive(86));
        assertEquals(86, setting.getKeyCode());
        assertEquals(86, setting.saveValue().getAsInt());
    }
}