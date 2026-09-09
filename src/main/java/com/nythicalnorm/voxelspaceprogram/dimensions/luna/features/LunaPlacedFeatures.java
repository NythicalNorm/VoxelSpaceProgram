package com.nythicalnorm.voxelspaceprogram.dimensions.luna.features;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class LunaPlacedFeatures {
    public static final ResourceKey<PlacedFeature> LUNA_BOULDER_PLACED_FEATURE =
            ResourceKey.create(Registries.PLACED_FEATURE, VoxelSpaceProgram.rl("luna_boulder"));

    public static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features =
                context.lookup(Registries.CONFIGURED_FEATURE);

        Holder<ConfiguredFeature<?, ?>> boulder =
                features.getOrThrow(
                        LunaConfiguredFeatures.LUNA_BOULDER
                );

        context.register(
            LUNA_BOULDER_PLACED_FEATURE,
            new PlacedFeature(
                boulder,
                List.of(
                    RarityFilter.onAverageOnceEvery(10),
                    InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                    BiomeFilter.biome()
                )
            )
        );
    }
}
