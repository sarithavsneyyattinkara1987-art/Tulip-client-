package dev.tulip.client.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class ModuleSetting {
    private final String name;
    private final Supplier<String> value;
    private final BooleanSupplier visible;
    private final Runnable toggle;
    private final DoubleSupplier normalizedValue;
    private final DoubleConsumer setNormalizedValue;
    private final List<String> choices;
    private final Consumer<String> setChoice;
    private final KeybindState keybind;

    private ModuleSetting(String name, Supplier<String> value, BooleanSupplier visible, Runnable toggle,
                          DoubleSupplier normalizedValue, DoubleConsumer setNormalizedValue,
                          List<String> choices, Consumer<String> setChoice, KeybindState keybind) {
        this.name = name;
        this.value = value;
        this.visible = visible;
        this.toggle = toggle;
        this.normalizedValue = normalizedValue;
        this.setNormalizedValue = setNormalizedValue;
        this.choices = choices;
        this.setChoice = setChoice;
        this.keybind = keybind;
    }

    public static ModuleSetting toggle(String name, BooleanSupplier value, Runnable toggle) {
        return toggle(name, value, toggle, () -> true);
    }

    public static ModuleSetting toggle(String name, BooleanSupplier value, Runnable toggle, BooleanSupplier visible) {
        return new ModuleSetting(name, () -> value.getAsBoolean() ? "ON" : "OFF", visible, toggle, null, null,
            List.of(), null, null);
    }

    public static ModuleSetting slider(String name, Supplier<String> value, DoubleSupplier normalizedValue,
                                       DoubleConsumer setNormalizedValue) {
        return slider(name, value, normalizedValue, setNormalizedValue, () -> true);
    }

    public static ModuleSetting slider(String name, Supplier<String> value, DoubleSupplier normalizedValue,
                                       DoubleConsumer setNormalizedValue, BooleanSupplier visible) {
        return new ModuleSetting(name, value, visible, null, normalizedValue, setNormalizedValue, List.of(), null, null);
    }

    public static ModuleSetting choice(String name, Supplier<String> value, List<String> choices,
                                       Consumer<String> setChoice) {
        return new ModuleSetting(name, value, () -> true, null, null, null, List.copyOf(choices), setChoice, null);
    }

    public static ModuleSetting keybind(String name, int defaultKey) {
        KeybindState state = new KeybindState(defaultKey);
        return new ModuleSetting(name, state::label, () -> true, null, null, null, List.of(), null, state);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value.get();
    }

    public boolean isVisible() {
        return visible.getAsBoolean();
    }

    public boolean isToggle() {
        return toggle != null;
    }

    public boolean isChoice() {
        return !choices.isEmpty();
    }

    public boolean isKeybind() {
        return keybind != null;
    }

    public int getKeyCode() {
        return keybind == null ? GLFW.GLFW_KEY_UNKNOWN : keybind.keyCode;
    }

    public void setKeyCode(int keyCode) {
        if (keybind != null) keybind.keyCode = keyCode;
    }

    public boolean wasPressed() {
        return keybind != null && keybind.wasPressed();
    }

    public boolean isKeyDown() {
        return keybind != null && keybind.isDown();
    }

    public void cycleChoice() {
        if (!isChoice()) return;
        int index = choices.indexOf(value.get());
        setChoice.accept(choices.get((index + 1 + choices.size()) % choices.size()));
    }

    public void toggle() {
        if (toggle != null) toggle.run();
    }

    public double getNormalizedValue() {
        return normalizedValue == null ? 0.0 : normalizedValue.getAsDouble();
    }

    public void setNormalizedValue(double value) {
        if (setNormalizedValue != null) setNormalizedValue.accept(Math.clamp(value, 0.0, 1.0));
    }

    public JsonElement saveValue() {
        if (isToggle()) return new JsonPrimitive(getValue().equals("ON"));
        if (isChoice()) return new JsonPrimitive(getValue());
        if (isKeybind()) return new JsonPrimitive(getKeyCode());
        return new JsonPrimitive(getNormalizedValue());
    }

    public void loadValue(JsonElement savedValue) {
        if (savedValue == null || !savedValue.isJsonPrimitive()) return;
        JsonPrimitive primitive = savedValue.getAsJsonPrimitive();
        if (isToggle() && primitive.isBoolean()) {
            if (primitive.getAsBoolean() != getValue().equals("ON")) toggle();
        } else if (isChoice() && primitive.isString() && choices.contains(primitive.getAsString())) {
            setChoice.accept(primitive.getAsString());
        } else if (isKeybind() && primitive.isNumber()) {
            setKeyCode(primitive.getAsInt());
        } else if (!isToggle() && primitive.isNumber()) {
            setNormalizedValue(primitive.getAsDouble());
        }
    }

    private static final class KeybindState {
        private int keyCode;
        private boolean pressed;

        private KeybindState(int keyCode) {
            this.keyCode = keyCode;
        }

        private String label() {
            if (keyCode < 0) return "Mouse " + (-keyCode);
            if (keyCode == GLFW.GLFW_KEY_UNKNOWN) return "Unbound";
            String keyName = GLFW.glfwGetKeyName(keyCode, 0);
            return keyName == null ? "Key " + keyCode : keyName.toUpperCase(java.util.Locale.ROOT);
        }

        private boolean wasPressed() {
            boolean down = isDown();
            boolean justPressed = down && !pressed;
            pressed = down;
            return justPressed;
        }

        private boolean isDown() {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() == null || keyCode == GLFW.GLFW_KEY_UNKNOWN) return false;
            long window = client.getWindow().getHandle();
            return keyCode < 0
                    ? GLFW.glfwGetMouseButton(window, -keyCode - 1) == GLFW.GLFW_PRESS
                    : GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
        }
    }
}