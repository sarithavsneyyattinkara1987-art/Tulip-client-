package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.EntityHitResult;

public class TotemHitModule extends Module {
    private double switchDelayMs = 30.0;
    private int previousSlot = -1;
    private long restoreAt;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.slider("Switch Delay", () -> String.format(java.util.Locale.ROOT, "%.0f ms", switchDelayMs),
                    () -> (switchDelayMs - 10.0) / 90.0, value -> switchDelayMs = 10.0 + value * 90.0)
    );

    public TotemHitModule() {
        super("Totem Hit", ModuleCategory.COMBAT);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static void beforeAttack(MinecraftClient client) {
        TotemHitModule module = Module.getModule(TotemHitModule.class).orElse(null);
        if (module == null || !module.isEnabled() || client.player == null || client.currentScreen != null
                || !client.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)
                || !(client.crosshairTarget instanceof EntityHitResult)) return;
        if (module.previousSlot != -1) return;
        for (int slot = 0; slot < 9; slot++) {
            if (!client.player.getInventory().getStack(slot).isIn(ItemTags.SWORDS)) continue;
            module.previousSlot = client.player.getInventory().getSelectedSlot();
            client.player.getInventory().setSelectedSlot(slot);
            module.restoreAt = System.currentTimeMillis() + (long) module.switchDelayMs;
            return;
        }
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || previousSlot == -1 || client.player == null || System.currentTimeMillis() < restoreAt) return;
        restore(client);
    }

    private void restore(MinecraftClient client) {
        if (client.player != null && previousSlot != -1) client.player.getInventory().setSelectedSlot(previousSlot);
        previousSlot = -1;
        restoreAt = 0L;
    }

    @Override
    protected void onDisable() {
        restore(MinecraftClient.getInstance());
    }
}