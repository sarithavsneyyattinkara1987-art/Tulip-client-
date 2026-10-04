package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class AutoWebModule extends Module {
    private double minimumFallDistance = 2.0;
    private boolean restoreSlot = true;
    private int previousSlot = -1;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Fall Distance", () -> String.format(java.util.Locale.ROOT, "%.1f", minimumFallDistance),
                    () -> (minimumFallDistance - 1.0) / 9.0, value -> minimumFallDistance = 1.0 + value * 9.0),
            ModuleSetting.toggle("Restore slot", () -> restoreSlot, () -> restoreSlot = !restoreSlot)
    );

    public AutoWebModule() {
        super("Auto Web", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        if (client.player.isSpectator() || client.player.isCreative() || client.player.isOnGround()) return;

        int webSlot = findWebSlot(client);
        if (webSlot == -1) return;

        if (previousSlot < 0) previousSlot = client.player.getInventory().getSelectedSlot();
        if (client.player.getInventory().getSelectedSlot() != webSlot) {
            client.player.getInventory().setSelectedSlot(webSlot);
            return;
        }

        if (client.player.fallDistance < minimumFallDistance) return;
        BlockPos target = client.player.getBlockPos().down();
        if (!client.world.getBlockState(target).isAir()) return;
        if (client.player.getVelocity().y >= -0.25) return;

        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
    }

    private int findWebSlot(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.COBWEB)) return slot;
        }
        return -1;
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (restoreSlot && client.player != null && previousSlot >= 0) {
            client.player.getInventory().setSelectedSlot(previousSlot);
        }
        previousSlot = -1;
    }
}
