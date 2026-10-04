package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.lwjgl.glfw.GLFW;

public class SwordHotswapModule extends Module {
    private final ModuleSetting hotswapKey = ModuleSetting.keybind("Hotswap Key", -GLFW.GLFW_MOUSE_BUTTON_RIGHT - 1);
    private double swapDelayMs = 150.0;
    private boolean switchBack = true;
    private int previousSlot = -1;
    private int shieldSlot = -1;
    private long switchAt;
    private final List<ModuleSetting> settings = List.of(
            hotswapKey,
            ModuleSetting.slider("Swap Delay (MS)", () -> String.format(java.util.Locale.ROOT, "%.0f ms", swapDelayMs),
                    () -> swapDelayMs / 1000.0, value -> swapDelayMs = value * 1000.0),
            ModuleSetting.toggle("Switch Back", () -> switchBack, () -> switchBack = !switchBack)
    );

    public SwordHotswapModule() {
        super("Sword Hotswap", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null) return;
        if (hotswapKey.wasPressed() && previousSlot == -1 && client.player.getMainHandStack().isIn(ItemTags.SWORDS)) {
            int foundShield = findShield(client);
            if (foundShield >= 0) {
                previousSlot = client.player.getInventory().getSelectedSlot();
                shieldSlot = foundShield;
                switchAt = System.currentTimeMillis() + (long) swapDelayMs;
            }
        }
        if (previousSlot == -1 || shieldSlot == -1 || System.currentTimeMillis() < switchAt) return;
        client.player.getInventory().setSelectedSlot(shieldSlot);
        shieldSlot = -1;
        if (switchBack && !hotswapKey.isKeyDown()) restore(client);
    }

    private int findShield(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.SHIELD)) return slot;
        }
        return -1;
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
        shieldSlot = -1;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}