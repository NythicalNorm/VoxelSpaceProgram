package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.VSPDimensions;
import com.nythicalnorm.voxelspaceprogram.dimensions.vanilla.VanillaBiomeModifiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class VSPWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DIMENSION_TYPE, VSPDimensions::bootstrapType)
            .add(Registries.LEVEL_STEM, VSPDimensions::bootstrapStem)
            .add(Registries.NOISE_SETTINGS, VSPDimensions::bootstrapNoiseSettings)
            .add(Registries.NOISE, VSPDimensions::bootstrapNoiseParameters)
            .add(Registries.BIOME, VSPDimensions::bootstrapBiomes)
            .add(Registries.CONFIGURED_FEATURE, VSPDimensions::bootstrapConfiguredFeature)
            .add(Registries.PLACED_FEATURE, VSPDimensions::bootstrapPlacedFeatures)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, VanillaBiomeModifiers::bootstrapBiomeModifiers);


    public VSPWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(VoxelSpaceProgram.MODID));
    }
}
