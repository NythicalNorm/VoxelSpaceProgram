package com.nythicalnorm.voxelspaceprogram.Item;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.client.renderer.item.ItemProperties;

public class VSPItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(VSPItems.HANDHELD_PROPELLER.get(), VoxelSpaceProgram.rl( "inuse"),
                (pStack, pLevel, pEntity, pSeed) -> {
            if (pEntity == null) {
                return 0f;
            }
            if (pEntity.getUseItem().getItem() == VSPItems.HANDHELD_PROPELLER.get() && pEntity.isUsingItem()) { //getUseItem().getItem() == VSPItems.HANDHELD_PROPELLER.get()) {
                return 1f;
            }
            else {
                return 0f;
            }
        });
    }
}
