package dev.tulip.client.mixin;

import dev.tulip.client.module.SnapTapModule;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Shadow @Final private InputUtil.Key defaultKey;

    @Inject(method = "setPressed(Z)V", at = @At("HEAD"))
    private void tulip$recordMovementPress(boolean pressed, CallbackInfo ci) {
        SnapTapModule.onKeyPressed(defaultKey.getCode(), pressed);
    }

    @Inject(method = "isPressed()Z", at = @At("HEAD"), cancellable = true)
    private void tulip$resolveSnapTap(CallbackInfoReturnable<Boolean> cir) {
        int code = defaultKey.getCode();
        if ((code == 65 || code == 68 || code == 87 || code == 83) && !SnapTapModule.isAllowed(code)) {
            cir.setReturnValue(false);
        }
    }
}