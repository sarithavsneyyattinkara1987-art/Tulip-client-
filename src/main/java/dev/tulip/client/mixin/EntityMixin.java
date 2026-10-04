package dev.tulip.client.mixin;

import dev.tulip.client.module.HitboxesModule;
import dev.tulip.client.module.OutlineEspModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getBoundingBox()Lnet/minecraft/util/math/Box;", at = @At("RETURN"), cancellable = true)
    private void tulip$expandTargetBox(CallbackInfoReturnable<Box> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity target && MinecraftClient.getInstance().player != target) {
            double expansion = HitboxesModule.getExpansion(target);
            if (expansion > 0.0) cir.setReturnValue(cir.getReturnValue().expand(expansion));
        }
    }

    @Inject(method = "isGlowing()Z", at = @At("HEAD"), cancellable = true)
    private void tulip$outlineSelectedEntities(CallbackInfoReturnable<Boolean> cir) {
        if (OutlineEspModule.shouldOutline((Entity) (Object) this)) cir.setReturnValue(true);
    }
}