package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class FastExpModule extends Module {
    private double chance = 75.0;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Chance %", () -> String.format(java.util.Locale.ROOT, "%.0f%%", chance),
                    () -> (chance - 1.0) / 99.0, value -> chance = 1.0 + value * 99.0)
    );

    public FastExpModule() {
        super("Fast Exp", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null
                || !client.player.getMainHandStack().isOf(Items.EXPERIENCE_BOTTLE)) return;
        if (ThreadLocalRandom.current().nextDouble(100.0) < chance) {
            ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        }
    }
}