package dev.tulip.client.module;

import org.lwjgl.glfw.GLFW;

public class SnapTapModule extends Module {
    private static long lastA;
    private static long lastD;
    private static long lastW;
    private static long lastS;

    public SnapTapModule() {
        super("Snap Tap", ModuleCategory.MOVEMENT);
    }

    public static void onKeyPressed(int keyCode, boolean pressed) {
        long time = pressed ? System.currentTimeMillis() : 0L;
        switch (keyCode) {
            case GLFW.GLFW_KEY_A -> lastA = time;
            case GLFW.GLFW_KEY_D -> lastD = time;
            case GLFW.GLFW_KEY_W -> lastW = time;
            case GLFW.GLFW_KEY_S -> lastS = time;
        }
    }

    public static boolean isAllowed(int keyCode) {
        if (!Module.getModule(SnapTapModule.class).map(Module::isEnabled).orElse(false)) return true;
        return switch (keyCode) {
            case GLFW.GLFW_KEY_A -> lastA == 0L || lastD == 0L || lastD <= lastA;
            case GLFW.GLFW_KEY_D -> lastD == 0L || lastA == 0L || lastD >= lastA;
            case GLFW.GLFW_KEY_W -> lastW == 0L || lastS == 0L || lastW >= lastS;
            case GLFW.GLFW_KEY_S -> lastS == 0L || lastW == 0L || lastS >= lastW;
            default -> true;
        };
    }

    @Override
    protected void onDisable() {
        lastA = lastD = lastW = lastS = 0L;
    }
}