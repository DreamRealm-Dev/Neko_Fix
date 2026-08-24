package com.neko.fix.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 只要姿态被锁成 SLEEPING，就强制 isSleeping() 返回 true。
 * 这样 LivingEntityRenderer 会稳定按躺姿旋转，不会因为 sleepingPos/pose 同步抖动而在站/躺之间切换。
 */
@Mixin(value = LivingEntity.class, priority = 10000)
public abstract class SleepingStateStabilizerMixin {

    @Inject(method = "isSleeping", at = @At("RETURN"), cancellable = true)
    private void nekoFix$forceSleepingState(CallbackInfoReturnable<Boolean> cir) {
        if (((LivingEntity) (Object) this).getPose() == Pose.SLEEPING) {
            cir.setReturnValue(true);
        }
    }
}
