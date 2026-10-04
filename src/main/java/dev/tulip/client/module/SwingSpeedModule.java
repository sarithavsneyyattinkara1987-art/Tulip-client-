package dev.tulip.client.module;

import java.util.List;
import net.minecraft.entity.LivingEntity;

public class SwingSpeedModule extends Module {
    private double duration = 12.0;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Swing Speed", () -> String.format(java.util.Locale.ROOT, "%.0f ticks", duration),
                    () -> (duration - 1.0) / 19.0, value -> duration = 1.0 + value * 19.0)
    );

    public SwingSpeedModule() {
        super("Swing Speed", ModuleCategory.RENDER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static int modifyDuration(LivingEntity entity, int original) {
        if (entity != net.minecraft.client.MinecraftClient.getInstance().player) return original;
        return Module.getModule(SwingSpeedModule.class).filter(Module::isEnabled)
                .map(module -> (int) module.duration).orElse(original);
    }
}