package com.nythicalnorm.nythicalSpaceProgram.dimensions;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class SpaceDimension {
    public static final ResourceKey<LevelStem> SPACE_KEY = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath (NythicalSpaceProgram.MODID, "spacedim"));
    public static final ResourceKey<Level> SPACE_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath (NythicalSpaceProgram.MODID, "spacedim"));
    public static final ResourceKey<DimensionType> SPACE_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath (NythicalSpaceProgram.MODID, "spacedim_type"));

    public static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(SPACE_DIM_TYPE, new DimensionType(
                OptionalLong.of(0), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // effectsLocation
                0.5f, // ambientLight
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)));
    }

    public static void bootstrapStem(BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        FlatLevelGeneratorSettings flatlevelgeneratorsettings =
                new FlatLevelGeneratorSettings(Optional.empty(), biomes.getOrThrow(Biomes.THE_VOID), List.of());

        ChunkGenerator VoidGenerator = new FlatLevelSource(flatlevelgeneratorsettings);
        flatlevelgeneratorsettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.AIR));
        LevelStem stem = new LevelStem(dimTypes.getOrThrow(SpaceDimension.SPACE_DIM_TYPE), VoidGenerator);

        context.register(SPACE_KEY, stem);
    }
}
