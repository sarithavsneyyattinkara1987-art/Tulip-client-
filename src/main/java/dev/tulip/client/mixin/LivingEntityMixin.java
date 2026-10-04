package dev.tulip.client.mixin;

import dev.tulip.client.module.SwingSpeedModule;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "getHandSwingDuration()I", at = @At("RETURN"), cancellable = true)
    private void tulip$modifySwingDuration(CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        int modified = SwingSpeedModule.modifyDuration((LivingEntity) (Object) this, original);
        if (modified != original) cir.setReturnValue(modified);
    }
}