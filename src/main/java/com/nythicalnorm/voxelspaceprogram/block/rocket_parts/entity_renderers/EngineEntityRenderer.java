package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity_renderers;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.MultiblockBERenderer;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.RocketryMultiblock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.EngineModelData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class EngineEntityRenderer extends MultiblockBERenderer<EngineEntity> {
    private static final Map<Block, EngineModel> EngineModelMap = new Object2ObjectOpenHashMap<>();
    public static final String GIMBALING_PART = "gimbaling";

    public EngineEntityRenderer(BlockEntityRendererProvider.Context context) {
        EngineModelMap.put(
                VSPBlocks.THREE_KEROLOX.get(),
                new EngineModel(context, EngineModelData.ThreeKeroloxLayerLoc, VoxelSpaceProgram.rl("block/three_kerolox"))
        );
        EngineModelMap.put(
                VSPBlocks.TWO_KEROLOX.get(),
                new EngineModel(context, EngineModelData.TwoKeroloxLayerLoc, VoxelSpaceProgram.rl("block/two_kerolox"))
        );
    }

    @Override
    public Material getMaterial(Block block) {
        EngineModel model = EngineModelMap.get(block);
        if (model != null) {
            return model.getMaterial();
        } else {
            return null;
        }
    }

    @Override
    public Vector3f getPostRotOffset(Block block) {
        if (block instanceof RocketryMultiblock rocketryMultiblock && rocketryMultiblock.isEvenBlockSize()) {
            return new Vector3f(0.5f, 0.0f, 0.5f);
        } else {
            return new Vector3f(0f, 1.0f, 0f);
        }
    }

    @Override
    protected ModelPart getMainModel(Block block) {
        EngineModel model = EngineModelMap.get(block);
        if (model != null) {
            return model.getFixed();
        } else {
            return null;
        }
    }

    @Override
    protected void setModelPositions(EngineEntity blockEntity, ModelPart main, float pPartialTick) {
        ModelPart gimbaling = main.getChild(GIMBALING_PART);
        gimbaling.xRot = blockEntity.getGimbalXrot();
        gimbaling.yRot = blockEntity.getGimbalYrot();
    }

    private static class EngineModel {
        private final ModelPart fixed;
        private final Material material;

        public EngineModel(BlockEntityRendererProvider.Context context, ModelLayerLocation layerLocation, ResourceLocation textureLocation) {
            ModelPart modelpart = context.bakeLayer(layerLocation);
            this.fixed = modelpart.getChild("fixed");
            this.material = new Material(InventoryMenu.BLOCK_ATLAS, textureLocation);
        }

        public ModelPart getFixed() {
            return fixed;
        }
        public Material getMaterial() {
            return material;
        }
    }
}
