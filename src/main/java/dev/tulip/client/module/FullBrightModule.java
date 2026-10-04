package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBrightModule extends Module {
    private double previousGamma = 0.5D;
    private boolean nightVision = true;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Night Vision", () -> nightVision, () -> nightVision = !nightVision)
    );

    public FullBrightModule() {
        super("Full Bright", ModuleCategory.RENDER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options == null) return;
        previousGamma = client.options.getGamma().getValue();
        client.options.getGamma().setValue(16.0D);
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.options == null) return;
        client.options.getGamma().setValue(16.0D);
        if (nightVision) {
            client.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, false, false));
        }
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options != null) {
            client.options.getGamma().setValue(previousGamma);
        }
    }
}
