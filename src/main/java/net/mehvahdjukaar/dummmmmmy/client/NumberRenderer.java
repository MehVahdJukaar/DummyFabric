package net.mehvahdjukaar.dummmmmmy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.entity.DummyNumberEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;

public class NumberRenderer extends EntityRenderer<DummyNumberEntity> {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public NumberRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityRendererRegistry.Context context) {
        super(entityRenderDispatcher);
    }


    @Override
    public void render(DummyNumberEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                       int packedLightIn) {
        Font fontrenderer = this.entityRenderDispatcher.getFont();
        matrixStackIn.pushPose();
        // translate towards player
        Player player = Minecraft.getInstance().player;


        //Vector3d v = (player.getPositionVec().subtract(entityIn.getPositionVec())).normalize();
       // matrixStackIn.translate(v.getX(), v.getY(), v.getZ());


        // animation
        matrixStackIn.translate(0, Mth.lerp(partialTicks, entityIn.prevDy, entityIn.dy), 0);
        // rotate towards camera
        double d = Math.sqrt(this.entityRenderDispatcher.distanceToSqr(entityIn.getX(), entityIn.getY(), entityIn.getZ()));


        float fadeout = Mth.lerp(partialTicks, entityIn.prevFadeout, entityIn.fadeout);

        float defscale = 0.006f;
        float scale = (float) (defscale * d);
        matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
        // matrixStackIn.translate(0, 0, -1);
        // animation
        matrixStackIn.translate(Mth.lerp(partialTicks, entityIn.prevDx, entityIn.dx),0, 0);
        // scale depending on distance so size remains the same
        matrixStackIn.scale(-scale, -scale, scale);
        matrixStackIn.translate(0, (4d*(1-fadeout)) , 0);
        matrixStackIn.scale(fadeout, fadeout, fadeout);
        matrixStackIn.translate(0,  -d / 10d, 0);

        float number = Configs.SHOW_HEARTHS? entityIn.getNumber()/2f : entityIn.getNumber();
        String s = df.format(number);
        // center string
        matrixStackIn.translate((-fontrenderer.width(s) / 2f) + 0.5f, 0, 0);
        fontrenderer.drawInBatch(s, 0, 0, entityIn.color, true, matrixStackIn.last().pose(), bufferIn, false, 0, packedLightIn);
        // matrixStackIn.translate(fontrenderer.getStringWidth(s) / 2, 0, 0);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(DummyNumberEntity entity) {
        return null;
    }
}
