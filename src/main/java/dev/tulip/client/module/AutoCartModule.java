package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class AutoCartModule extends Module {
    private boolean autoSwitch = true;
    private boolean active;
    private int previousSlot = -1;
    private int stage;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Auto Switch", () -> autoSwitch, () -> autoSwitch = !autoSwitch)
    );

    public AutoCartModule() {
        super("Auto Cart", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        if (client.player.isUsingItem() && client.player.getActiveItem().getItem() instanceof BowItem) {
            if (!active && client.player.getMainHandStack().isOf(Items.BOW) && findRail(client) >= 0 && findCart(client) >= 0) {
                active = true;
                previousSlot = client.player.getInventory().getSelectedSlot();
                stage = 0;
            }
            return;
        }
        if (!active) return;
        if (stage == 0) {
            stage = 1;
            return;
        }
        if (stage == 1) {
            useItemInSlot(client, findRail(client));
            stage = 2;
            return;
        }
        useItemInSlot(client, findCart(client));
        if (autoSwitch && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        clear();
    }

    private void useItemInSlot(MinecraftClient client, int slot) {
        if (slot < 0) return;
        client.player.getInventory().setSelectedSlot(slot);
        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
    }

    private int findRail(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            var item = client.player.getInventory().getStack(slot).getItem();
            if (item == Items.RAIL || item == Items.POWERED_RAIL || item == Items.DETECTOR_RAIL || item == Items.ACTIVATOR_RAIL) return slot;
        }
        return -1;
    }

    private int findCart(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.TNT_MINECART)) return slot;
        }
        return -1;
    }

    private void clear() {
        active = false;
        previousSlot = -1;
        stage = 0;
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (autoSwitch && client.player != null && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        clear();
    }
}