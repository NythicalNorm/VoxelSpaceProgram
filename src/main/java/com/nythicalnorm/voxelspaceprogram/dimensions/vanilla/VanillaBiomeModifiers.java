package com.nythicalnorm.voxelspaceprogram.dimensions.vanilla;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class VanillaBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_ALUMINUM_ORE = registerBiomeModifier("add_aluminum_ore");

    public static void bootstrapBiomeModifiers(BootstapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(ADD_ALUMINUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
            HolderSet.direct(
                    placedFeatures.getOrThrow(VanillaPlacedFeatures.ALUMINUM_ORE_PLACED_MIDDLE),
                    placedFeatures.getOrThrow(VanillaPlacedFeatures.ALUMINUM_ORE_PLACED_SMALL)
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }

    public static ResourceKey<BiomeModifier> registerBiomeModifier(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, VoxelSpaceProgram.rl(name));
    }
}
