package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class FastMineModule extends Module {
    private double speed = 5.0;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Speed", () -> String.format(java.util.Locale.ROOT, "%.1f", speed),
                    () -> (speed - 1.0) / 9.0, value -> speed = 1.0 + value * 9.0)
    );

    public FastMineModule() {
        super("Fast Mine", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static float modifyBreakingSpeed(PlayerEntity player, float originalSpeed) {
        if (player != MinecraftClient.getInstance().player) return originalSpeed;
        double multiplier = Module.getModule(FastMineModule.class)
                .filter(Module::isEnabled).map(module -> module.speed).orElse(1.0);
        return (float) (originalSpeed * multiplier);
    }
}