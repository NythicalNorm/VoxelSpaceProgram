package com.nythicalnorm.nythicalSpaceProgram.datagen.loot;

import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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
    }

    @Override
    protected  Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
