package dev.tulip.client.module;

import dev.tulip.client.friends.FriendManager;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpearKillModule extends Module {
    private boolean targetPlayersOnly = true;
    private boolean ignoreFriends = true;
    private double lungeStrength = 5.0;
    private double lockRange = 64.0;
    private double chargeTicks = 10.0;
    private int chargedTicks;
    private Entity target;
    private boolean launched;
    private float previousYaw;
    private float previousPitch;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Target Players Only", () -> targetPlayersOnly, () -> targetPlayersOnly = !targetPlayersOnly),
            ModuleSetting.toggle("Ignore Friends", () -> ignoreFriends, () -> ignoreFriends = !ignoreFriends),
            ModuleSetting.slider("Lunge Strength", () -> String.format(Locale.ROOT, "%.1f", lungeStrength),
                    () -> lungeStrength / 10.0, value -> lungeStrength = value * 10.0),
            ModuleSetting.slider("Lock Range", () -> String.format(Locale.ROOT, "%.0f blocks", lockRange),
                    () -> (lockRange - 1.0) / 255.0, value -> lockRange = 1.0 + value * 255.0),
            ModuleSetting.slider("Charge Ticks", () -> String.format(Locale.ROOT, "%.0f", chargeTicks),
                    () -> (chargeTicks - 1.0) / 39.0, value -> chargeTicks = 1.0 + value * 39.0)
    );

    public SpearKillModule() {
        super("SpearKill", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null
                || !client.options.useKey.isPressed() || !isHoldingSpear(client)) {
            reset(client, true);
            return;
        }
        chargedTicks++;
        if (target == null || !target.isAlive()) target = chooseTarget(client);
        if (!(target instanceof LivingEntity living) || !living.isAlive() || !accepts(client, living)) return;
        aimAt(client, living);
        if (!launched && chargedTicks >= chargeTicks) {
            Vec3d velocity = Vec3d.fromPolar(client.player.getPitch(), client.player.getYaw());
            client.player.setSprinting(true);
            client.player.setVelocity(velocity.multiply(lungeStrength));
            launched = true;
        }
    }

    private Entity chooseTarget(MinecraftClient client) {
        Entity crosshair = client.targetedEntity;
        if (crosshair instanceof LivingEntity living && accepts(client, living)) return living;
        Vec3d view = client.player.getRotationVec(1.0F).normalize();
        return StreamSupport.stream(client.world.getEntities().spliterator(), false).filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity).filter(entity -> accepts(client, entity))
                .filter(entity -> entity.squaredDistanceTo(client.player) <= lockRange * lockRange)
                .max((first, second) -> Double.compare(targetScore(client, first, view), targetScore(client, second, view)))
                .orElse(null);
    }

    private boolean accepts(MinecraftClient client, LivingEntity entity) {
        if (entity == client.player || !entity.isAlive() || entity.squaredDistanceTo(client.player) > lockRange * lockRange) return false;
        if (targetPlayersOnly && !(entity instanceof PlayerEntity)) return false;
        if (!targetPlayersOnly && (entity instanceof PassiveEntity || entity instanceof Tameable)) return false;
        if (entity instanceof PlayerEntity player && ignoreFriends && FriendManager.isFriend(player.getUuid())) return false;
        return !client.player.isTeammate(entity);
    }

    private double targetScore(MinecraftClient client, Entity entity, Vec3d view) {
        Vec3d direction = entity.getBoundingBox().getCenter().subtract(client.player.getEyePos()).normalize();
        return view.dotProduct(direction) - client.player.getEyePos().squaredDistanceTo(entity.getBoundingBox().getCenter()) * 5.0E-4;
    }

    private void aimAt(MinecraftClient client, Entity entity) {
        if (chargedTicks == 1) {
            previousYaw = client.player.getYaw();
            previousPitch = client.player.getPitch();
        }
        Vec3d delta = entity.getBoundingBox().getCenter().subtract(client.player.getEyePos()).normalize();
        float yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F);
        float pitch = MathHelper.clamp((float) -Math.toDegrees(Math.asin(delta.y)), -89.0F, 89.0F);
        client.player.setYaw(yaw);
        client.player.setHeadYaw(yaw);
        client.player.setPitch(pitch);
    }

    private boolean isHoldingSpear(MinecraftClient client) {
        return isSpear(client.player.getMainHandStack().getItem()) || isSpear(client.player.getOffHandStack().getItem());
    }

    private boolean isSpear(net.minecraft.item.Item item) {
        String path = Registries.ITEM.getId(item).getPath();
        return path.equals("spear") || path.endsWith("_spear") || path.contains("spear");
    }

    private void reset(MinecraftClient client, boolean restoreRotation) {
        if (restoreRotation && chargedTicks > 0 && !launched && client.player != null) {
            client.player.setYaw(previousYaw);
            client.player.setHeadYaw(previousYaw);
            client.player.setPitch(previousPitch);
        }
        chargedTicks = 0;
        target = null;
        launched = false;
    }

    @Override
    protected void onDisable() {
        reset(MinecraftClient.getInstance(), true);
    }
}