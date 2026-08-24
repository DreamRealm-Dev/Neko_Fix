package com.neko.fix.mixin;

import org.cneko.toneko.common.mod.entities.ai.goal.NekoSleepInBedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * toNeko 的 NekoSleepInBedGoal 在已经躺下后仍会每 tick 重复调用 startSleeping()，
 * 导致站/躺动画反复重启抽搐。这里在已经处于 SLEEPING 状态时跳过 tick。
 */
@Mixin(NekoSleepInBedGoal.class)
public abstract class NekoSleepInBedGoalMixin {

    @Invoker("isSleeping")
    protected abstract boolean nekoFix$isSleeping();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nekoFix$skipRepeatedSleepStart(CallbackInfo ci) {
        if (this.nekoFix$isSleeping()) {
            ci.cancel();
        }
    }
}
