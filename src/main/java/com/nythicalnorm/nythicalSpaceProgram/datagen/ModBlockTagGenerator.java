package com.nythicalnorm.nythicalSpaceProgram.datagen;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NythicalSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModTags.Blocks.MAGNETIC_METALS).add(Blocks.IRON_BLOCK, Blocks.COPPER_BLOCK, Blocks.GOLD_BLOCK, Blocks.REDSTONE_BLOCK);
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.OXYGEN_PROPELLANT_TANK.get(), ModBlocks.LUNAR_REGOLITH.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.OXYGEN_PROPELLANT_TANK.get()).add(ModBlocks.LUNAR_REGOLITH.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.LUNAR_REGOLITH.get());
    }
}
