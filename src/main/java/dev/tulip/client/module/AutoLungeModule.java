package dev.tulip.client.module;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;

public class AutoLungeModule extends Module {
    private double switchBackDelayMs = 30.0;
    private int previousSlot = -1;
    private int lungeSlot = -1;
    private long restoreAt;
    private boolean wasAttacking;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Switch Back Delay", () -> String.format(Locale.ROOT, "%.0f ms", switchBackDelayMs),
                    () -> switchBackDelayMs / 200.0, value -> switchBackDelayMs = value * 200.0)
    );

    public AutoLungeModule() {
        super("Auto Lunge", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null) {
            restore(client);
            wasAttacking = false;
            return;
        }
        if (previousSlot >= 0 && System.currentTimeMillis() >= restoreAt) {
            if (client.player.getInventory().getSelectedSlot() == lungeSlot) client.player.getInventory().setSelectedSlot(previousSlot);
            clearSwap();
        }
        boolean attacking = client.options.attackKey.isPressed();
        if (attacking && !wasAttacking && previousSlot == -1) {
            int slot = findLungeSpear(client);
            int current = client.player.getInventory().getSelectedSlot();
            if (slot >= 0 && slot != current) {
                previousSlot = current;
                lungeSlot = slot;
                client.player.getInventory().setSelectedSlot(slot);
                restoreAt = System.currentTimeMillis() + (long) switchBackDelayMs;
            }
        }
        wasAttacking = attacking;
    }

    private int findLungeSpear(MinecraftClient client) {
        for (int slot = 0; slot < 9; slot++) {
            var stack = client.player.getInventory().getStack(slot);
            String path = Registries.ITEM.getId(stack.getItem()).getPath();
            boolean spear = path.equals("spear") || path.endsWith("_spear") || path.contains("spear");
            boolean lunge = stack.getEnchantments().getEnchantments().stream()
                    .anyMatch(enchantment -> enchantment.getIdAsString().toLowerCase(Locale.ROOT).contains("lunge"));
            if (spear && lunge) return slot;
        }
        return -1;
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && previousSlot >= 0 && client.player.getInventory().getSelectedSlot() == lungeSlot) {
            client.player.getInventory().setSelectedSlot(previousSlot);
        }
        clearSwap();
    }

    private void clearSwap() {
        previousSlot = -1;
        lungeSlot = -1;
        restoreAt = 0L;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
        wasAttacking = false;
    }
}