package com.nythicalnorm.voxelspaceprogram.Item;

import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MultiBlockItem extends BlockItem {
    public MultiBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties.requiredFeatures());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(VSPClientItemExtensions.INSTANCE);
    }

    public boolean renderBlockPreview() {
        return true;
    }

    public VSPMultiblock getMultiblock() {
        return (VSPMultiblock) this.getBlock();
    }

    public boolean canBePlacedWithPlayer(BlockPlaceContext pContext, BlockState pState) {
        return this.canPlace(pContext, pState);
    }
}
