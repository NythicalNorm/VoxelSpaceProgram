package com.nythicalnorm.nythicalSpaceProgram.datagen.loot;

import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.fluid.ModFluids;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.OXYGEN_PROPELLANT_TANK.get());
        this.dropSelf(ModBlocks.LUNAR_REGOLITH.get());
        this.dropSelf(ModBlocks.MAGNETIZED_IRON_BLOCK.get());
        this.dropSelf(ModBlocks.CRYOGENIC_AIR_SEPARATOR.get());
        this.dropSelf(ModBlocks.MAGNETIZER.get());
        this.dropOther(ModFluids.LIQUID_OXYGEN.block.get(), Blocks.AIR);
        this.dropSelf(ModBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get());
    }

    @Override
    protected  Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
