package com.nythicalnorm.voxelspaceprogram.dimensions.vanilla;

import com.nythicalnorm.voxelspaceprogram.dimensions.VSPWorldGenUtils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VanillaPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ALUMINUM_ORE_PLACED_MIDDLE =
            VSPWorldGenUtils.registerPlacedFeature("overworld_aluminum_ore_placed_middle");

    public static final ResourceKey<PlacedFeature> ALUMINUM_ORE_PLACED_SMALL =
            VSPWorldGenUtils.registerPlacedFeature("overworld_aluminum_ore_placed_small");

    public static void bootstrapPlacedFeatures(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(
            context,
                ALUMINUM_ORE_PLACED_MIDDLE,
            configuredFeatures.getOrThrow(VanillaConfiguredFeatures.OVERWORLD_ALUMINUM_ORE),
            VSPWorldGenUtils.commonOrePlacement(
                7,
                HeightRangePlacement.triangle(VerticalAnchor.absolute(-42), VerticalAnchor.absolute(48)))
        );

        PlacementUtils.register(
                context,
                ALUMINUM_ORE_PLACED_SMALL,
                configuredFeatures.getOrThrow(VanillaConfiguredFeatures.OVERWORLD_ALUMINUM_ORE),
                VSPWorldGenUtils.commonOrePlacement(
                        5,
                        HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(64)))
        );
    }
}
