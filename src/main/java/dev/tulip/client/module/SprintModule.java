package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class SprintModule extends Module {
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Sprint", this::isEnabled, this::toggle)
    );

    public SprintModule() {
        super("Sprint", ModuleCategory.MOVEMENT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null) return;

        PlayerEntity player = client.player;
        boolean moving = player.forwardSpeed > 0.0F || player.sidewaysSpeed != 0.0F;
        boolean shouldSprint = moving && !player.isSneaking() && !player.isSwimming() && !player.isClimbing() && !player.hasVehicle();
        player.setSprinting(shouldSprint);
    }
}
