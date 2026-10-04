package dev.tulip.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class KeepSprintModule extends Module {
    public KeepSprintModule() {
        super("Keep Sprint", ModuleCategory.MOVEMENT);
    }

    public static void afterAttack(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (player == client.player && Module.getModule(KeepSprintModule.class).map(Module::isEnabled).orElse(false)) {
            player.setSprinting(true);
        }
    }
}
