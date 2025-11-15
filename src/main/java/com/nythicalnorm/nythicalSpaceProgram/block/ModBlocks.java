package com.nythicalnorm.nythicalSpaceProgram.block;

import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.custom.CryogenicAirSeparator;
import com.nythicalnorm.nythicalSpaceProgram.block.custom.FootprintedRegolith;
import com.nythicalnorm.nythicalSpaceProgram.block.custom.Magnetizer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NythicalSpaceProgram.MODID);

    public static final RegistryObject<Block> OXYGEN_PROPELLANT_TANK = registerBlock("oxygen_propellant_tank",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> MAGNETIZED_IRON_BLOCK = registerBlock("magnetized_iron_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.NETHERITE_BLOCK)));

    public static final RegistryObject<Block> LUNAR_REGOLITH = registerBlock("lunar_regolith",
            () -> new FootprintedRegolith(BlockBehaviour.Properties.copy(Blocks.NETHERRACK).sound(SoundType.POWDER_SNOW).destroyTime(2f)));

    public static final RegistryObject<Block> CRYOGENIC_AIR_SEPARATOR = registerBlock("cryogenic_air_separator",
            () -> new CryogenicAirSeparator(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).noOcclusion()));

    public static final RegistryObject<Block> CRYOGENIC_AIR_SEPARATOR_PART = registerBlock("cryogenic_air_separator_part",
            () -> new CryogenicAirSeparator(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER)));

    public static final RegistryObject<Block> MAGNETIZER = registerBlock("magnetizer",
            () -> new Magnetizer(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.COPPER).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
