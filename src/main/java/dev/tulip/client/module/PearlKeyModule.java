package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class PearlKeyModule extends Module {
    private final ModuleSetting key = ModuleSetting.keybind("Pearl Key", GLFW.GLFW_KEY_P);
    private double throwDelayMs = 1000.0;
    private double switchDelayMs = 50.0;
    private long nextThrowAt;
    private long restoreAt;
    private int previousSlot = -1;
    private boolean throwing;
    private final List<ModuleSetting> settings = List.of(
            key,
            ModuleSetting.slider("Throw Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", throwDelayMs),
                    () -> (throwDelayMs - 100.0) / 4900.0, value -> throwDelayMs = 100.0 + value * 4900.0),
            ModuleSetting.slider("Switch Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", switchDelayMs),
                    () -> switchDelayMs / 500.0, value -> switchDelayMs = value * 500.0)
    );

    public PearlKeyModule() {
        super("Pearl Key", ModuleCategory.MISC);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null) return;
        long now = System.currentTimeMillis();
        if (throwing && now >= restoreAt) restore(client);
        if (throwing || !key.wasPressed() || now < nextThrowAt
                || client.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.ENDER_PEARL))) return;
        for (int slot = 0; slot < 9; slot++) {
            if (!client.player.getInventory().getStack(slot).isOf(Items.ENDER_PEARL)) continue;
            previousSlot = client.player.getInventory().getSelectedSlot();
            client.player.getInventory().setSelectedSlot(slot);
            ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
            throwing = true;
            restoreAt = now + (long) switchDelayMs;
            nextThrowAt = now + (long) throwDelayMs;
            return;
        }
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
        throwing = false;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}