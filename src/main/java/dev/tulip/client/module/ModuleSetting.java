package dev.tulip.client.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.List;

public final class ModuleSetting {
    private final String name;
    private final Supplier<String> value;
    private final BooleanSupplier visible;
    private final Runnable toggle;
    private final DoubleSupplier normalizedValue;
    private final DoubleConsumer setNormalizedValue;
    private final List<String> choices;
    private final Consumer<String> setChoice;

    private ModuleSetting(String name, Supplier<String> value, BooleanSupplier visible, Runnable toggle,
                          DoubleSupplier normalizedValue, DoubleConsumer setNormalizedValue,
                          List<String> choices, Consumer<String> setChoice) {
        this.name = name;
        this.value = value;
        this.visible = visible;
        this.toggle = toggle;
        this.normalizedValue = normalizedValue;
        this.setNormalizedValue = setNormalizedValue;
        this.choices = choices;
        this.setChoice = setChoice;
    }

    public static ModuleSetting toggle(String name, BooleanSupplier value, Runnable toggle) {
        return toggle(name, value, toggle, () -> true);
    }

    public static ModuleSetting toggle(String name, BooleanSupplier value, Runnable toggle, BooleanSupplier visible) {
        return new ModuleSetting(name, () -> value.getAsBoolean() ? "ON" : "OFF", visible, toggle, null, null,
            List.of(), null);
    }

    public static ModuleSetting slider(String name, Supplier<String> value, DoubleSupplier normalizedValue,
                                       DoubleConsumer setNormalizedValue) {
        return slider(name, value, normalizedValue, setNormalizedValue, () -> true);
    }

    public static ModuleSetting slider(String name, Supplier<String> value, DoubleSupplier normalizedValue,
                                       DoubleConsumer setNormalizedValue, BooleanSupplier visible) {
        return new ModuleSetting(name, value, visible, null, normalizedValue, setNormalizedValue, List.of(), null);
    }

    public static ModuleSetting choice(String name, Supplier<String> value, List<String> choices,
                                       Consumer<String> setChoice) {
        return new ModuleSetting(name, value, () -> true, null, null, null, List.copyOf(choices), setChoice);
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
        return new JsonPrimitive(getNormalizedValue());
    }

    public void loadValue(JsonElement savedValue) {
        if (savedValue == null || !savedValue.isJsonPrimitive()) return;
        JsonPrimitive primitive = savedValue.getAsJsonPrimitive();
        if (isToggle() && primitive.isBoolean()) {
            if (primitive.getAsBoolean() != getValue().equals("ON")) toggle();
        } else if (isChoice() && primitive.isString() && choices.contains(primitive.getAsString())) {
            setChoice.accept(primitive.getAsString());
        } else if (!isToggle() && primitive.isNumber()) {
            setNormalizedValue(primitive.getAsDouble());
        }
    }
}