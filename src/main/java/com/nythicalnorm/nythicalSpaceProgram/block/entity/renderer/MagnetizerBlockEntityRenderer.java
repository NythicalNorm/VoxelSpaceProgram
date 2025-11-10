package com.nythicalnorm.nythicalSpaceProgram.block.entity.renderer;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.nythicalnorm.nythicalSpaceProgram.block.entity.MagnetizerEntity;
import com.nythicalnorm.nythicalSpaceProgram.block.entity.models.MagnetizerModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class MagnetizerBlockEntityRenderer implements BlockEntityRenderer<MagnetizerEntity> {

    private static MagnetizerModels magnetizerModels;

    public MagnetizerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(MagnetizerModels.LAYER_LOCATION);
        magnetizerModels = new MagnetizerModels(modelpart);
    }

    @Override
    public void render(MagnetizerEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f,0.75f,0.5f);

        ItemStack InputitemStack = pBlockEntity.getInputStack();
        ItemStack OutputitemStack = pBlockEntity.getOutputStack();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getRenderFacing()));

        if (!InputitemStack.isEmpty() && InputitemStack.getCount() > 1) {
            drawItemStack(InputitemStack, pPoseStack, pBlockEntity, itemRenderer, pBuffer, 0.0f, -0.375f, 0f);
        }
        if (!OutputitemStack.isEmpty()) {
            drawItemStack(OutputitemStack, pPoseStack, pBlockEntity, itemRenderer, pBuffer, 0.0f, 0.375f, 0f);
        }

        float deltaTime = Minecraft.getInstance().getDeltaFrameTime();
        Vector3f centerItemPos = new Vector3f(0f, 0f, 0.0625f);
        float currRot = pBlockEntity.getMagnetTableYrot();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(currRot));
        boolean is_crafting = pBlockEntity.isCrafting();

        if (is_crafting) {
            currRot = currRot - deltaTime*5f;
            pBlockEntity.setMagnetTableYrot(currRot);
            drawItemStack(InputitemStack, pPoseStack, pBlockEntity, itemRenderer, pBuffer, centerItemPos.x, centerItemPos.y, centerItemPos.z);
        }

        if (magnetizerModels != null) {
            magnetizerModels.renderRotatingTable(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, is_crafting);
        }
        pPoseStack.popPose();

    }

    private void drawItemStack(ItemStack itemStack, PoseStack poseStack, BlockEntity blockEntity, ItemRenderer itemRenderer,
                          MultiBufferSource pBuffer, float x, float y, float z) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(270f));
        poseStack.translate(x, y, z);
        poseStack.scale(0.25f, 0.25f, 0.25f);

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, pBuffer, blockEntity.getLevel(), 1);
        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
