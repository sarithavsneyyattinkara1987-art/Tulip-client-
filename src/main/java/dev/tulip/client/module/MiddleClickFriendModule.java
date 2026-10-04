package dev.tulip.client.module;

import dev.tulip.client.config.ClientConfig;
import dev.tulip.client.friends.FriendManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class MiddleClickFriendModule extends Module {
    private boolean wasPressed;

    public MiddleClickFriendModule() {
        super("Middle Click Friend", ModuleCategory.MISC);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return List.of();
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.currentScreen != null) {
            wasPressed = false;
            return;
        }
        boolean pressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
                == GLFW.GLFW_PRESS;
        if (pressed && !wasPressed && client.crosshairTarget instanceof EntityHitResult hit
                && hit.getEntity() instanceof PlayerEntity target && target != client.player) {
            boolean added = FriendManager.toggle(target.getUuid(), target.getName().getString());
            ClientConfig.save();
            client.player.sendMessage(Text.literal(target.getName().getString()
                    + (added ? " added to friends" : " removed from friends")), false);
        }
        wasPressed = pressed;
    }
}