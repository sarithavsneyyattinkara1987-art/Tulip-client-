package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class AutoExtinguishModule extends Module {
    private double minDelayMs = 250.0;
    private double maxDelayMs = 330.0;
    private boolean pickUpAfter;
    private boolean rotateBack;
    private boolean returnToPreviousSlot;
    private int stage;
    private long nextActionAt;
    private int previousSlot = -1;
    private float previousPitch;
    private boolean changedPitch;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Min Delay MS", () -> String.format(java.util.Locale.ROOT, "%.0f", minDelayMs),
                    () -> minDelayMs / 1200.0, value -> minDelayMs = Math.min(value * 1200.0, maxDelayMs)),
            ModuleSetting.slider("Max Delay MS", () -> String.format(java.util.Locale.ROOT, "%.0f", maxDelayMs),
                    () -> maxDelayMs / 1200.0, value -> maxDelayMs = Math.max(value * 1200.0, minDelayMs)),
            ModuleSetting.toggle("Pick up after", () -> pickUpAfter, () -> pickUpAfter = !pickUpAfter),
            ModuleSetting.toggle("Rotate back", () -> rotateBack, () -> rotateBack = !rotateBack),
            ModuleSetting.toggle("Goto prev-slot", () -> returnToPreviousSlot,
                    () -> returnToPreviousSlot = !returnToPreviousSlot)
    );

    public AutoExtinguishModule() {
        super("Auto Extinguish", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        if (stage == 0) {
            if (client.player.isOnFire()) begin(client);
            return;
        }
        if (System.currentTimeMillis() < nextActionAt) return;

        switch (stage) {
            case 1 -> selectWaterBucket(client);
            case 2 -> aimDown(client);
            case 3 -> placeWater(client);
            case 4 -> pickWaterBack(client);
            case 5 -> restore(client);
            default -> restore(client);
        }
    }

    private void begin(MinecraftClient client) {
        if (findSlot(client, Items.WATER_BUCKET) == -1) return;
        previousSlot = client.player.getInventory().getSelectedSlot();
        previousPitch = client.player.getPitch();
        stage = 1;
        scheduleNextAction();
    }

    private void selectWaterBucket(MinecraftClient client) {
        int slot = findSlot(client, Items.WATER_BUCKET);
        if (slot == -1) {
            stage = 5;
            return;
        }
        client.player.getInventory().setSelectedSlot(slot);
        stage = 2;
        scheduleNextAction();
    }

    private void aimDown(MinecraftClient client) {
        client.player.setPitch(89.9F);
        changedPitch = true;
        stage = 3;
        nextActionAt = System.currentTimeMillis();
    }

    private void placeWater(MinecraftClient client) {
        if (!client.player.getMainHandStack().isOf(Items.WATER_BUCKET)) {
            stage = 5;
            return;
        }
        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        stage = pickUpAfter ? 4 : 5;
        nextActionAt = System.currentTimeMillis() + (pickUpAfter ? 100L : 0L);
    }

    private void pickWaterBack(MinecraftClient client) {
        if (client.player.getMainHandStack().isOf(Items.BUCKET)) {
            ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        }
        stage = 5;
    }

    private void scheduleNextAction() {
        double range = maxDelayMs - minDelayMs;
        double delay = range <= 0.0 ? minDelayMs : minDelayMs + ThreadLocalRandom.current().nextDouble(range);
        nextActionAt = System.currentTimeMillis() + (long) delay;
    }

    private int findSlot(MinecraftClient client, net.minecraft.item.Item item) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(item)) return slot;
        }
        return -1;
    }

    private void restore(MinecraftClient client) {
        if (client.player != null) {
            if (returnToPreviousSlot && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
            if (rotateBack && changedPitch) client.player.setPitch(previousPitch);
        }
        stage = 0;
        previousSlot = -1;
        changedPitch = false;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}