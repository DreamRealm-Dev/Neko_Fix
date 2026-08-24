package com.neko.fix.util;

/**
 * 自实现的“菜单模糊阻断”工具，替代 FancyMenu 的 RenderingUtils。
 * 只维护两个状态，实际阻断由 Neko_Fix 自己 Mixin 到原版 Screen / GameRenderer / Options 完成。
 */
public final class NekoRenderingUtils {
    private static int blurBlockDepth = 0;
    private static int overrideBackgroundBlurRadius = -1000;

    private NekoRenderingUtils() {
    }

    public static void setVanillaMenuBlurringBlocked(boolean blocked) {
        if (blocked) {
            blurBlockDepth++;
        } else if (blurBlockDepth > 0) {
            blurBlockDepth--;
        }
    }

    public static boolean isVanillaMenuBlurringBlocked() {
        return blurBlockDepth > 0;
    }

    public static void setOverrideBackgroundBlurRadius(int radius) {
        overrideBackgroundBlurRadius = radius;
    }

    public static void resetOverrideBackgroundBlurRadius() {
        overrideBackgroundBlurRadius = -1000;
    }

    public static boolean shouldOverrideBackgroundBlurRadius() {
        return overrideBackgroundBlurRadius != -1000;
    }

    public static int getOverrideBackgroundBlurRadius() {
        return overrideBackgroundBlurRadius;
    }
}
