package com.neko.fix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 最终保底：在所有 getPose 注入之后，只要 sleepingPos 存在就强制 SLEEPING。
 * 使用低优先级，确保在 toNeko 的客户端 pose 覆盖之后执行。
 */
@Mixin(value = Entity.class, priority = 0)
public abstract class SleepingPoseFinalMixin {

    @Inject(method = "getPose", at = @At("RETURN"), cancellable = true)
    private void nekoFix$finalForceSleepingPose(CallbackInfoReturnable<Pose> cir) {
        if ((Object) this instanceof LivingEntity living && living.getSleepingPos().isPresent()) {
            cir.setReturnValue(Pose.SLEEPING);
        }
    }
}
