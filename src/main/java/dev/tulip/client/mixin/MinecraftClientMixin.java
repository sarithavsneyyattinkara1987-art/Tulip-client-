package dev.tulip.client.mixin;

import dev.tulip.client.module.AntiMissModule;
import dev.tulip.client.module.STapModule;
import dev.tulip.client.module.SwordSwapModule;
import dev.tulip.client.module.ShieldBreakerModule;
import dev.tulip.client.module.TotemHitModule;
import dev.tulip.client.module.WTapModule;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doAttack()Z", at = @At("HEAD"), cancellable = true)
    private void tulip$prepareAttackModules(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (ShieldBreakerModule.beforeAttack(client)) {
            cir.setReturnValue(false);
            return;
        }
        TotemHitModule.beforeAttack(client);
        SwordSwapModule.beforeAttack(client);
        WTapModule.beforeAttack(client);
        STapModule.beforeAttack(client);
    }

    @Inject(method = "doAttack()Z", at = @At("HEAD"), cancellable = true)
    private void tulip$cancelMiss(CallbackInfoReturnable<Boolean> cir) {
        if (AntiMissModule.shouldCancelMiss((MinecraftClient) (Object) this)) cir.setReturnValue(false);
    }
}