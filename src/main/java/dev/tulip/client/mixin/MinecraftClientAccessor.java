package dev.tulip.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("itemUseCooldown")
    void tulip$setItemUseCooldown(int cooldown);

    @Invoker("doItemUse")
    void tulip$invokeDoItemUse();

    @Invoker("doAttack")
    boolean tulip$invokeDoAttack();
}