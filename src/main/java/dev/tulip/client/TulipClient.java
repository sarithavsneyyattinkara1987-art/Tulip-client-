package dev.tulip.client;

import dev.tulip.client.config.ClientConfig;
import dev.tulip.client.gui.AdinStyleScreen;
import dev.tulip.client.module.AutoToolModule;
import dev.tulip.client.module.ArrayListModule;
import dev.tulip.client.module.AntiMissModule;
import dev.tulip.client.module.AimAssistModule;
import dev.tulip.client.module.AutoLungeModule;
import dev.tulip.client.module.AutoPotModule;
import dev.tulip.client.module.AutoHeadHitterModule;
import dev.tulip.client.module.AutoDoubleHandModule;
import dev.tulip.client.module.AutoCartModule;
import dev.tulip.client.module.CartKeyModule;
import dev.tulip.client.module.AutoDrainModule;
import dev.tulip.client.module.AutoExtinguishModule;
import dev.tulip.client.module.AutoFireworkModule;
import dev.tulip.client.module.AutoMlgModule;
import dev.tulip.client.module.AutoWebModule;
import dev.tulip.client.module.CriticalsModule;
import dev.tulip.client.module.FastMineModule;
import dev.tulip.client.module.FastExpModule;
import dev.tulip.client.module.FastPlaceModule;
import dev.tulip.client.module.FullBrightModule;
import dev.tulip.client.module.FriendsModule;
import dev.tulip.client.module.ReBuffNotifierModule;
import dev.tulip.client.module.KeepSprintModule;
import dev.tulip.client.module.HitboxesModule;
import dev.tulip.client.module.MiddleClickFriendModule;
import dev.tulip.client.module.OutlineEspModule;
import dev.tulip.client.module.Module;
import dev.tulip.client.module.ModuleCategory;
import dev.tulip.client.module.SprintModule;
import dev.tulip.client.module.SwingSpeedModule;
import dev.tulip.client.module.TriggerbotModule;
import dev.tulip.client.module.WatermarkModule;
import dev.tulip.client.module.HitCobModule;
import dev.tulip.client.module.VelocityModule;
import dev.tulip.client.module.SwordSwapModule;
import dev.tulip.client.module.TotemHitModule;
import dev.tulip.client.module.WTapModule;
import dev.tulip.client.module.XbowCartModule;
import dev.tulip.client.module.ShieldBreakerModule;
import dev.tulip.client.module.SpearKillModule;
import dev.tulip.client.module.PearlKeyModule;
import dev.tulip.client.module.SnapTapModule;
import dev.tulip.client.module.STapModule;
import dev.tulip.client.module.SwordHotswapModule;
import dev.tulip.client.module.ThrowPotModule;
import dev.tulip.client.module.WindChargeKeyModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class TulipClient implements ClientModInitializer {
    public static final String MOD_ID = "tulip-client";
    public static final String MOD_NAME = "Adin";

    private static KeyBinding guiKeyBinding;
    private final TriggerbotModule triggerbot = new TriggerbotModule();

    @Override
    public void onInitializeClient() {
        Module.register(triggerbot);
        Module.register(new SprintModule());
        Module.register(new KeepSprintModule());
        Module.register(new AutoHeadHitterModule());
        Module.register(new AutoFireworkModule());
        Module.register(new AutoDoubleHandModule());
        Module.register(new AutoDrainModule());
        Module.register(new AutoExtinguishModule());
        Module.register(new AutoMlgModule());
        Module.register(new AutoWebModule());
        Module.register(new FastExpModule());
        Module.register(new ReBuffNotifierModule());
        Module.register(new FriendsModule());
        Module.register(new MiddleClickFriendModule());
        Module.register(new AutoToolModule());
        Module.register(new FastPlaceModule());
        Module.register(new FastMineModule());
        Module.register(new FullBrightModule());
        Module.register(new CriticalsModule());
        Module.register(new AimAssistModule());
        Module.register(new AutoLungeModule());
        Module.register(new SpearKillModule());
        Module.register(new STapModule());
        Module.register(new AutoCartModule());
        Module.register(new XbowCartModule());
        Module.register(new AutoPotModule());
        Module.register(new ThrowPotModule());
        Module.register(new ArrayListModule());
        Module.register(new WatermarkModule());
        Module.register(new SwingSpeedModule());
        Module.register(new HitboxesModule());
        Module.register(new OutlineEspModule());
        Module.register(new HitCobModule());
        Module.register(new VelocityModule());
        Module.register(new AntiMissModule());
        Module.register(new WTapModule());
        Module.register(new TotemHitModule());
        Module.register(new SwordSwapModule());
        Module.register(new ShieldBreakerModule());
        Module.register(new SwordHotswapModule());
        Module.register(new SnapTapModule());
        Module.register(new PearlKeyModule());
        Module.register(new WindChargeKeyModule());
        Module.register(new CartKeyModule());
        ClientConfig.load();

        HudElementRegistry.addLast(Identifier.of(MOD_ID, "vengeance_hud"), (context, tickCounter) -> {
            Module.getModule(WatermarkModule.class).ifPresent(module -> module.render(context));
            Module.getModule(ArrayListModule.class).ifPresent(module -> module.render(context));
        });

        guiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.adin.open_gui",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyBinding.Category.create(Identifier.of(MOD_ID, "client"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Module module : Module.getModules()) module.onTick(client);
            while (guiKeyBinding.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new AdinStyleScreen());
            }
        });
    }
}
