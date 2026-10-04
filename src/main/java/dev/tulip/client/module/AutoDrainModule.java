package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;

public class AutoDrainModule extends Module {
    private double cooldownMs = 250.0;
    private double switchBackMs = 75.0;
    private long lastDrainAt;
    private long switchBackAt;
    private int previousSlot = -1;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Cooldown MS", () -> String.format(java.util.Locale.ROOT, "%.0f ms", cooldownMs),
                    () -> (cooldownMs - 50.0) / 1950.0, value -> cooldownMs = 50.0 + value * 1950.0),
            ModuleSetting.slider("SwitchBack MS", () -> String.format(java.util.Locale.ROOT, "%.0f ms", switchBackMs),
                    () -> switchBackMs / 500.0, value -> switchBackMs = value * 500.0)
    );

    public AutoDrainModule() {
        super("Auto Drain", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        long now = System.currentTimeMillis();
        if (previousSlot != -1) {
            if (now >= switchBackAt) restore(client);
            return;
        }
        if (now - lastDrainAt < cooldownMs || !(client.crosshairTarget instanceof BlockHitResult hit)) return;
        var target = hit.getBlockPos();
        if (!isStillWater(client, target) && !isStillWater(client, target.offset(hit.getSide()))) return;
        if (insideCobweb(client)) return;

        int bucketSlot = findEmptyBucket(client);
        if (bucketSlot == -1) return;
        previousSlot = client.player.getInventory().getSelectedSlot();
        client.player.getInventory().setSelectedSlot(bucketSlot);
        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        lastDrainAt = now;
        switchBackAt = now + (long) switchBackMs;
    }

    private boolean isStillWater(MinecraftClient client, net.minecraft.util.math.BlockPos pos) {
        return client.world.getBlockState(pos).isOf(Blocks.WATER)
                && client.world.getFluidState(pos).getFluid() == Fluids.WATER
                && client.world.getFluidState(pos).isStill();
    }

    private int findEmptyBucket(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.BUCKET)) return slot;
        }
        return -1;
    }

    private boolean insideCobweb(MinecraftClient client) {
        var box = client.player.getBoundingBox();
        for (int x = (int) Math.floor(box.minX); x <= (int) Math.floor(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y <= (int) Math.floor(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z <= (int) Math.floor(box.maxZ); z++) {
                    if (client.world.getBlockState(new net.minecraft.util.math.BlockPos(x, y, z)).isOf(Blocks.COBWEB)) return true;
                }
            }
        }
        return false;
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}