package dev.tulip.client.mixin;

import dev.tulip.client.module.FastMineModule;
import dev.tulip.client.module.KeepSprintModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F", at = @At("RETURN"), cancellable = true)
    private void tulip$modifyBreakingSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        float modified = FastMineModule.modifyBreakingSpeed((PlayerEntity) (Object) this, cir.getReturnValue());
        if (modified != cir.getReturnValue()) cir.setReturnValue(modified);
    }

    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void tulip$keepSprint(Entity target, CallbackInfo ci) {
        KeepSprintModule.afterAttack((PlayerEntity) (Object) this);
    }
}