package com.nythicalnorm.voxelspaceprogram.Item;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.BEItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class VSPClientItemExtensions implements IClientItemExtensions {
    public static VSPClientItemExtensions INSTANCE = new VSPClientItemExtensions();

    private final BEItemRenderer myBEWLR = new BEItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return myBEWLR;
    }
}
