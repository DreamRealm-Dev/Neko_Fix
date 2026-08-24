package com.neko.fix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.cneko.toneko.common.mod.client.api.ClientEntityPoseManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 禁止 toNeko 客户端 PoseManager 在实体睡觉时写入非 SLEEPING 姿态。
 * 这是日志里“STANDING 覆盖 SLEEPING”的直接来源。
 */
@Mixin(ClientEntityPoseManager.class)
public abstract class ClientPoseSleepGuardMixin {

    @Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
    private static void nekoFix$blockNonSleepingPoseWhileSleeping(Entity entity, Pose pose, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && living.getSleepingPos().isPresent() && pose != Pose.SLEEPING) {
            ci.cancel();
        }
    }
}
