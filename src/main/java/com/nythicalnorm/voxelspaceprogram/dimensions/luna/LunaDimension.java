package com.nythicalnorm.voxelspaceprogram.dimensions.luna;

import com.nythicalnorm.planetshine.PlanetShine;
import com.nythicalnorm.voxelspaceprogram.dimensions.VSPDimensions;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.OptionalLong;

public class LunaDimension {
    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(VSPDimensions.LUNA_DIM_TYPE, new DimensionType(
                OptionalLong.empty(), // fixedTime
                true, // hasSkylight
                false, // hasCeiling
                true, // ultraWarm
                true, // natural
                0.333333333333f, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                -64, // minY
                384, // height
                320, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                PlanetShine.rl("ps_space_dimension_effects"), // effectsLocation
                0.0f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0))
        );
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        Holder<DimensionType> moonType = dimensionTypes.getOrThrow(VSPDimensions.LUNA_DIM_TYPE);
        Holder<NoiseGeneratorSettings> moonNoise = noiseSettings.getOrThrow(LunaNoiseSettings.LUNA_NOISE);

        FixedBiomeSource biomeSource = new FixedBiomeSource(biomes.getOrThrow(LunaBiomes.TERRAE_BIOME));
        NoiseBasedChunkGenerator generator = new NoiseBasedChunkGenerator(biomeSource, moonNoise);

        context.register(VSPDimensions.LUNA_KEY,
                new LevelStem(
                        moonType,
                        generator
                )
        );
    }
}
