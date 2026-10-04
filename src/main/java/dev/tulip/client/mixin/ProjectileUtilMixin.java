package dev.tulip.client.mixin;

import dev.tulip.client.module.HitboxesModule;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
    @Inject(
            method = "raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;",
            at = @At("HEAD")
    )
    private static void tulip$beginExpandedEntityRaycast(Entity source, Vec3d start, Vec3d end, Box box,
                                                          Predicate<Entity> predicate, double maxDistance,
                                                          CallbackInfoReturnable<EntityHitResult> cir) {
        HitboxesModule.beginRaycast();
    }

    @Inject(
            method = "raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;",
            at = @At("RETURN")
    )
    private static void tulip$endExpandedEntityRaycast(Entity source, Vec3d start, Vec3d end, Box box,
                                                        Predicate<Entity> predicate, double maxDistance,
                                                        CallbackInfoReturnable<EntityHitResult> cir) {
        HitboxesModule.endRaycast();
    }
}