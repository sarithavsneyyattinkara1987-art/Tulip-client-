package dev.tulip.client.module;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;

public class HitboxesModule extends Module {
    private static final ThreadLocal<Boolean> IN_ENTITY_RAYCAST = ThreadLocal.withInitial(() -> false);
    private double expansion = 0.3;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Expansion", () -> String.format(java.util.Locale.ROOT, "%.2f blocks", expansion),
                    () -> expansion / 2.0, value -> expansion = value * 2.0)
    );

    public HitboxesModule() {
        super("Hitboxes", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static void beginRaycast() {
        IN_ENTITY_RAYCAST.set(true);
    }

    public static void endRaycast() {
        IN_ENTITY_RAYCAST.set(false);
    }

    public static double getExpansion(PlayerEntity target) {
        if (!IN_ENTITY_RAYCAST.get() || target == net.minecraft.client.MinecraftClient.getInstance().player) return 0.0;
        return Module.getModule(HitboxesModule.class).filter(Module::isEnabled)
                .map(module -> module.expansion).orElse(0.0);
    }
}