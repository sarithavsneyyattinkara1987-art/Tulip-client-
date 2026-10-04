package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ReBuffNotifierModule extends Module {
    private boolean soundAlert = true;
    private boolean showNotification = true;
    private double volume = 1.0;
    private boolean hadSpeed;
    private boolean hadStrength;
    private long lastAlertAt;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Sound Alert", () -> soundAlert, () -> soundAlert = !soundAlert),
            ModuleSetting.toggle("Show Notification", () -> showNotification, () -> showNotification = !showNotification),
            ModuleSetting.slider("Volume", () -> String.format(java.util.Locale.ROOT, "%.1f", volume),
                    () -> (volume - 0.1) / 1.9, value -> volume = 0.1 + value * 1.9)
    );

    public ReBuffNotifierModule() {
        super("ReBuff Notifier", ModuleCategory.PLAYER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null) return;
        boolean speed = client.player.hasStatusEffect(StatusEffects.SPEED);
        boolean strength = client.player.hasStatusEffect(StatusEffects.STRENGTH);
        if (hadSpeed && !speed) alert(client, "Speed");
        if (hadStrength && !strength) alert(client, "Strength");
        hadSpeed = speed;
        hadStrength = strength;
    }

    private void alert(MinecraftClient client, String effect) {
        long now = System.currentTimeMillis();
        if (now - lastAlertAt < 3000L) return;
        lastAlertAt = now;
        if (soundAlert) {
            client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), (float) volume));
        }
        if (showNotification && client.player != null) {
            client.player.sendMessage(Text.literal(effect + " effect expired"), true);
        }
    }

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        hadSpeed = client.player != null && client.player.hasStatusEffect(StatusEffects.SPEED);
        hadStrength = client.player != null && client.player.hasStatusEffect(StatusEffects.STRENGTH);
        lastAlertAt = 0L;
    }

    @Override
    protected void onDisable() {
        hadSpeed = false;
        hadStrength = false;
    }
}