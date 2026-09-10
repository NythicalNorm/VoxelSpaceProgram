package com.nythicalnorm.voxelspaceprogram.block;

import com.nythicalnorm.voxelspaceprogram.Item.NSPItems;
import com.nythicalnorm.voxelspaceprogram.Item.RocketryBlockItem;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.gse.VehicleAssembler;
import com.nythicalnorm.voxelspaceprogram.block.gse.PlatformAssembly;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.CryogenicAirSeparator;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.PropellantTankBlock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.multiblock.BoundingBlock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.EngineBlock;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineProperties;
import com.nythicalnorm.voxelspaceprogram.block.terrain.luna.FootprintedRegolith;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.Magnetizer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class NSPBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, VoxelSpaceProgram.MODID);

    public static final RegistryObject<Block> HYPERGOLIC_STEEL_TANK = registerBlock("hypergolic_steel_tank",
            () -> new PropellantTankBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> MAGNETIZED_IRON_BLOCK = registerBlock("magnetized_iron_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> LUNAR_REGOLITH = registerBlock("lunar_regolith",
            () -> new FootprintedRegolith(BlockBehaviour.Properties.copy(Blocks.NETHERRACK).sound(SoundType.POWDER_SNOW).destroyTime(2f)));


    //Manufacturing Blocks
    public static final RegistryObject<Block> CRYOGENIC_AIR_SEPARATOR = registerBlock("cryogenic_air_separator",
            () -> new CryogenicAirSeparator(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).noOcclusion()));

    public static final RegistryObject<Block> CRYOGENIC_AIR_SEPARATOR_PART = registerBlock("cryogenic_air_separator_part",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).destroyTime(3f)));

    public static final RegistryObject<Block> MAGNETIZER = registerBlock("magnetizer",
            () -> new Magnetizer(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).noOcclusion()));

    // Ground Service Equipment (GSE) blocks
    public static final RegistryObject<Block> VEHICLE_ASSEMBLY_PLATFORM = registerBlock("vehicle_assembly_platform",
            () -> new PlatformAssembly(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).destroyTime(2f).explosionResistance(10f)));

    public static final RegistryObject<Block> VEHICLE_ASSEMBLY_SCAFFOLD = registerBlock("vehicle_assembly_scaffold",
            () -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).sound(SoundType.COPPER).destroyTime(2f)
                    .explosionResistance(10f).noOcclusion()));

    public static final RegistryObject<Block> VEHICLE_ASSEMBLER = registerBlock("vehicle_assembler",
            () -> new VehicleAssembler(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).noOcclusion()));

    // Engines
    public static final RegistryObject<Block> THREE_KEROLOX = registerRocketryBlock("three_kerolox",
            () -> new EngineBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).sound(SoundType.NETHERITE_BLOCK).noOcclusion(),
                    new EngineProperties.Builder()
                            .BlockSize(3)
                            .PixelHeight(48)
                            .PixelWidth(38)
                            .build()));

    public static final RegistryObject<Block> TWO_KEROLOX = registerRocketryBlock("two_kerolox",
            () -> new EngineBlock(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK).sound(SoundType.NETHERITE_BLOCK).noOcclusion(),
            new EngineProperties.Builder()
                    .BlockSize(2)
                    .PixelHeight(32)
                    .PixelWidth(24)
                    .build()));

    public static final RegistryObject<Block> BOUNDING_BLOCK = registerBlockOnly("engine_bounding_block",
            () -> new BoundingBlock(BlockBehaviour.Properties.copy(THREE_KEROLOX.get())));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = registerBlockOnly(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<T> registerBlockOnly(String name, Supplier<T> block) {
        return BLOCKS.register(name,block);
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return NSPItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerRocketryBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = registerBlockOnly(name,block);
        NSPItems.ITEMS.register(name, () -> new RocketryBlockItem(toReturn.get(), new Item.Properties()));

        return toReturn;
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
