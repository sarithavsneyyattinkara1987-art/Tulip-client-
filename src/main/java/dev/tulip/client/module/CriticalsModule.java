package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.EntityHitResult;

public class CriticalsModule extends Module {
    private static final List<String> MODES = List.of("Vanilla", "Watchdog Old", "Mospixel");
    private String mode = "Vanilla";
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.choice("Mode", () -> mode, MODES, value -> mode = value)
    );

    public CriticalsModule() {
        super("Criticals", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static boolean canCritical(MinecraftClient client, Entity target) {
        if (client == null || client.player == null || target == null) return false;
        var player = client.player;
        if (player.isGliding() || player.isClimbing() || player.isTouchingWater() || player.hasVehicle()) {
            return false;
        }
        if (player.hasStatusEffect(StatusEffects.BLINDNESS)) {
            return false;
        }
        return target instanceof LivingEntity;
    }

    public static void beforeAttack(Entity target) {
        MinecraftClient client = MinecraftClient.getInstance();
        CriticalsModule module = Module.getModule(CriticalsModule.class).orElse(null);
        if (module == null || !module.isEnabled() || !canCritical(client, target)) return;
        var player = client.player;
        if (player == null || player.networkHandler == null) return;
        if (player.fallDistance > 0.0F && !player.isOnGround() && !player.isClimbing()
                && !player.isTouchingWater() && !player.hasVehicle()) return;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        switch (module.mode) {
            case "Watchdog Old" -> {
                if (player.isOnGround()) player.setPosition(x, y + 0.001D, z);
            }
            case "Mospixel" -> {
                player.networkHandler.sendPacket((Packet<?>) new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 2.71875E-7D, z, false, false));
                player.networkHandler.sendPacket((Packet<?>) new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, false));
            }
            default -> {
                player.networkHandler.sendPacket((Packet<?>) new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.2D, z, false, false));
                player.networkHandler.sendPacket((Packet<?>) new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.1D, z, false, false));
            }
        }
    }
}