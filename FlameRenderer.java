package com.superapp.dilucarmor.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.superapp.dilucarmor.DilucArmor;
import com.superapp.dilucarmor.FlameProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** An animated, full-bright, camera-facing phoenix sprite with distinct flapping wings. */
public final class FlameRenderer extends EntityRenderer<FlameProjectile> {
    private static final ResourceLocation FIREBALL = DilucArmor.id("textures/entity/fireball.png");
    private static final ResourceLocation[] PHOENIX = {
            DilucArmor.id("textures/entity/phoenix_0.png"),
            DilucArmor.id("textures/entity/phoenix_1.png")
    };

    public FlameRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0;
    }

    @Override public ResourceLocation getTextureLocation(FlameProjectile entity) {
        return entity.isPhoenix() ? PHOENIX[(entity.tickCount / 4) % 2] : FIREBALL;
    }

    @Override public void render(FlameProjectile entity, float yaw, float partialTick,
                                 PoseStack poses, MultiBufferSource buffers, int light) {
        poses.pushPose();
        poses.mulPose(entityRenderDispatcher.cameraOrientation());
        poses.mulPose(Axis.YP.rotationDegrees(180.0F));
        float scale = entity.isPhoenix() ? 3.2F : 0.55F;
        poses.scale(scale, scale, scale);
        PoseStack.Pose pose = poses.last();
        VertexConsumer vertices = buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        vertex(vertices, pose, -0.5F, -0.5F, 0, 1);
        vertex(vertices, pose,  0.5F, -0.5F, 1, 1);
        vertex(vertices, pose,  0.5F,  0.5F, 1, 0);
        vertex(vertices, pose, -0.5F,  0.5F, 0, 0);
        poses.popPose();
        super.render(entity, yaw, partialTick, poses, buffers, light);
    }

    private static void vertex(VertexConsumer out, PoseStack.Pose pose, float x, float y, float u, float v) {
        out.addVertex(pose, x, y, 0.0F).setColor(255, 255, 255, 255).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
