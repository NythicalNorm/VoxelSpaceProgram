package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.util.NSPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NSPBlockTagGenerator extends BlockTagsProvider {
    public NSPBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VoxelSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(NSPTags.Blocks.MAGNETIC_METALS).add(Blocks.IRON_BLOCK, Blocks.COPPER_BLOCK, Blocks.GOLD_BLOCK, Blocks.REDSTONE_BLOCK);

        this.tag(BlockTags.NEEDS_STONE_TOOL).add(NSPBlocks.CRYOGENIC_AIR_SEPARATOR.get(), NSPBlocks.MAGNETIZER.get(),
                NSPBlocks.MAGNETIZED_IRON_BLOCK.get(), NSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get(), NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(),
                NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(
                NSPBlocks.HYPERGOLIC_STEEL_TANK.get(),
                NSPBlocks.LUNAR_REGOLITH.get(),
                NSPBlocks.VEHICLE_ASSEMBLER.get(),
                NSPBlocks.THREE_KEROLOX.get(),
                NSPBlocks.TWO_KEROLOX.get(),
                NSPBlocks.BOUNDING_BLOCK.get(),
                NSPBlocks.ALUMINIUM_ORE.get(),
                NSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                NSPBlocks.ALUMINIUM_BLOCK.get(),
                NSPBlocks.RAW_ALUMINIUM_BLOCK.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                NSPBlocks.HYPERGOLIC_STEEL_TANK.get(),
                NSPBlocks.LUNAR_REGOLITH.get(),
                NSPBlocks.CRYOGENIC_AIR_SEPARATOR.get(),
                NSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get(),
                NSPBlocks.MAGNETIZER.get(),
                NSPBlocks.MAGNETIZED_IRON_BLOCK.get(),
                NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(),
                NSPBlocks.VEHICLE_ASSEMBLER.get(),
                NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get(),
                NSPBlocks.THREE_KEROLOX.get(),
                NSPBlocks.TWO_KEROLOX.get(),
                NSPBlocks.BOUNDING_BLOCK.get(),
                NSPBlocks.ALUMINIUM_ORE.get(),
                NSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                NSPBlocks.ALUMINIUM_BLOCK.get(),
                NSPBlocks.RAW_ALUMINIUM_BLOCK.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(NSPBlocks.LUNAR_REGOLITH.get());
    }
}
