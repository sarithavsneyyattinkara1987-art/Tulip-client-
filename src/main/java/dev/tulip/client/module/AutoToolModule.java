package dev.tulip.client.module;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoToolModule extends Module {
    private boolean returnToPrevious = true;
    private boolean onlyWhenSneaking;
    private boolean preventLowDurability = true;
    private double delayMs = 5.0;
    private double durabilityThreshold = 10.0;
    private int previousSlot = -1;
    private long lastSwitchAt;
    private final List<ModuleSetting> settings = List.of(
        ModuleSetting.slider("Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", delayMs),
            () -> delayMs / 100.0, value -> delayMs = value * 100.0),
        ModuleSetting.toggle("Return To Previous", () -> returnToPrevious,
            () -> returnToPrevious = !returnToPrevious),
        ModuleSetting.toggle("Only When Sneaking", () -> onlyWhenSneaking,
            () -> onlyWhenSneaking = !onlyWhenSneaking),
        ModuleSetting.toggle("Prevent Low Durability", () -> preventLowDurability,
            () -> preventLowDurability = !preventLowDurability),
        ModuleSetting.slider("Durability Threshold", () -> String.format(java.util.Locale.ROOT, "%.0f", durabilityThreshold),
            () -> (durabilityThreshold - 1.0) / 99.0, value -> durabilityThreshold = 1.0 + value * 99.0,
            () -> preventLowDurability)
    );

    public AutoToolModule() {
        super("Auto Tool", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null) return;
        PlayerEntity player = client.player;
        if (onlyWhenSneaking && !player.isSneaking()) return;
        if (!client.options.attackKey.isPressed()) {
            restorePreviousSlot(player);
            return;
        }
        if (client.crosshairTarget == null) return;
        if (!(client.crosshairTarget instanceof BlockHitResult hitResult)) return;

        BlockState state = client.world.getBlockState(hitResult.getBlockPos());
        if (state.isAir() || state.getHardness(client.world, hitResult.getBlockPos()) < 0.0F) return;

        int bestSlot = -1;
        float bestSpeed = -1.0F;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty() || !(stack.isSuitableFor(state) || stack.getMiningSpeedMultiplier(state) > 1.0F)) continue;
            if (preventLowDurability && isLowDurability(stack)) continue;
            float speed = stack.getMiningSpeedMultiplier(state) * materialMultiplier(stack);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = slot;
            }
        }

        if (bestSlot >= 0 && bestSlot != player.getInventory().getSelectedSlot()
                && System.currentTimeMillis() - lastSwitchAt >= delayMs) {
            if (previousSlot == -1) previousSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(bestSlot);
            lastSwitchAt = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) restorePreviousSlot(client.player);
    }

    private void restorePreviousSlot(PlayerEntity player) {
        if (returnToPrevious && previousSlot != -1) player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
    }

    private boolean isLowDurability(ItemStack stack) {
        return stack.getMaxDamage() > 0 && stack.getMaxDamage() - stack.getDamage() <= durabilityThreshold;
    }

    private float materialMultiplier(ItemStack stack) {
        String itemName = stack.getItem().toString().toLowerCase(java.util.Locale.ROOT);
        if (itemName.contains("netherite")) return 6.0F;
        if (itemName.contains("diamond")) return 5.0F;
        if (itemName.contains("iron")) return 4.0F;
        if (itemName.contains("golden")) return 3.5F;
        if (itemName.contains("stone")) return 2.0F;
        if (itemName.contains("wooden")) return 1.5F;
        return 1.0F;
    }
}
