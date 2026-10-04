package dev.tulip.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HitCobModule extends Module {
    public HitCobModule() {
        super("Hit Cob", ModuleCategory.COMBAT);
    }

    public static void onAttack(Entity attacked) {
        MinecraftClient client = MinecraftClient.getInstance();
        HitCobModule module = Module.getModule(HitCobModule.class).orElse(null);
        if (module == null || !module.isEnabled() || client.player == null || client.world == null
                || client.interactionManager == null || !(attacked instanceof PlayerEntity target)) return;

        int webSlot = findWebSlot(client);
        if (webSlot == -1) return;
        BlockPos targetFeet = target.getBlockPos();
        if (client.world.getBlockState(targetFeet).isOf(net.minecraft.block.Blocks.COBWEB)
                || client.world.getBlockState(targetFeet).isOf(net.minecraft.block.Blocks.WATER)) return;
        if (client.player.getEntityPos().distanceTo(Vec3d.ofCenter(targetFeet)) > 4.5) return;

        double targetSpeed = Math.hypot(target.getVelocity().x, target.getVelocity().z);
        Vec3d direction = target.getEntityPos().subtract(client.player.getEntityPos()).normalize();
        double lead = targetSpeed < 0.05 ? (client.player.isSprinting() ? 1.8 : 1.2)
                : targetSpeed < 0.13 ? (client.player.isSprinting() ? 1.5 : 1.0)
                : targetSpeed < 0.26 ? (client.player.isSprinting() ? 1.2 : 0.8)
                : (client.player.isSprinting() ? 1.0 : 0.7);
        BlockPos predictedFeet = BlockPos.ofFloored(target.getEntityPos().add(direction.multiply(lead)));
        if (client.world.getBlockState(predictedFeet).isOf(net.minecraft.block.Blocks.COBWEB)
                || client.world.getBlockState(predictedFeet).isOf(net.minecraft.block.Blocks.WATER)) return;
        if (!client.world.getBlockState(predictedFeet).isAir()) predictedFeet = targetFeet;
        BlockPos support = predictedFeet.down();
        if (client.world.getBlockState(support).isAir()
                || client.player.getEntityPos().distanceTo(Vec3d.ofCenter(predictedFeet)) > 4.5) return;

        int oldSlot = client.player.getInventory().getSelectedSlot();
        float oldYaw = client.player.getYaw();
        float oldPitch = client.player.getPitch();
        Vec3d hitPosition = Vec3d.ofCenter(support).add(0.0, 0.5, 0.0);
        Vec3d look = hitPosition.subtract(client.player.getEyePos());
        double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
        float yaw = (float) Math.toDegrees(Math.atan2(look.z, look.x)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(look.y, horizontal));

        try {
            client.player.setYaw(MathHelper.wrapDegrees(yaw));
            client.player.setPitch(MathHelper.clamp(pitch, -89.0F, 89.0F));
            client.player.getInventory().setSelectedSlot(webSlot);
            BlockHitResult hit = new BlockHitResult(hitPosition, Direction.UP, support, false);
            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
        } finally {
            client.player.getInventory().setSelectedSlot(oldSlot);
            client.player.setYaw(oldYaw);
            client.player.setPitch(oldPitch);
        }
    }

    private static int findWebSlot(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.COBWEB)) return slot;
        }
        return -1;
    }
}