package dev.tulip.client.module;

import dev.tulip.client.friends.FriendManager;
import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class ShieldBreakerModule extends Module {
    private double cps = 20.0;
    private double reactionDelayMs;
    private double swapDelayMs = 50.0;
    private double attackDelayMs = 50.0;
    private double swapBackDelayMs = 100.0;
    private boolean revertSlot = true;
    private boolean checkFacing = true;
    private boolean autoStun = true;
    private boolean disableIfUsingItem = true;
    private boolean ignoreFriends;
    private int previousSlot = -1;
    private int stage;
    private long nextActionAt;
    private long lastAttemptAt;
    private boolean replaying;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("CPS", () -> String.format(java.util.Locale.ROOT, "%.0f", cps),
                    () -> (cps - 1.0) / 19.0, value -> cps = 1.0 + value * 19.0),
            ModuleSetting.slider("Reaction Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", reactionDelayMs),
                    () -> reactionDelayMs / 250.0, value -> reactionDelayMs = value * 250.0),
            ModuleSetting.slider("Swap Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", swapDelayMs),
                    () -> swapDelayMs / 500.0, value -> swapDelayMs = value * 500.0),
            ModuleSetting.slider("Attack Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", attackDelayMs),
                    () -> attackDelayMs / 500.0, value -> attackDelayMs = value * 500.0),
            ModuleSetting.slider("Swap Back Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", swapBackDelayMs),
                    () -> swapBackDelayMs / 500.0, value -> swapBackDelayMs = value * 500.0),
            ModuleSetting.toggle("Revert Slot", () -> revertSlot, () -> revertSlot = !revertSlot),
            ModuleSetting.toggle("Check Facing", () -> checkFacing, () -> checkFacing = !checkFacing),
            ModuleSetting.toggle("Auto Stun", () -> autoStun, () -> autoStun = !autoStun),
            ModuleSetting.toggle("Disable if using item", () -> disableIfUsingItem,
                    () -> disableIfUsingItem = !disableIfUsingItem),
            ModuleSetting.toggle("Ignore Friends", () -> ignoreFriends, () -> ignoreFriends = !ignoreFriends)
    );

    public ShieldBreakerModule() {
        super("Shield Breaker", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static boolean beforeAttack(MinecraftClient client) {
        ShieldBreakerModule module = Module.getModule(ShieldBreakerModule.class).orElse(null);
        if (module == null || !module.isEnabled() || module.replaying) return false;
        if (module.previousSlot != -1) return true;
        PlayerEntity target = module.target(client);
        if (target == null || !module.eligible(client, target)) return false;
        long now = System.currentTimeMillis();
        if (now - module.lastAttemptAt < 1000.0 / module.cps) return false;
        if (client.player.isUsingItem() && module.disableIfUsingItem) return false;
        if (module.findAxe(client) == -1) return false;

        module.previousSlot = client.player.getInventory().getSelectedSlot();
        module.stage = 1;
        module.nextActionAt = now + (long) module.reactionDelayMs + (long) module.swapDelayMs;
        module.lastAttemptAt = now;
        return true;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || previousSlot == -1 || client.player == null || client.world == null) return;
        PlayerEntity target = target(client);
        if (target == null || !eligible(client, target)) {
            finish(client);
            return;
        }
        if (System.currentTimeMillis() < nextActionAt) return;

        if (stage == 1) {
            int axeSlot = findAxe(client);
            if (axeSlot == -1) {
                finish(client);
                return;
            }
            client.player.getInventory().setSelectedSlot(axeSlot);
            stage = 2;
            nextActionAt = System.currentTimeMillis() + (long) attackDelayMs;
        } else if (stage == 2) {
            replaying = true;
            try {
                ((MinecraftClientAccessor) client).tulip$invokeDoAttack();
                if (autoStun) ((MinecraftClientAccessor) client).tulip$invokeDoAttack();
            } finally {
                replaying = false;
            }
            stage = 3;
            nextActionAt = System.currentTimeMillis() + (long) swapBackDelayMs;
        } else if (stage == 3) {
            finish(client);
        }
    }

    private PlayerEntity target(MinecraftClient client) {
        if (client.currentScreen != null || !(client.crosshairTarget instanceof EntityHitResult hit)
                || !(hit.getEntity() instanceof PlayerEntity target) || target == client.player) return null;
        return target;
    }

    private boolean eligible(MinecraftClient client, PlayerEntity target) {
        if (!target.isBlocking() || !target.isHolding(Items.SHIELD)) return false;
        if (ignoreFriends && FriendManager.isFriend(target.getUuid())) return false;
        if (!checkFacing) return true;
        Vec3d towardAttacker = client.player.getEntityPos().subtract(target.getEntityPos()).normalize();
        return target.getRotationVec(1.0F).dotProduct(towardAttacker) > 0.3;
    }

    private int findAxe(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).getItem() instanceof AxeItem) return slot;
        }
        return -1;
    }

    private void finish(MinecraftClient client) {
        if (revertSlot && client.player != null && previousSlot >= 0) {
            client.player.getInventory().setSelectedSlot(previousSlot);
        }
        previousSlot = -1;
        stage = 0;
        nextActionAt = 0L;
    }

    @Override
    protected void onDisable() {
        finish(MinecraftClient.getInstance());
    }
}