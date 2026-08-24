package com.neko.fix.mixin;

import com.neko.fix.util.NekoRenderingUtils;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 替代 FancyMenu：菜单背景模糊强度临时覆盖为 0。
 */
@Mixin(Options.class)
public abstract class VanillaBlurRadiusMixin {

    @Inject(method = "getMenuBackgroundBlurriness", at = @At("RETURN"), cancellable = true)
    private void nekoFix$overrideBlurRadius(CallbackInfoReturnable<Integer> cir) {
        if (NekoRenderingUtils.shouldOverrideBackgroundBlurRadius()) {
            cir.setReturnValue(NekoRenderingUtils.getOverrideBackgroundBlurRadius());
        }
    }
}
