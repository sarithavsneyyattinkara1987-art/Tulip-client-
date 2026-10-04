package dev.tulip.client.mixin;

import dev.tulip.client.module.CriticalsModule;
import dev.tulip.client.module.HitCobModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(
            method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V",
                    shift = At.Shift.AFTER
            )
    )
    private void tulip$beforeAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        CriticalsModule.beforeAttack(target);
        HitCobModule.onAttack(target);
    }
}