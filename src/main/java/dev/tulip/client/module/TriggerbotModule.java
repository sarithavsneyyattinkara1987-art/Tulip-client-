package dev.tulip.client.module;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public class TriggerbotModule extends Module {
    private static final double MIN_THRESHOLD = 0.50;
    private static final double MAX_THRESHOLD = 1.25;

    private double minCooldown = 0.60;
    private double maxCooldown = 1.00;
    private double reach = 3.00;
    private double threshold = 1.00;
    private int overchargeTicks;
    private boolean extraDelay;
    private boolean rangeCheck;
    private boolean weaponsOnly = true;
    private boolean playersOnly = true;
    private boolean ignoreInvisible = true;
    private boolean criticalTiming = true;

    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Minimum cooldown", () -> percent(minCooldown),
                    () -> (minCooldown - MIN_THRESHOLD) / (MAX_THRESHOLD - MIN_THRESHOLD), this::setMinCooldown),
            ModuleSetting.slider("Maximum cooldown", () -> percent(maxCooldown),
                    () -> (maxCooldown - MIN_THRESHOLD) / ((extraDelay ? MAX_THRESHOLD : 1.0) - MIN_THRESHOLD), this::setMaxCooldown),
            ModuleSetting.toggle("Extra delay", () -> extraDelay, () -> {
                extraDelay = !extraDelay;
                if (!extraDelay) maxCooldown = Math.min(maxCooldown, 1.0);
            }),
            ModuleSetting.toggle("Range check", () -> rangeCheck, () -> rangeCheck = !rangeCheck),
            ModuleSetting.slider("Reach", () -> String.format(java.util.Locale.ROOT, "%.1f blocks", reach),
                    () -> (reach - 1.0) / 5.0, value -> reach = 1.0 + value * 5.0,
                    () -> rangeCheck),
            ModuleSetting.toggle("Weapons only", () -> weaponsOnly, () -> weaponsOnly = !weaponsOnly),
            ModuleSetting.toggle("Players only", () -> playersOnly, () -> playersOnly = !playersOnly),
            ModuleSetting.toggle("Ignore invisible", () -> ignoreInvisible, () -> ignoreInvisible = !ignoreInvisible),
            ModuleSetting.toggle("Critical timing", () -> criticalTiming, () -> criticalTiming = !criticalTiming)
    );

    public TriggerbotModule() {
        super("Triggerbot", ModuleCategory.COMBAT);
        rearm();
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    protected void onEnable() {
        rearm();
    }

    @Override
    protected void onDisable() {
        overchargeTicks = 0;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.interactionManager == null
                || client.currentScreen != null) return;

        var player = client.player;
        if (player.isUsingItem() || weaponsOnly && !holdsWeapon(player.getMainHandStack())) return;

        if (player.getAttackCooldownProgress(0.5f) >= 1.0f) overchargeTicks++;
        else overchargeTicks = 0;

        if (!(client.crosshairTarget instanceof EntityHitResult hit)
                || !(hit.getEntity() instanceof LivingEntity target)
                || !isValidTarget(player, target)) return;

        if (rangeCheck && hit.getPos().squaredDistanceTo(player.getEyePos()) > reach * reach) return;
        if (!isCharged(player.getAttackCooldownProgress(0.5f), player.getAttackCooldownProgressPerTick())) return;
        if (!canAttackNow(client, player)) return;

        client.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
        rearm();
    }

    private boolean isValidTarget(PlayerEntity player, LivingEntity target) {
        return target != player && target.isAlive() && !target.isSpectator()
                && (!ignoreInvisible || !target.isInvisible())
                && (!playersOnly || target instanceof PlayerEntity);
    }

    private boolean isCharged(float charge, float chargePerTick) {
        if (charge < Math.min(threshold, 1.0)) return false;
        int requiredOvercharge = (int) Math.ceil(Math.max(0.0, threshold - 1.0) / chargePerTick);
        return overchargeTicks >= requiredOvercharge;
    }

    private boolean canAttackNow(MinecraftClient client, PlayerEntity player) {
        if (!criticalTiming) return true;
        if (player.isOnGround()) return !client.options.jumpKey.isPressed();
        if (player.fallDistance <= 0.0 || player.isClimbing() || player.isTouchingWater() || player.hasVehicle()) return false;
        if (player.isSprinting()) {
            player.setSprinting(false);
            return false;
        }
        return true;
    }

    private boolean holdsWeapon(ItemStack stack) {
        return stack.isIn(ItemTags.WEAPON_ENCHANTABLE);
    }

    private void rearm() {
        double max = extraDelay ? maxCooldown : Math.min(maxCooldown, 1.0);
        threshold = minCooldown + ThreadLocalRandom.current().nextDouble() * Math.max(0.0, max - minCooldown);
        overchargeTicks = 0;
    }

    private void setMinCooldown(double normalized) {
        minCooldown = MIN_THRESHOLD + normalized * (MAX_THRESHOLD - MIN_THRESHOLD);
        minCooldown = Math.min(minCooldown, maxCooldown);
    }

    private void setMaxCooldown(double normalized) {
        double allowedMax = extraDelay ? MAX_THRESHOLD : 1.0;
        maxCooldown = MIN_THRESHOLD + normalized * (allowedMax - MIN_THRESHOLD);
        maxCooldown = Math.max(maxCooldown, minCooldown);
    }

    private static String percent(double value) {
        return Math.round(value * 100.0) + "%";
    }
}
