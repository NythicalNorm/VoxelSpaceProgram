package com.nythicalnorm.voxelspaceprogram.datagen.loot;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.fluid.VSPFluids;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class VSPBlockLootTables extends BlockLootSubProvider {
    public VSPBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(VSPBlocks.MAGNETIZED_IRON_BLOCK.get());

        this.add(VSPBlocks.ALUMINIUM_ORE.get(),
                this.createOreDrop(VSPBlocks.ALUMINIUM_ORE.get(), VSPItems.RAW_ALUMINUM.get())
        );
        this.add(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(),
                this.createOreDrop(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(), VSPItems.RAW_ALUMINUM.get())
        );

        this.dropSelf(VSPBlocks.ALUMINIUM_BLOCK.get());
        this.dropSelf(VSPBlocks.RAW_ALUMINIUM_BLOCK.get());

        this.dropSelf(VSPBlocks.HYPERGOLIC_STEEL_TANK.get());
        this.dropSelf(VSPBlocks.LUNAR_REGOLITH.get());
        this.dropSelf(VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get());
        this.dropSelf(VSPBlocks.MAGNETIZER.get());
        this.dropOther(VSPFluids.LIQUID_OXYGEN.block.get(), Blocks.AIR);
        this.dropOther(VSPFluids.NITROGEN.block.get(), Blocks.AIR);
        this.dropSelf(VSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get());

        this.dropSelf(VSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get());
        this.dropSelf(VSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get());
        this.dropSelf(VSPBlocks.VEHICLE_ASSEMBLER.get());

        this.dropSelf(VSPBlocks.THREE_KEROLOX.get());
        this.dropSelf(VSPBlocks.TWO_KEROLOX.get());
        this.dropOther(VSPBlocks.BOUNDING_BLOCK.get(), Blocks.AIR);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return VSPBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
