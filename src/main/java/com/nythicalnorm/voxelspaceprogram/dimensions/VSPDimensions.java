package com.nythicalnorm.voxelspaceprogram.dimensions;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.luna.LunaBiomes;
import com.nythicalnorm.voxelspaceprogram.dimensions.luna.LunaDimension;
import com.nythicalnorm.voxelspaceprogram.dimensions.luna.LunaNoiseRouterBuilder;
import com.nythicalnorm.voxelspaceprogram.dimensions.luna.LunaNoiseSettings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class VSPDimensions {
    public static final ResourceKey<LevelStem> LUNA_KEY = ResourceKey.create(Registries.LEVEL_STEM, VoxelSpaceProgram.rl("luna"));
    public static final ResourceKey<Level> LUNA_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, VoxelSpaceProgram.rl("luna"));
    public static final ResourceKey<DimensionType> LUNA_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, VoxelSpaceProgram.rl("luna_type"));

    public static final ResourceKey<LevelStem> MARS_KEY = ResourceKey.create(Registries.LEVEL_STEM, VoxelSpaceProgram.rl("mars"));
    public static final ResourceKey<Level> MARS_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, VoxelSpaceProgram.rl("mars"));
    public static final ResourceKey<DimensionType> MARS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, VoxelSpaceProgram.rl("mars_type"));


    public static void bootstrapType(BootstapContext<DimensionType> context) {
        LunaDimension.bootstrapType(context);
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        LunaDimension.bootstrapStem(context);
    }

    public static void bootstrapNoiseSettings(BootstapContext<NoiseGeneratorSettings> context) {
        LunaNoiseSettings.bootstrap(context);
    }

    public static void bootstrapNoiseParameters(BootstapContext<NormalNoise.NoiseParameters> context) {
        LunaNoiseRouterBuilder.bootstrapNoiseParameters(context);
    }

    public static void bootstrapBiomes(BootstapContext<Biome> context) {
        LunaBiomes.bootstrapBiomes(context);
    }
}
