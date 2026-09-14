package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.util.VSPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class VSPBlockTagGenerator extends BlockTagsProvider {
    public VSPBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VoxelSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(VSPTags.Blocks.MAGNETIC_METALS).add(Blocks.IRON_BLOCK, Blocks.COPPER_BLOCK, Blocks.GOLD_BLOCK, Blocks.REDSTONE_BLOCK);

        this.tag(BlockTags.NEEDS_STONE_TOOL).add(
                VSPBlocks.MACHINERY_BOUNDING_BLOCK.get(),
                VSPBlocks.SHEET_METAL_ROLLER.get(),
                VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get(),
                VSPBlocks.MAGNETIZER.get(),
                VSPBlocks.MAGNETIZED_IRON_BLOCK.get(),
                VSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get(),
                VSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(),
                VSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get()
        );

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(
                VSPBlocks.HYPERGOLIC_STEEL_TANK.get(),
                VSPBlocks.LUNAR_REGOLITH.get(),
                VSPBlocks.VEHICLE_ASSEMBLER.get(),
                VSPBlocks.THREE_KEROLOX.get(),
                VSPBlocks.TWO_KEROLOX.get(),
                VSPBlocks.ROCKETRY_BOUNDING_BLOCK.get(),
                VSPBlocks.ALUMINIUM_ORE.get(),
                VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                VSPBlocks.ALUMINIUM_BLOCK.get(),
                VSPBlocks.RAW_ALUMINIUM_BLOCK.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                VSPBlocks.SHEET_METAL_ROLLER.get(),
                VSPBlocks.HYPERGOLIC_STEEL_TANK.get(),
                VSPBlocks.LUNAR_REGOLITH.get(),
                VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get(),
                VSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get(),
                VSPBlocks.MAGNETIZER.get(),
                VSPBlocks.MAGNETIZED_IRON_BLOCK.get(),
                VSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(),
                VSPBlocks.VEHICLE_ASSEMBLER.get(),
                VSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get(),
                VSPBlocks.THREE_KEROLOX.get(),
                VSPBlocks.TWO_KEROLOX.get(),
                VSPBlocks.MACHINERY_BOUNDING_BLOCK.get(),
                VSPBlocks.ROCKETRY_BOUNDING_BLOCK.get(),
                VSPBlocks.ALUMINIUM_ORE.get(),
                VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                VSPBlocks.ALUMINIUM_BLOCK.get(),
                VSPBlocks.RAW_ALUMINIUM_BLOCK.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(VSPBlocks.LUNAR_REGOLITH.get());
    }
}
