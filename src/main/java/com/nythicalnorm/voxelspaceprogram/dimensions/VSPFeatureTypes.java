package com.nythicalnorm.voxelspaceprogram.dimensions;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.luna.features.BoulderConfiguration;
import com.nythicalnorm.voxelspaceprogram.dimensions.luna.features.BoulderFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSPFeatureTypes {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, VoxelSpaceProgram.MODID);
    public static final RegistryObject<Feature<BoulderConfiguration>> LUNA_BOULDER = FEATURES.register("luna_boulder", BoulderFeature::new);

    public static void register(IEventBus eventBus)
    {
        FEATURES.register(eventBus);
    }
}
