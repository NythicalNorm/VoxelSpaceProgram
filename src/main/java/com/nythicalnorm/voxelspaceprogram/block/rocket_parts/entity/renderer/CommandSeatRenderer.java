package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.renderer;

import com.nythicalnorm.voxelspaceprogram.block.multiblock.MultiblockBERenderer;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.CommandSeatBE;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.CommandSeatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class CommandSeatRenderer extends MultiblockBERenderer<CommandSeatBE> {
    private static CommandSeatModel model;

    public CommandSeatRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(CommandSeatModel.LAYER_LOCATION);
        model = new CommandSeatModel(modelpart);
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
    protected void setModelPositions(CommandSeatBE blockEntity, ModelPart main, float pPartialTick) {

    }

    @Override
    public @Nullable Material getMaterial(Block block) {
        return CommandSeatModel.MATERIAL_LOCATION;
    }
}
