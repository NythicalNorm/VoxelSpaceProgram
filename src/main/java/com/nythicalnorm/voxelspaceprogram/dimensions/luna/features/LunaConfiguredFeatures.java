package com.nythicalnorm.voxelspaceprogram.dimensions.luna.features;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.VSPFeatureTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class LunaConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> LUNA_BOULDER =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, VoxelSpaceProgram.rl("luna_boulder"));

    public static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(
                context,
                LUNA_BOULDER,
                VSPFeatureTypes.LUNA_BOULDER.get(),
                new BoulderConfiguration(
                        2,
                        5,
                        1,
                        4,
                        BlockStateProvider.simple(
                                Blocks.DEEPSLATE.defaultBlockState()
                        )
                )
        );
    }

//    BlockStateProvider block =
//            new WeightedStateProvider(
//                    SimpleWeightedRandomList.<BlockState>builder()
//                            .add(Blocks.STONE.defaultBlockState(), 70)
//                            .add(Blocks.TUFF.defaultBlockState(), 20)
//                            .add(Blocks.ANDESITE.defaultBlockState(), 10)
//                            .build()
//            );
}
