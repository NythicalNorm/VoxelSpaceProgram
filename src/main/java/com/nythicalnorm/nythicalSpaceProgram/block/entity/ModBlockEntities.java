package com.nythicalnorm.nythicalSpaceProgram.block.entity;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
     public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
             .create(ForgeRegistries.BLOCK_ENTITY_TYPES, NythicalSpaceProgram.MODID);

     public static final RegistryObject<BlockEntityType<MagnetizerEntity>> MAGNETIZER_BE =
             BLOCK_ENTITIES.register("magnetizer_be", () ->
                     BlockEntityType.Builder.of(MagnetizerEntity::new, ModBlocks.MAGNETIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CryogenicAirSeparatorEntity>> CRYOGENIC_AIR_SEPARATOR_BE =
            BLOCK_ENTITIES.register("cryogenic_air_separator_be", () ->
                    BlockEntityType.Builder.of(CryogenicAirSeparatorEntity::new, ModBlocks.CRYOGENIC_AIR_SEPARATOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
     }
}
