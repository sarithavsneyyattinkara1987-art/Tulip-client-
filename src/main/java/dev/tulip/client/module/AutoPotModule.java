package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

public class AutoPotModule extends Module {
    private double healthThreshold = 10.0;
    private double throwCooldownMs = 250.0;
    private double rotationSpeed = 10.0;
    private double swapDelayMs = 50.0;
    private double minimumPlayerDistance;
    private boolean requireOnGround = true;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Health Threshold", () -> String.format(java.util.Locale.ROOT, "%.1f", healthThreshold),
                    () -> (healthThreshold - 1.0) / 19.0, value -> healthThreshold = 1.0 + value * 19.0),
            ModuleSetting.slider("Throw Cooldown", () -> String.format(java.util.Locale.ROOT, "%.0f ms", throwCooldownMs),
                    () -> (throwCooldownMs - 50.0) / 950.0, value -> throwCooldownMs = 50.0 + value * 950.0),
            ModuleSetting.slider("Rotation Speed", () -> String.format(java.util.Locale.ROOT, "%.1f", rotationSpeed),
                    () -> (rotationSpeed - 1.0) / 19.0, value -> rotationSpeed = 1.0 + value * 19.0),
            ModuleSetting.slider("Swap Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", swapDelayMs),
                    () -> swapDelayMs / 200.0, value -> swapDelayMs = value * 200.0),
            ModuleSetting.slider("Min Player Distance", () -> String.format(java.util.Locale.ROOT, "%.1f blocks", minimumPlayerDistance),
                    () -> minimumPlayerDistance / 10.0, value -> minimumPlayerDistance = value * 10.0),
            ModuleSetting.toggle("Require On Ground", () -> requireOnGround, () -> requireOnGround = !requireOnGround)
    );
    private final List<Integer> healingSlots = new ArrayList<>();
    private int previousSlot = -1;
    private float previousPitch;
    private float rotateProgress;
    private float rotateTarget;
    private boolean rotating;
    private boolean waitingToThrow;
    private long nextThrowAt;
    private long nextAttemptAt;

    public AutoPotModule() {
        super("Auto Pot", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null
                || client.player.isUsingItem()) return;
        if (rotating) {
            updateRotation(client);
            return;
        }
        if (waitingToThrow) {
            if (System.currentTimeMillis() >= nextThrowAt) throwHealingPotion(client);
            return;
        }
        if (client.player.getHealth() > healthThreshold || System.currentTimeMillis() < nextAttemptAt
                || requireOnGround && !client.player.isOnGround() || hasNearbyPlayer(client)) return;
        collectHealingPotions(client);
        if (healingSlots.isEmpty()) return;
        previousSlot = client.player.getInventory().getSelectedSlot();
        previousPitch = client.player.getPitch();
        startRotation(client, 89.9F);
    }

    private void startRotation(MinecraftClient client, float pitch) {
        rotateTarget = pitch;
        rotateProgress = 0.0F;
        rotating = true;
        updateRotation(client);
    }

    private void updateRotation(MinecraftClient client) {
        float amount = rotationSpeed <= 1.0 ? 1.0F : (float) ((rotationSpeed - 1.0) * 0.2);
        rotateProgress = Math.min(1.0F, rotateProgress + amount);
        float next = client.player.getPitch() + (rotateTarget - client.player.getPitch()) * rotateProgress;
        client.player.setPitch(next);
        if (rotateProgress >= 1.0F || Math.abs(rotateTarget - next) < 0.5F) {
            client.player.setPitch(rotateTarget);
            rotating = false;
            if (rotateTarget > 80.0F) {
                client.player.getInventory().setSelectedSlot(healingSlots.getFirst());
                waitingToThrow = true;
                nextThrowAt = System.currentTimeMillis() + (long) swapDelayMs;
            } else {
                restoreAfterUse(client);
            }
        }
    }

    private void throwHealingPotion(MinecraftClient client) {
        if (client.player == null || healingSlots.isEmpty()) {
            restore(client);
            return;
        }
        client.player.getInventory().setSelectedSlot(healingSlots.getFirst());
        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        waitingToThrow = false;
        nextAttemptAt = System.currentTimeMillis() + (long) throwCooldownMs;
        startRotation(client, previousPitch);
    }

    private void collectHealingPotions(MinecraftClient client) {
        healingSlots.clear();
        for (int slot = 0; slot < 9; slot++) {
            if (isHealingSplash(client.player.getInventory().getStack(slot))) healingSlots.add(slot);
        }
    }

    private boolean isHealingSplash(ItemStack stack) {
        if (!stack.isOf(Items.SPLASH_POTION)) return false;
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;
        boolean fromPotion = contents.potion().map(RegistryEntry::value).map(Potion::getEffects).orElse(List.of())
                .stream().anyMatch(effect -> effect.getEffectType().equals(StatusEffects.INSTANT_HEALTH));
        return fromPotion || contents.customEffects().stream()
                .anyMatch(effect -> effect.getEffectType().equals(StatusEffects.INSTANT_HEALTH));
    }

    private boolean hasNearbyPlayer(MinecraftClient client) {
        if (minimumPlayerDistance <= 0.0) return false;
        double rangeSquared = minimumPlayerDistance * minimumPlayerDistance;
        return client.world.getPlayers().stream().anyMatch(player -> player != client.player
                && player.squaredDistanceTo(client.player) < rangeSquared);
    }

    private void restoreAfterUse(MinecraftClient client) {
        if (previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
        healingSlots.clear();
        nextAttemptAt = System.currentTimeMillis() + (long) throwCooldownMs;
    }

    private void restore(MinecraftClient client) {
        rotating = false;
        waitingToThrow = false;
        if (client.player != null && previousSlot >= 0) {
            client.player.getInventory().setSelectedSlot(previousSlot);
            client.player.setPitch(previousPitch);
        }
        previousSlot = -1;
        healingSlots.clear();
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}