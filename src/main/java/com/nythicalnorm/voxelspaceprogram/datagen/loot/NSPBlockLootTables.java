package com.nythicalnorm.voxelspaceprogram.datagen.loot;

import com.nythicalnorm.voxelspaceprogram.Item.NSPItems;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.fluid.NSPFluids;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class NSPBlockLootTables extends BlockLootSubProvider {
    public NSPBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(NSPBlocks.MAGNETIZED_IRON_BLOCK.get());

        this.add(NSPBlocks.ALUMINIUM_ORE.get(),
                this.createOreDrop(NSPBlocks.ALUMINIUM_ORE.get(), NSPItems.RAW_ALUMINUM.get())
        );
        this.add(NSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                this.createOreDrop(NSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(), NSPItems.RAW_ALUMINUM.get())
        );

        this.dropSelf(NSPBlocks.ALUMINIUM_BLOCK.get());
        this.dropSelf(NSPBlocks.RAW_ALUMINIUM_BLOCK.get());

        this.dropSelf(NSPBlocks.HYPERGOLIC_STEEL_TANK.get());
        this.dropSelf(NSPBlocks.LUNAR_REGOLITH.get());
        this.dropSelf(NSPBlocks.CRYOGENIC_AIR_SEPARATOR.get());
        this.dropSelf(NSPBlocks.MAGNETIZER.get());
        this.dropOther(NSPFluids.LIQUID_OXYGEN.block.get(), Blocks.AIR);
        this.dropSelf(NSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get());

        this.dropSelf(NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get());
        this.dropSelf(NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get());
        this.dropSelf(NSPBlocks.VEHICLE_ASSEMBLER.get());

        this.dropSelf(NSPBlocks.THREE_KEROLOX.get());
        this.dropSelf(NSPBlocks.TWO_KEROLOX.get());
        this.dropOther(NSPBlocks.BOUNDING_BLOCK.get(), Blocks.AIR);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return NSPBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
