package com.neko.fix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 强制睡觉中的实体保持 SLEEPING 姿态。
 * 防止 toNeko 的 pose 同步/自定义姿态把站姿写回睡觉实体，造成站/躺反复抽搐。
 */
@Mixin(value = Entity.class, priority = 10000)
public abstract class SleepingPoseStabilizerMixin {

    @Inject(method = "getPose", at = @At("RETURN"), cancellable = true)
    private void nekoFix$forceSleepingPose(CallbackInfoReturnable<Pose> cir) {
        if ((Object) this instanceof LivingEntity living && living.getSleepingPos().isPresent()) {
            cir.setReturnValue(Pose.SLEEPING);
        }
    }

    @Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
    private void nekoFix$preventStandingWhileSleeping(Pose pose, CallbackInfo ci) {
        if ((Object) this instanceof LivingEntity living && living.getSleepingPos().isPresent() && pose != Pose.SLEEPING) {
            ((Entity) (Object) this).setPose(Pose.SLEEPING);
            ci.cancel();
        }
    }
}
