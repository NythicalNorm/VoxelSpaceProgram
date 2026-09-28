package com.nythicalnorm.voxelspaceprogram.entities;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSPEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, VoxelSpaceProgram.MODID);

    public static final RegistryObject<EntityType<CommandSeatEntity>> COMMAND_SEAT_ENTITY =
            ENTITY_TYPES.register("command_seat_entity", () -> EntityType.Builder.of(CommandSeatEntity::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.25f, 0.35f)
                    .build("command_seat_entity")
            );

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
