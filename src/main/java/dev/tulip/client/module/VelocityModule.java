package dev.tulip.client.module;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class VelocityModule extends Module {
    private double chance = 100.0;
    private boolean ignoreSprintKey = true;
    private boolean ignoreOnFire = true;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Chance (%)", () -> String.format(java.util.Locale.ROOT, "%.0f%%", chance),
                    () -> (chance - 1.0) / 99.0, value -> chance = 1.0 + value * 99.0),
            ModuleSetting.toggle("Ignore S press", () -> ignoreSprintKey, () -> ignoreSprintKey = !ignoreSprintKey),
            ModuleSetting.toggle("Ignore on fire", () -> ignoreOnFire, () -> ignoreOnFire = !ignoreOnFire)
    );

    public VelocityModule() {
        super("Velocity", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static void onVelocityPacket(EntityVelocityUpdateS2CPacket packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        VelocityModule module = Module.getModule(VelocityModule.class).orElse(null);
        if (module == null || !module.isEnabled() || client.player == null || client.currentScreen != null) return;
        if (packet.getEntityId() != client.player.getId() || !client.player.isOnGround()) return;
        if (module.ignoreSprintKey && client.options.backKey.isPressed()) return;
        if (module.ignoreOnFire && client.player.isOnFire()) return;
        if (ThreadLocalRandom.current().nextDouble(100.0) < module.chance) client.player.jump();
    }
}