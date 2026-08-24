package com.neko.fix.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.cneko.justarod.client.screen.JRSyncScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import com.neko.fix.util.NekoRenderingUtils;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * JustARod F10 状态菜单：取消原版 1.21+ 的菜单背景模糊，
 * 只绘制普通菜单背景，避免正文区域看起来被模糊。
 */
@Pseudo
@Mixin(JRSyncScreen.class)
public abstract class JustARodJRSyncScreenMixin extends Screen {
    protected JustARodJRSyncScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void nekoFix$blockBlurStart(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        NekoRenderingUtils.setVanillaMenuBlurringBlocked(true);
        NekoRenderingUtils.setOverrideBackgroundBlurRadius(0);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void nekoFix$blockBlurEnd(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        NekoRenderingUtils.setVanillaMenuBlurringBlocked(false);
        NekoRenderingUtils.resetOverrideBackgroundBlurRadius();
    }

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
    )
    private void nekoFix$renderBackgroundWithoutBlur(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 参考 mcbench 方案：完全不走原版背景/模糊，只画色块。
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        int x0 = this.width / 2 - 130;
        int y0 = 35;
        int x1 = this.width / 2 + 130;
        int y1 = 175;
        graphics.fill(x0, y0, x1, y1, 0xFF101010);
    }
}
