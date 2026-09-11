package com.nythicalnorm.voxelspaceprogram.dimensions.vanilla;

import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.dimensions.VSPWorldGenUtils;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class VanillaConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ALUMINUM_ORE =
            VSPWorldGenUtils.registerConfiguredFeature("overworld_aluminum_ore");

    public static void bootstrapConfiguredFeatures(BootstapContext<ConfiguredFeature<?,?>> context) {
        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> overworldAluminumOres = List.of(
                OreConfiguration.target(stoneReplaceable, NSPBlocks.ALUMINIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceable, NSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get().defaultBlockState())
        );

        FeatureUtils.register(context, OVERWORLD_ALUMINUM_ORE, Feature.ORE, new OreConfiguration(overworldAluminumOres, 11));
    }
}
