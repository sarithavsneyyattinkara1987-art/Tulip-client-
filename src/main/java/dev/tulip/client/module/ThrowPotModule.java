package dev.tulip.client.module;

import dev.tulip.client.mixin.MinecraftClientAccessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import org.lwjgl.glfw.GLFW;

public class ThrowPotModule extends Module {
    private final ModuleSetting throwKey = ModuleSetting.keybind("Throw Key", GLFW.GLFW_KEY_K);
    private double throwDelayMs = 250.0;
    private double healthThreshold = 10.0;
    private boolean multiThrow = true;
    private double potDelayMs = 150.0;
    private boolean autoSwitchBack = true;
    private boolean lookDown = true;
    private int previousSlot = -1;
    private float previousPitch;
    private int throwsRemaining;
    private long nextThrowAt;
    private long nextActivationAt;
    private boolean throwing;
    private final List<Integer> healingSlots = new ArrayList<>();
    private final List<ModuleSetting> settings = List.of(
            throwKey,
            ModuleSetting.slider("Throw Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", throwDelayMs),
                    () -> (throwDelayMs - 50.0) / 950.0, value -> throwDelayMs = 50.0 + value * 950.0),
            ModuleSetting.slider("Health Threshold", () -> String.format(java.util.Locale.ROOT, "%.1f", healthThreshold),
                    () -> (healthThreshold - 1.0) / 19.0, value -> healthThreshold = 1.0 + value * 19.0),
            ModuleSetting.toggle("Multi Throw", () -> multiThrow, () -> multiThrow = !multiThrow),
            ModuleSetting.slider("Pot Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", potDelayMs),
                    () -> (potDelayMs - 50.0) / 450.0, value -> potDelayMs = 50.0 + value * 450.0),
            ModuleSetting.toggle("Auto Switch Back", () -> autoSwitchBack, () -> autoSwitchBack = !autoSwitchBack),
            ModuleSetting.toggle("Look Down", () -> lookDown, () -> lookDown = !lookDown)
    );

    public ThrowPotModule() {
        super("Throw Pot", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null || client.currentScreen != null) return;
        if (throwing) {
            if (System.currentTimeMillis() >= nextThrowAt) throwNext(client);
            return;
        }
        if (!throwKey.wasPressed() || System.currentTimeMillis() < nextActivationAt
                || client.player.getHealth() > healthThreshold) return;
        collectHealingPotions(client);
        if (healingSlots.isEmpty()) return;
        previousSlot = client.player.getInventory().getSelectedSlot();
        previousPitch = client.player.getPitch();
        if (lookDown) client.player.setPitch(89.9F);
        throwsRemaining = multiThrow ? Math.min(3, healingSlots.size()) : 1;
        throwing = true;
        nextThrowAt = System.currentTimeMillis();
    }

    private void throwNext(MinecraftClient client) {
        if (throwsRemaining <= 0 || healingSlots.isEmpty()) {
            finish(client);
            return;
        }
        int slot = healingSlots.removeFirst();
        client.player.getInventory().setSelectedSlot(slot);
        ((MinecraftClientAccessor) client).tulip$invokeDoItemUse();
        throwsRemaining--;
        nextThrowAt = System.currentTimeMillis() + (long) potDelayMs;
        if (throwsRemaining == 0) {
            nextActivationAt = System.currentTimeMillis() + (long) throwDelayMs;
            finish(client);
        }
    }

    private void collectHealingPotions(MinecraftClient client) {
        healingSlots.clear();
        for (int slot = 0; slot < 9; slot++) {
            if (isHealingSplash(client.player.getInventory().getStack(slot))) healingSlots.add(slot);
        }
    }

    private boolean isHealingSplash(ItemStack stack) {
        if (!stack.isOf(Items.SPLASH_POTION)) return false;
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;
        boolean fromPotion = contents.potion().map(RegistryEntry::value).map(Potion::getEffects).orElse(List.of())
                .stream().anyMatch(effect -> effect.getEffectType().equals(StatusEffects.INSTANT_HEALTH));
        return fromPotion || contents.customEffects().stream()
                .anyMatch(effect -> effect.getEffectType().equals(StatusEffects.INSTANT_HEALTH));
    }

    private void finish(MinecraftClient client) {
        if (client.player != null) {
            if (autoSwitchBack && previousSlot >= 0) client.player.getInventory().setSelectedSlot(previousSlot);
            if (lookDown) client.player.setPitch(previousPitch);
        }
        previousSlot = -1;
        throwing = false;
        throwsRemaining = 0;
        healingSlots.clear();
    }

    @Override
    protected void onDisable() {
        finish(MinecraftClient.getInstance());
    }
}