package dev.tulip.client.module;

import dev.tulip.client.friends.FriendManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssistModule extends Module {
    private double fov = 90.0;
    private double range = 5.0;
    private double speed = 10.0;
    private boolean targetPlayers = true;
    private boolean targetMobs;
    private boolean weaponsOnly;
    private boolean throughWalls;
    private boolean ignoreBlocks = true;
    private boolean onlyOutsideHitbox = true;
    private long lastUpdateAt;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("FOV", () -> String.format(java.util.Locale.ROOT, "%.0f", fov),
                    () -> (fov - 10.0) / 170.0, value -> fov = 10.0 + value * 170.0),
            ModuleSetting.slider("Range", () -> String.format(java.util.Locale.ROOT, "%.1f blocks", range),
                    () -> (range - 1.0) / 9.0, value -> range = 1.0 + value * 9.0),
            ModuleSetting.slider("Speed", () -> String.format(java.util.Locale.ROOT, "%.1f", speed),
                    () -> (speed - 1.0) / 14.0, value -> speed = 1.0 + value * 14.0),
            ModuleSetting.toggle("Target Players", () -> targetPlayers, () -> targetPlayers = !targetPlayers),
            ModuleSetting.toggle("Target Mobs", () -> targetMobs, () -> targetMobs = !targetMobs),
            ModuleSetting.toggle("Weapons Only", () -> weaponsOnly, () -> weaponsOnly = !weaponsOnly),
            ModuleSetting.toggle("Through Walls", () -> throughWalls, () -> throughWalls = !throughWalls),
            ModuleSetting.toggle("Ignore Blocks", () -> ignoreBlocks, () -> ignoreBlocks = !ignoreBlocks),
            ModuleSetting.toggle("Only Outside Hitbox", () -> onlyOutsideHitbox, () -> onlyOutsideHitbox = !onlyOutsideHitbox)
    );

    public AimAssistModule() {
        super("Aim Assist", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        if (weaponsOnly && !holdsWeapon(client)) return;
        if (client.crosshairTarget instanceof BlockHitResult && client.options.attackKey.isPressed()) return;
        if (ignoreBlocks && client.crosshairTarget instanceof BlockHitResult) return;

        Entity target = selectTarget(client);
        if (target == null) {
            lastUpdateAt = 0L;
            return;
        }
        if (!throughWalls && !client.player.canSee(target)) return;
        if (onlyOutsideHitbox && client.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() == target) return;

        Vec3d delta = target.getEyePos().subtract(client.player.getEyePos());
        float desiredYaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F);
        float desiredPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(delta.y, Math.sqrt(delta.x * delta.x + delta.z * delta.z))), -89.0F, 89.0F);
        long now = System.currentTimeMillis();
        if (lastUpdateAt == 0L) {
            lastUpdateAt = now;
            return;
        }
        float deltaSeconds = (now - lastUpdateAt) / 1000.0F;
        lastUpdateAt = now;
        if (deltaSeconds < 0.001F || deltaSeconds > 0.1F) return;
        float amount = MathHelper.clamp(deltaSeconds * (float) (speed / 10.0), 0.0F, 1.0F);
        float eased = easeOut(amount);
        client.player.setYaw(client.player.getYaw() + MathHelper.wrapDegrees(desiredYaw - client.player.getYaw()) * eased);
        client.player.setPitch(MathHelper.clamp(client.player.getPitch() + (desiredPitch - client.player.getPitch()) * eased, -89.0F, 89.0F));
    }

    private Entity selectTarget(MinecraftClient client) {
        Entity selected = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive() || entity == client.player) continue;
            if (!acceptsType(entity) || entity instanceof PlayerEntity player && FriendManager.isFriend(player.getUuid())) continue;
            double distance = client.player.distanceTo(entity);
            if (distance > range) continue;
            Vec3d aimPoint = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
            float[] rotation = rotationTo(client.player.getEyePos(), aimPoint);
            double angularDistance = Math.hypot(MathHelper.wrapDegrees(rotation[0] - client.player.getYaw()), rotation[1] - client.player.getPitch());
            if (angularDistance > fov / 2.0) continue;
            double score = distance + angularDistance * 2.0;
            if (score < bestScore) {
                selected = entity;
                bestScore = score;
            }
        }
        return selected;
    }

    private boolean acceptsType(Entity entity) {
        if (entity instanceof PlayerEntity) return targetPlayers;
        return targetMobs && !(entity instanceof PassiveEntity) && !(entity instanceof Tameable);
    }

    private float[] rotationTo(Vec3d origin, Vec3d destination) {
        Vec3d delta = destination.subtract(origin);
        double horizontal = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        return new float[]{MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F),
                MathHelper.clamp((float) -Math.toDegrees(Math.atan2(delta.y, horizontal)), -89.0F, 89.0F)};
    }

    private boolean holdsWeapon(MinecraftClient client) {
        var stack = client.player.getMainHandStack();
        return stack.getItem() instanceof AxeItem || stack.isIn(ItemTags.SWORDS);
    }

    private float easeOut(float value) {
        float x = MathHelper.clamp(value, 0.0F, 1.0F);
        float eased = 1.0F - (float) Math.pow(1.0F - x, 3.0);
        return eased * (1.0F + 2.70158F * (float) Math.pow(eased - 1.0F, 3.0)
                + 1.70158F * (float) Math.pow(eased - 1.0F, 2.0));
    }

    @Override
    protected void onDisable() {
        lastUpdateAt = 0L;
    }
}