package com.neko.fix.mixin;

import com.neko.fix.util.NekoRenderingUtils;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 替代 FancyMenu：Screen.renderBlurredBackground 不再绘制模糊背景。
 */
@Mixin(Screen.class)
public abstract class VanillaBlurBlockScreenMixin {

    @Inject(method = "renderBlurredBackground", at = @At("HEAD"), cancellable = true)
    private void nekoFix$blockVanillaBlur(float partialTick, CallbackInfo ci) {
        if (NekoRenderingUtils.isVanillaMenuBlurringBlocked()) {
            ci.cancel();
        }
    }
}
