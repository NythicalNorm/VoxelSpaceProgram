package com.nythicalnorm.voxelspaceprogram.dimensions.luna.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record BoulderConfiguration(
        int minRadius,
        int maxRadius,
        int minHeight,
        int maxHeight,
        BlockStateProvider block
) implements FeatureConfiguration {

    public static final Codec<BoulderConfiguration> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("min_radius")
                                    .forGetter(BoulderConfiguration::minRadius),

                            Codec.INT.fieldOf("max_radius")
                                    .forGetter(BoulderConfiguration::maxRadius),

                            Codec.INT.fieldOf("min_height")
                                    .forGetter(BoulderConfiguration::minHeight),

                            Codec.INT.fieldOf("max_height")
                                    .forGetter(BoulderConfiguration::maxHeight),

                            BlockStateProvider.CODEC.fieldOf("block")
                                    .forGetter(BoulderConfiguration::block)
                    ).apply(instance, BoulderConfiguration::new)
            );
}
