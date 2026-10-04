package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class AutoMlgModule extends Module {
    private double fallDistance = 8.0;
    private boolean pickUp = true;
    private int stage;
    private int stageTicks;
    private int previousSlot = -1;
    private float previousPitch;
    private boolean changedPitch;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Fall Distance", () -> String.format(java.util.Locale.ROOT, "%.0f blocks", fallDistance),
                    () -> (fallDistance - 3.0) / 37.0, value -> fallDistance = 3.0 + value * 37.0),
            ModuleSetting.toggle("Pick Up", () -> pickUp, () -> pickUp = !pickUp)
    );

    public AutoMlgModule() {
        super("Auto MLG", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        switch (stage) {
            case 0 -> beginFall(client);
            case 1 -> placeWater(client);
            case 2 -> pickWaterBack(client);
            case 3 -> restore(client);
            default -> restore(client);
        }
    }

    private void beginFall(MinecraftClient client) {
        var player = client.player;
        if (player.isOnGround() || player.isTouchingWater() || player.fallDistance < fallDistance) return;
        int bucket = findSlot(client, Items.WATER_BUCKET);
        if (bucket == -1) return;
        previousSlot = player.getInventory().getSelectedSlot();
        previousPitch = player.getPitch();
        player.getInventory().setSelectedSlot(bucket);
        stage = 1;
        stageTicks = 0;
        changedPitch = false;
    }

    private void placeWater(MinecraftClient client) {
        var player = client.player;
        if (player.isOnGround()) {
            stage = 3;
            return;
        }
        if (!changedPitch) {
            player.setPitch(89.5F);
            changedPitch = true;
            return;
        }
        stageTicks++;
        if (distanceToGround(client) > 2 || stageTicks % 2 != 0) return;
        if (!player.getMainHandStack().isOf(Items.WATER_BUCKET)) return;
        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        stage = pickUp ? 2 : 3;
        stageTicks = 0;
    }

    private void pickWaterBack(MinecraftClient client) {
        stageTicks++;
        if (!client.player.isOnGround() && !client.player.isTouchingWater() || stageTicks < 3) return;
        int emptyBucket = findSlot(client, Items.BUCKET);
        if (emptyBucket != -1) {
            client.player.getInventory().setSelectedSlot(emptyBucket);
            ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        }
        stage = 3;
    }

    private int distanceToGround(MinecraftClient client) {
        var position = client.player.getBlockPos();
        for (int distance = 1; distance <= 6; distance++) {
            if (!client.world.getBlockState(position.down(distance)).isAir()) return distance;
        }
        return 7;
    }

    private int findSlot(MinecraftClient client, net.minecraft.item.Item item) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(item)) return slot;
        }
        return -1;
    }

    private void restore(MinecraftClient client) {
        if (client.player != null) {
            if (previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
            if (changedPitch) client.player.setPitch(previousPitch);
        }
        stage = 0;
        stageTicks = 0;
        previousSlot = -1;
        changedPitch = false;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}