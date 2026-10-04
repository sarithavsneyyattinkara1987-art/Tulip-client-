package dev.tulip.client.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private static final List<Module> MODULES = new ArrayList<>();

    private final String name;
    private final ModuleCategory category;
    private boolean enabled;

    protected Module(String name, ModuleCategory category) {
        this.name = name;
        this.category = category;
    }

    public static void register(Module module) {
        MODULES.add(module);
    }

    public static List<Module> getModules() {
        return List.copyOf(MODULES);
    }

    public static <T extends Module> Optional<T> getModule(Class<T> type) {
        return MODULES.stream().filter(type::isInstance).map(type::cast).findFirst();
    }

    public String getName() {
        return name;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public List<ModuleSetting> getSettings() {
        return List.of();
    }

    public void onTick(MinecraftClient client) {
    }
}
