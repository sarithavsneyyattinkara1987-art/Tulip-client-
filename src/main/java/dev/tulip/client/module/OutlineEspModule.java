package dev.tulip.client.module;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class OutlineEspModule extends Module {
    private boolean showSelf;
    private boolean teamCheck;
    private boolean showPassives;
    private boolean showHostiles;
    private double range = 100.0;
    private final List<ModuleSetting> settings = List.of(
            ModuleSetting.toggle("Show Self", () -> showSelf, () -> showSelf = !showSelf),
            ModuleSetting.toggle("Team Check", () -> teamCheck, () -> teamCheck = !teamCheck),
            ModuleSetting.toggle("Show Passives", () -> showPassives, () -> showPassives = !showPassives),
            ModuleSetting.toggle("Show Hostiles", () -> showHostiles, () -> showHostiles = !showHostiles),
            ModuleSetting.slider("Range", () -> String.format(java.util.Locale.ROOT, "%.0f blocks", range),
                    () -> (range - 10.0) / 190.0, value -> range = 10.0 + value * 190.0)
    );

    public OutlineEspModule() {
        super("Outline ESP", ModuleCategory.RENDER);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return settings;
    }

    public static boolean shouldOutline(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        OutlineEspModule module = Module.getModule(OutlineEspModule.class).orElse(null);
        if (module == null || !module.isEnabled() || client.player == null || entity == null) return false;
        if (client.player.distanceTo(entity) > module.range) return false;
        if (entity instanceof PlayerEntity player) {
            if (player == client.player) return module.showSelf;
            return !module.teamCheck || !client.player.isTeammate(player);
        }
        if (entity instanceof PassiveEntity) return module.showPassives;
        return entity instanceof HostileEntity && module.showHostiles;
    }
}