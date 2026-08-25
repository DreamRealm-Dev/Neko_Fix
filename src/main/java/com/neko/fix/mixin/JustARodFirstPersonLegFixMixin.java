package com.neko.fix.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 JustARod 的 PlayerEntityModelMixin 在第一人称下把腿重新显示出来的问题。
 *
 * JustARod 会在 PlayerModel.setupAnim 的 TAIL 把腿/裤腿设为可见；
 * Better Combat 的 THIRD_PERSON_MODEL 第一人称模式会直接渲染这个模型，
 * 但不会设置 PlayerAnimator 的 firstPersonPass 标志。
 * 所以这里直接判断“当前相机是第一人称 + 正在渲染本地玩家”，
 * 在 TAIL 之后把腿强制隐藏，保留 Better Combat 第一人称动画。
 */
@Mixin(value = PlayerModel.class, priority = 0)
public abstract class JustARodFirstPersonLegFixMixin {

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("TAIL")
    )
    private void nekoFix$hideLegsInFirstPerson(
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci
    ) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (!(entity instanceof Player player) || player != mc.player) return;

        PlayerModel<?> model = (PlayerModel<?>) (Object) this;
        model.leftLeg.visible = false;
        model.rightLeg.visible = false;
        model.leftPants.visible = false;
        model.rightPants.visible = false;
    }
}
