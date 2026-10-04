package dev.tulip.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;

public class AntiMissModule extends Module {
    public AntiMissModule() {
        super("Anti Miss", ModuleCategory.COMBAT);
    }

    public static boolean shouldCancelMiss(MinecraftClient client) {
        return Module.getModule(AntiMissModule.class).filter(Module::isEnabled).isPresent()
                && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.MISS;
    }
}