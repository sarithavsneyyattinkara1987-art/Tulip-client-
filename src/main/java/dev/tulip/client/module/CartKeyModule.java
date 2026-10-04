package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class CartKeyModule extends Module {
    private final ModuleSetting key = ModuleSetting.keybind("Key", GLFW.GLFW_KEY_C);
    private double delayMs = 150.0;
    private boolean silent = true;
    private boolean switchBack = true;
    private double switchDelayMs = 250.0;
    private boolean autoBow = true;
    private double bowWindowMs = 1000.0;
    private int stage;
    private int previousSlot = -1;
    private long nextStepAt;
    private long bowStartedAt;
    private boolean useKeyWasDown;
    private final List<ModuleSetting> settings = List.of(
            key,
            ModuleSetting.slider("Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", delayMs),
                    () -> (delayMs - 50.0) / 450.0, value -> delayMs = 50.0 + value * 450.0),
            ModuleSetting.toggle("Silent", () -> silent, () -> silent = !silent),
            ModuleSetting.toggle("Switch Back", () -> switchBack, () -> switchBack = !switchBack),
            ModuleSetting.slider("Switch Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", switchDelayMs),
                    () -> (switchDelayMs - 100.0) / 900.0, value -> switchDelayMs = 100.0 + value * 900.0),
            ModuleSetting.toggle("Auto Bow", () -> autoBow, () -> autoBow = !autoBow),
            ModuleSetting.slider("Bow Window", () -> String.format(java.util.Locale.ROOT, "%.0f ms", bowWindowMs),
                    () -> (bowWindowMs - 500.0) / 1500.0, value -> bowWindowMs = 500.0 + value * 1500.0,
                    () -> autoBow)
    );

    public CartKeyModule() {
        super("Cart Key", ModuleCategory.MISC);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        long now = System.currentTimeMillis();
        boolean useDown = client.options.useKey.isPressed();
        if (stage == 0 && key.wasPressed() && validPlacement(client)) {
            previousSlot = client.player.getInventory().getSelectedSlot();
            stage = 1;
            nextStepAt = now + (long) delayMs;
        }
        if (stage == 1 && now >= nextStepAt) {
            useItem(client, findItem(client, Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL));
            stage = 2;
            nextStepAt = now + (long) delayMs;
        } else if (stage == 2 && now >= nextStepAt) {
            useItem(client, findItem(client, Items.TNT_MINECART));
            if (autoBow) {
                int bow = findBow(client);
                if (bow >= 0) {
                    client.player.getInventory().setSelectedSlot(bow);
                    stage = 3;
                    bowStartedAt = now;
                } else stage = switchBack ? 4 : 0;
            } else stage = switchBack ? 4 : 0;
            nextStepAt = now + (long) switchDelayMs;
        } else if (stage == 3) {
            if (useDown) bowStartedAt = now;
            else if (useKeyWasDown || now - bowStartedAt >= bowWindowMs) stage = switchBack ? 4 : 0;
        } else if (stage == 4 && now >= nextStepAt) {
            restore(client);
        }
        useKeyWasDown = useDown;
    }

    private boolean validPlacement(MinecraftClient client) {
        BlockPos target = placementPosition(client);
        return target != null && client.player.getEntityPos().distanceTo(target.toCenterPos()) <= 4.5
                && client.world.getBlockState(target).isAir()
                && findItem(client, Items.RAIL, Items.POWERED_RAIL, Items.DETECTOR_RAIL, Items.ACTIVATOR_RAIL) >= 0
                && findItem(client, Items.TNT_MINECART) >= 0;
    }

    private BlockPos placementPosition(MinecraftClient client) {
        if (client.crosshairTarget instanceof BlockHitResult hit) return hit.getBlockPos().offset(hit.getSide());
        if (client.crosshairTarget instanceof EntityHitResult) return client.player.getBlockPos().up();
        if (client.crosshairTarget != null && client.crosshairTarget.getType() != HitResult.Type.MISS) return null;
        var start = client.player.getCameraPosVec(1.0F);
        var end = start.add(client.player.getRotationVec(1.0F).multiply(5.0));
        var hit = client.world.raycast(new net.minecraft.world.RaycastContext(start, end,
                net.minecraft.world.RaycastContext.ShapeType.OUTLINE, net.minecraft.world.RaycastContext.FluidHandling.NONE, client.player));
        return hit == null ? client.player.getBlockPos().up() : hit.getBlockPos().offset(hit.getSide());
    }

    private int findItem(MinecraftClient client, Item... items) {
        for (int slot = 0; slot < 9; slot++) {
            Item held = client.player.getInventory().getStack(slot).getItem();
            for (Item item : items) if (held == item) return slot;
        }
        return -1;
    }

    private int findBow(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) if (client.player.getInventory().getStack(slot).getItem() instanceof BowItem) return slot;
        return -1;
    }

    private void useItem(MinecraftClient client, int slot) {
        if (slot < 0) return;
        if (silent) {
            int heldSlot = client.player.getInventory().getSelectedSlot();
            client.player.getInventory().setSelectedSlot(slot);
            ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
            client.player.getInventory().setSelectedSlot(heldSlot);
        } else {
            client.player.getInventory().setSelectedSlot(slot);
            ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        }
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
        stage = 0;
        previousSlot = -1;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
        useKeyWasDown = false;
    }
}