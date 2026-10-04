package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;

public class AutoDoubleHandModule extends Module {
    private boolean inventorySwitch = true;
    private double totemSlot = 9.0;
    private boolean healthSwitch;
    private double healthThreshold = 15.0;
    private int previousSlot = -1;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Inventory Switch", () -> inventorySwitch, () -> inventorySwitch = !inventorySwitch),
            ModuleSetting.slider("Totem Slot", () -> String.format(java.util.Locale.ROOT, "%.0f", totemSlot),
                    () -> (totemSlot - 1.0) / 8.0, value -> totemSlot = Math.round(1.0 + value * 8.0)),
            ModuleSetting.toggle("Health Switch", () -> healthSwitch, () -> healthSwitch = !healthSwitch),
            ModuleSetting.slider("Health Threshold", () -> String.format(java.util.Locale.ROOT, "%.1f", healthThreshold),
                    () -> (healthThreshold - 1.0) / 19.0, value -> healthThreshold = 1.0 + value * 19.0,
                    () -> healthSwitch)
    );

    public AutoDoubleHandModule() {
        super("Auto Double Hand", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        boolean inInventory = inventorySwitch && client.currentScreen instanceof InventoryScreen;
        boolean lowHealth = healthSwitch && client.player.getHealth() <= healthThreshold
                && !client.player.isUsingItem() && !client.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
        if (inInventory || lowHealth) {
            if (previousSlot == -1 && !client.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                int slot = findTotem(client);
                if (slot == -1 && inInventory) slot = (int) totemSlot - 1;
                if (slot != -1) {
                    previousSlot = client.player.getInventory().getSelectedSlot();
                    client.player.getInventory().setSelectedSlot(slot);
                }
            }
        } else restore(client);
    }

    private int findTotem(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.TOTEM_OF_UNDYING)) return slot;
        }
        return -1;
    }

    private void restore(MinecraftClient client) {
        if (previousSlot != -1 && client.player != null) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}