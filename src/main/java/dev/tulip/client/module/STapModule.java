package dev.tulip.client.module;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class STapModule extends Module {
    private double delayMs = 60.0;
    private double chance = 100.0;
    private boolean onlyOnGround = true;
    private boolean releasingBackKey;
    private boolean previousBackPhysical;
    private long releaseAt;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Ms", () -> String.format(java.util.Locale.ROOT, "%.0f ms", delayMs),
                    () -> (delayMs - 1.0) / 499.0, value -> delayMs = 1.0 + value * 499.0),
            ModuleSetting.slider("Chance (%)", () -> String.format(java.util.Locale.ROOT, "%.0f%%", chance),
                    () -> (chance - 1.0) / 99.0, value -> chance = 1.0 + value * 99.0),
            ModuleSetting.toggle("Only on ground", () -> onlyOnGround, () -> onlyOnGround = !onlyOnGround)
    );

    public STapModule() {
        super("STap", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static void beforeAttack(MinecraftClient client) {
        STapModule module = Module.getModule(STapModule.class).orElse(null);
        if (module == null || !module.isEnabled() || client.player == null || client.targetedEntity == null
                || !client.targetedEntity.isAlive() || module.onlyOnGround && !client.player.isOnGround()
                || !client.options.forwardKey.isPressed() || !client.player.isSprinting()
                || ThreadLocalRandom.current().nextDouble(100.0) >= module.chance) return;
        module.previousBackPhysical = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS;
        client.options.backKey.setPressed(true);
        module.releasingBackKey = true;
        module.releaseAt = System.currentTimeMillis() + (long) module.delayMs;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!releasingBackKey || client.player == null) return;
        if (System.currentTimeMillis() >= releaseAt) {
            boolean physicallyDown = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS;
            client.options.backKey.setPressed(physicallyDown || previousBackPhysical);
            releasingBackKey = false;
        }
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (releasingBackKey && client.player != null) client.options.backKey.setPressed(previousBackPhysical);
        releasingBackKey = false;
    }
}