package dev.tulip.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.tulip.client.friends.FriendManager;
import dev.tulip.client.module.Module;
import dev.tulip.client.module.ModuleSetting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClientConfig {
    private static final Logger LOGGER = Logger.getLogger("Adin");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("tulip-client.json");

    private ClientConfig() {
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        try {
            JsonElement rootElement = JsonParser.parseString(Files.readString(FILE));
            if (!rootElement.isJsonObject()) return;
            JsonObject root = rootElement.getAsJsonObject();
            FriendManager.load(root.get("friends"));
            if (!root.has("modules") || !root.get("modules").isJsonObject()) return;
            JsonObject modules = root.getAsJsonObject("modules");
            for (Module module : Module.getModules()) {
                String moduleId = module.getName().toLowerCase(Locale.ROOT);
                if (!modules.has(moduleId) || !modules.get(moduleId).isJsonObject()) continue;
                JsonObject savedModule = modules.getAsJsonObject(moduleId);
                if (savedModule.has("settings") && savedModule.get("settings").isJsonObject()) {
                    loadSettings(module, savedModule.getAsJsonObject("settings"));
                }
                if (savedModule.has("enabled") && savedModule.get("enabled").isJsonPrimitive()
                        && savedModule.getAsJsonPrimitive("enabled").isBoolean()) {
                    module.setEnabled(savedModule.get("enabled").getAsBoolean());
                }
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            LOGGER.log(Level.WARNING, "Unable to read Tulip configuration", exception);
        }
    }

    public static void save() {
        JsonObject modules = new JsonObject();
        for (Module module : Module.getModules()) {
            JsonObject savedModule = new JsonObject();
            savedModule.addProperty("enabled", module.isEnabled());
            JsonObject settings = new JsonObject();
            for (ModuleSetting setting : module.getSettings()) settings.add(setting.getName(), setting.saveValue());
            savedModule.add("settings", settings);
            modules.add(module.getName().toLowerCase(Locale.ROOT), savedModule);
        }
        JsonObject root = new JsonObject();
        root.add("modules", modules);
        root.add("friends", FriendManager.saveValue());

        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(root));
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Unable to save Tulip configuration", exception);
        }
    }

    private static void loadSettings(Module module, JsonObject savedSettings) {
        var settings = module.getSettings();
        settings.stream().filter(ModuleSetting::isToggle).forEach(setting -> loadSetting(setting, savedSettings));
        settings.stream().filter(ModuleSetting::isKeybind).forEach(setting -> loadSetting(setting, savedSettings));
        settings.stream().filter(setting -> setting.getName().equals("Maximum cooldown"))
                .forEach(setting -> loadSetting(setting, savedSettings));
        settings.stream().filter(setting -> !setting.isToggle() && !setting.getName().equals("Maximum cooldown"))
                .sorted(Comparator.comparing(ModuleSetting::getName)).forEach(setting -> loadSetting(setting, savedSettings));
    }

    private static void loadSetting(ModuleSetting setting, JsonObject savedSettings) {
        JsonElement value = savedSettings.get(setting.getName());
        if (value != null) setting.loadValue(value);
    }
}
