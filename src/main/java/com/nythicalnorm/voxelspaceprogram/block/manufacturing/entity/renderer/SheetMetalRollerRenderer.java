package com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.renderer;

import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.SheetMetalRollerEntity;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.models.SheetMetalRollerModel;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.MultiblockBERenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class SheetMetalRollerRenderer extends MultiblockBERenderer<SheetMetalRollerEntity> {
    private static SheetMetalRollerModel model;

    public SheetMetalRollerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(SheetMetalRollerModel.LAYER_LOCATION);
        model = new SheetMetalRollerModel(modelpart);
    }

    @Override
    protected Vector3f getPostRotOffset(Block block) {
        return new Vector3f(0.0f, -1.0f, 0.0f);
    }

    @Override
    protected ModelPart getMainModel(Block block) {
        return model.getMain();
    }

    @Override
    protected void setModelPositions(SheetMetalRollerEntity blockEntity, ModelPart main, float pPartialTick) {

    }

    @Override
    public @Nullable Material getMaterial(Block block) {
        return SheetMetalRollerModel.MATERIAL_LOCATION;
    }

//    @Override
//    public void render(SheetMetalRollerEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
//                       MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
//        VertexConsumer vertexconsumer = SheetMetalRollerModel.MATERIAL_LOCATION.buffer(pBuffer, RenderType::entitySolid);
//
//        model.getMachine().render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
//        model.getPiston().render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
//        model.getWheel1().render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
//        model.getWheel2().render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
//    }
//
//    @Override
//    public void renderPreview(BlockState blockState, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, Material material, float[] color) {
//        VertexConsumer vertexconsumer = SheetMetalRollerModel.MATERIAL_LOCATION.buffer(pBuffer, RenderType::entityTranslucent);
//        model.getMain().render(pPoseStack, vertexconsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], color[3]);
//    }
//
//    @Override
//    public @Nullable Material getMaterial(Block block) {
//        return SheetMetalRollerModel.MATERIAL_LOCATION;
//    }
}
