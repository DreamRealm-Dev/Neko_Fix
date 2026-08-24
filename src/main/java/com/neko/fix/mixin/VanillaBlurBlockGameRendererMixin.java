package com.neko.fix.mixin;

import com.neko.fix.util.NekoRenderingUtils;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 替代 FancyMenu：GameRenderer.processBlurEffect 不再执行整屏高斯模糊。
 */
@Mixin(GameRenderer.class)
public abstract class VanillaBlurBlockGameRendererMixin {

    @Inject(method = "processBlurEffect", at = @At("HEAD"), cancellable = true)
    private void nekoFix$blockBlurEffect(float partialTicks, CallbackInfo ci) {
        if (NekoRenderingUtils.isVanillaMenuBlurringBlocked()) {
            ci.cancel();
        }
    }
}
