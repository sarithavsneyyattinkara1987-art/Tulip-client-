package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class XbowCartModule extends Module {
    private final ModuleSetting key = ModuleSetting.keybind("Key", GLFW.GLFW_KEY_V);
    private double delayMs = 150.0;
    private long nextStepAt;
    private int stage;
    private final List<ModuleSetting> settings = List.of(
            key,
            ModuleSetting.slider("Delay (ms)", () -> String.format(java.util.Locale.ROOT, "%.0f ms", delayMs),
                    () -> (delayMs - 10.0) / 490.0, value -> delayMs = 10.0 + value * 490.0)
    );

    public XbowCartModule() {
        super("Xbow Cart", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null) return;
        if (stage == 0) {
            if (key.wasPressed()) {
                stage = 1;
                nextStepAt = System.currentTimeMillis() + (long) delayMs;
            }
            return;
        }
        if (System.currentTimeMillis() < nextStepAt) return;
        switch (stage) {
            case 1 -> useFirst(client, Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL);
            case 2 -> use(client, Items.TNT_MINECART);
            case 3 -> use(client, Items.FLINT_AND_STEEL);
            case 4 -> select(client, Items.CROSSBOW);
            default -> {
                stage = 0;
                return;
            }
        }
        stage++;
        nextStepAt = System.currentTimeMillis() + (long) delayMs;
        if (stage > 4) stage = 0;
    }

    private void useFirst(MinecraftClient client, Item... items) {
        for (int slot = 0; slot < 9; slot++) {
            Item item = client.player.getInventory().getStack(slot).getItem();
            for (Item candidate : items) {
                if (item == candidate) {
                    client.player.getInventory().setSelectedSlot(slot);
                    ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
                    return;
                }
            }
        }
    }

    private void use(MinecraftClient client, Item item) {
        if (select(client, item)) ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
    }

    private boolean select(MinecraftClient client, Item item) {
        for (int slot = 0; slot < 9; slot++) {
            if (!client.player.getInventory().getStack(slot).isOf(item)) continue;
            client.player.getInventory().setSelectedSlot(slot);
            return true;
        }
        return false;
    }

    @Override
    protected void onDisable() {
        stage = 0;
        nextStepAt = 0L;
    }
}