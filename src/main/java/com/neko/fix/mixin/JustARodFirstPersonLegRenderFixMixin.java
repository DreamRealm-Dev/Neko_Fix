package com.neko.fix.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 LivingEntityRenderer 真正调用 model.renderToBuffer 之前强制隐藏本地玩家的腿/裤腿。
 *
 * 根因：PlayerAnimator 的 hideBonesInFirstPerson 会在第一人称 THIRD_PERSON_MODEL 渲染前
 * 把所有骨骼设为不可见，只保留手臂；但 JustARod 的 PlayerEntityModelMixin 在
 * PlayerModel.setupAnim 的 TAIL 又把腿/裤腿强制设为可见，导致第一人称下腿重新出现。
 *
 * 这里选在 renderToBuffer 调用前（即 setupAnim 全部执行完之后、模型真正渲染之前）
 * 再隐藏一次，既保留 Better Combat 的第一人称手臂动画，又能挡住 JustARod 强制显示的腿。
 */
@Mixin(LivingEntityRenderer.class)
public abstract class JustARodFirstPersonLegRenderFixMixin {

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/model/EntityModel.renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
            )
    )
    private void nekoFix$hideLegsBeforeRender(
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (!(entity instanceof Player player) || player != mc.player) return;

        EntityModel<?> model = ((LivingEntityRenderer<?, ?>) (Object) this).getModel();
        if (model instanceof PlayerModel<?> playerModel) {
            playerModel.leftLeg.visible = false;
            playerModel.rightLeg.visible = false;
            playerModel.leftPants.visible = false;
            playerModel.rightPants.visible = false;
        }
    }
}
