package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Items;

public class AutoFireworkModule extends Module {
    private boolean onlyWhenFlying = true;
    private boolean respectGapples = true;
    private boolean respectArmor = true;
    private boolean autoSwitchBack = true;
    private double switchBackDelayMs = 100.0;
    private int previousSlot = -1;
    private long lastUseAt;
    private long switchBackAt;
    private boolean wasUsing;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Only When Flying", () -> onlyWhenFlying, () -> onlyWhenFlying = !onlyWhenFlying),
            ModuleSetting.toggle("Respect Gapples", () -> respectGapples, () -> respectGapples = !respectGapples),
            ModuleSetting.toggle("Respect Armor", () -> respectArmor, () -> respectArmor = !respectArmor),
            ModuleSetting.toggle("Auto Switch Back", () -> autoSwitchBack, () -> autoSwitchBack = !autoSwitchBack),
            ModuleSetting.slider("Switch Back Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", switchBackDelayMs),
                    () -> (switchBackDelayMs - 25.0) / 975.0, value -> switchBackDelayMs = 25.0 + value * 975.0)
    );

    public AutoFireworkModule() {
        super("Auto Firework", ModuleCategory.MOVEMENT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        long now = System.currentTimeMillis();

        if (previousSlot != -1 && now >= switchBackAt) restoreSlot(client);
        boolean using = client.options.useKey.isPressed();
        if (using && !wasUsing && now - lastUseAt >= 200 && canUse(client)) {
            int fireworkSlot = findFirework(client);
            if (fireworkSlot != -1) {
                previousSlot = client.player.getInventory().getSelectedSlot();
                client.player.getInventory().setSelectedSlot(fireworkSlot);
                ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
                lastUseAt = now;
                if (autoSwitchBack) switchBackAt = now + (long) switchBackDelayMs;
                else previousSlot = -1;
            }
        }
        wasUsing = using;
    }

    private boolean canUse(MinecraftClient client) {
        var player = client.player;
        var elytra = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!elytra.isOf(Items.ELYTRA) || elytra.getMaxDamage() > 0 && elytra.getDamage() >= elytra.getMaxDamage() - 1) return false;
        if (onlyWhenFlying && !player.isGliding()) return false;
        if (respectGapples && (isGapple(player.getMainHandStack()) || isGapple(player.getOffHandStack()))) return false;
        if (respectArmor && (isArmor(player.getMainHandStack()) || isArmor(player.getOffHandStack()))) return false;
        return true;
    }

    private boolean isGapple(net.minecraft.item.ItemStack stack) {
        return stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
    }

    private boolean isArmor(net.minecraft.item.ItemStack stack) {
        String name = stack.getItem().toString().toLowerCase(java.util.Locale.ROOT);
        return name.contains("helmet") || name.contains("chestplate") || name.contains("leggings") || name.contains("boots");
    }

    private int findFirework(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).getItem() instanceof FireworkRocketItem) return slot;
        }
        return -1;
    }

    private void restoreSlot(MinecraftClient client) {
        if (client.player != null && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
    }

    @Override
    protected void onDisable() {
        restoreSlot(MinecraftClient.getInstance());
        wasUsing = false;
    }
}