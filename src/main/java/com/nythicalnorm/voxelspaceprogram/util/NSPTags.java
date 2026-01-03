package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class NSPTags {
    public static class Blocks {
        public static  final TagKey<Block> MAGNETIC_METALS = tag("magnetic_boots_metals");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(VoxelSpaceProgram.rl(name));
        }
    }
    public static class Items {
        private static TagKey<Item> tag(String name) {
            return ItemTags.create(VoxelSpaceProgram.rl(name));
        }
    }
}
