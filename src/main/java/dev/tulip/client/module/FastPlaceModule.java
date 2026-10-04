package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import dev.tulip.client.mixin.MinecraftClientAccessor;

public class FastPlaceModule extends Module {
    private boolean blocksOnly = true;
    private double delayTicks;
    private long lastCooldownReset;
    private final List<ModuleSetting> settings = List.of(
        ModuleSetting.toggle("Blocks Only", () -> blocksOnly, () -> blocksOnly = !blocksOnly),
        ModuleSetting.slider("Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ticks", delayTicks),
            () -> delayTicks / 4.0, value -> delayTicks = Math.round(value * 4.0))
    );

    public FastPlaceModule() {
        super("Fast Place", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        var stack = client.player.getMainHandStack();
        if (blocksOnly && (stack.isEmpty() || !(stack.getItem() instanceof BlockItem))) return;
        long delayMs = (long) delayTicks * 50L;
        long now = System.currentTimeMillis();
        if (now - lastCooldownReset >= delayMs) {
            ((MinecraftClientAccessor) client).tulip$setItemUseCooldown(0);
            lastCooldownReset = now;
        }
    }

    @Override
    protected void onDisable() {
        ((MinecraftClientAccessor) MinecraftClient.getInstance()).tulip$setItemUseCooldown(4);
    }
}
