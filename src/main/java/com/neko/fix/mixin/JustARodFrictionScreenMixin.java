package com.neko.fix.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.cneko.justarod.client.screen.FrictionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import com.neko.fix.util.NekoRenderingUtils;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 同类 JustARod 界面：FrictionScreen 也去掉菜单背景模糊。
 */
@Pseudo
@Mixin(FrictionScreen.class)
public abstract class JustARodFrictionScreenMixin extends Screen {
    protected JustARodFrictionScreenMixin(Component title) {
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
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        int x0 = this.width / 2 - 130;
        int y0 = 35;
        int x1 = this.width / 2 + 130;
        int y1 = 175;
        graphics.fill(x0, y0, x1, y1, 0xFF101010);
    }
}
