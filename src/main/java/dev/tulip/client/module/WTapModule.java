package dev.tulip.client.module;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class WTapModule extends Module {
    private double chance = 100.0;
    private double delayMs = 60.0;
    private boolean onlyOnGround = true;
    private long restoreAt;
    private boolean releasedForward;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Chance (%)", () -> String.format(java.util.Locale.ROOT, "%.0f%%", chance),
                    () -> (chance - 1.0) / 99.0, value -> chance = 1.0 + value * 99.0),
            ModuleSetting.slider("Ms", () -> String.format(java.util.Locale.ROOT, "%.0f ms", delayMs),
                    () -> (delayMs - 1.0) / 499.0, value -> delayMs = 1.0 + value * 499.0),
            ModuleSetting.toggle("Only on ground", () -> onlyOnGround, () -> onlyOnGround = !onlyOnGround)
    );

    public WTapModule() {
        super("WTap", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static void beforeAttack(MinecraftClient client) {
        WTapModule module = Module.getModule(WTapModule.class).orElse(null);
        if (module == null || !module.isEnabled() || client.player == null || client.currentScreen != null
                || !(client.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult hit)
                || !hit.getEntity().isAlive() || module.onlyOnGround && !client.player.isOnGround()
                || !client.options.forwardKey.isPressed() || !client.player.isSprinting()
                || ThreadLocalRandom.current().nextDouble(100.0) >= module.chance) return;
        client.options.forwardKey.setPressed(false);
        module.releasedForward = true;
        module.restoreAt = System.currentTimeMillis() + (long) module.delayMs;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || !releasedForward || client.player == null || System.currentTimeMillis() < restoreAt) return;
        boolean keyHeld = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS;
        if (keyHeld) client.options.forwardKey.setPressed(true);
        releasedForward = false;
        restoreAt = 0L;
    }

    @Override
    protected void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (releasedForward && client.player != null
                && GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            client.options.forwardKey.setPressed(true);
        }
        releasedForward = false;
        restoreAt = 0L;
    }
}