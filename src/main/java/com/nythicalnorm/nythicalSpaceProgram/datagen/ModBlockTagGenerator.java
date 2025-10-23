package com.nythicalnorm.nythicalSpaceProgram.datagen;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
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
        this.tag(ModTags.Blocks.MAGNETIC_METALS).addTag(Tags.Blocks.ORES_IRON);

        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.OXYGEN_PROPELLANT_TANK.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.OXYGEN_PROPELLANT_TANK.get());
    }
}
