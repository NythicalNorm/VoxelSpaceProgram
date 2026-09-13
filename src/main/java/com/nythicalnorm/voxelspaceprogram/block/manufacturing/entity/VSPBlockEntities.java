package com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.gse.entity.VehicleAssemblerEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.PropellantTankEntity;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.BoundingBlockEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSPBlockEntities {
     public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
             .create(ForgeRegistries.BLOCK_ENTITY_TYPES, VoxelSpaceProgram.MODID);

    public static final RegistryObject<BlockEntityType<SheetMetalRollerEntity>> SHEET_METAL_ROLLER_BE =
            BLOCK_ENTITIES.register("sheet_metal_roller_be", () ->
                    BlockEntityType.Builder.of(SheetMetalRollerEntity::new, VSPBlocks.SHEET_METAL_ROLLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<MagnetizerEntity>> MAGNETIZER_BE =
             BLOCK_ENTITIES.register("magnetizer_be", () ->
                     BlockEntityType.Builder.of(MagnetizerEntity::new, VSPBlocks.MAGNETIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CryogenicAirSeparatorEntity>> CRYOGENIC_AIR_SEPARATOR_BE =
            BLOCK_ENTITIES.register("cryogenic_air_separator_be", () ->
                    BlockEntityType.Builder.of(CryogenicAirSeparatorEntity::new, VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<VehicleAssemblerEntity>> VEHICLE_ASSEMBLER_BE =
            BLOCK_ENTITIES.register("vehicle_assembler_be", () ->
                    BlockEntityType.Builder.of(VehicleAssemblerEntity::new, VSPBlocks.VEHICLE_ASSEMBLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<EngineEntity>> ENGINE_BE =
            BLOCK_ENTITIES.register("engine_be", () -> BlockEntityType.Builder.of(EngineEntity::new,
                    VSPBlocks.THREE_KEROLOX.get(),
                    VSPBlocks.TWO_KEROLOX.get())
                    .build(null)
            );

    public static final RegistryObject<BlockEntityType<PropellantTankEntity>> PROPELLANT_TANK_BE =
            BLOCK_ENTITIES.register("propellant_tank_be", () -> BlockEntityType.Builder.of(PropellantTankEntity::new,
                    VSPBlocks.HYPERGOLIC_STEEL_TANK.get())
                    .build(null)
            );

    public static final RegistryObject<BlockEntityType<BoundingBlockEntity>> BOUNDING_BLOCK_BE =
            BLOCK_ENTITIES.register("bounding_block_be", () -> BlockEntityType.Builder.of(BoundingBlockEntity::new, VSPBlocks.BOUNDING_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
     }
}
