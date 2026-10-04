package dev.tulip.client.module;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;

public class AutoHeadHitterModule extends Module {
    private double jumpDelayMs = 100.0;
    private boolean holdingSpace;
    private long lastJumpAt;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Jump Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", jumpDelayMs),
                    () -> jumpDelayMs / 500.0, value -> jumpDelayMs = value * 500.0),
            ModuleSetting.toggle("Holding Space", () -> holdingSpace, () -> holdingSpace = !holdingSpace)
    );

    public AutoHeadHitterModule() {
        super("Auto Head Hitter", ModuleCategory.MOVEMENT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        if (holdingSpace && !client.options.jumpKey.isPressed()) return;
        if (!client.player.isOnGround() || System.currentTimeMillis() - lastJumpAt < jumpDelayMs) return;

        var above = client.world.getBlockState(client.player.getBlockPos().up(2));
        if (!above.isAir() && above.getBlock() != Blocks.WATER && above.getBlock() != Blocks.LAVA) {
            client.player.jump();
            lastJumpAt = System.currentTimeMillis();
        }
    }
}