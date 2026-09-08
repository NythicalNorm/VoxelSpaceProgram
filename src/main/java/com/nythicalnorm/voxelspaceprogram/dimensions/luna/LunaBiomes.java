package com.nythicalnorm.voxelspaceprogram.dimensions.luna;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class LunaBiomes {
    public static final ResourceKey<Biome> TERRAE_BIOME = ResourceKey.create(Registries.BIOME, VoxelSpaceProgram.rl("luna_terrae"));

    public static void bootstrapBiomes(BootstapContext<Biome> context) {
        context.register(TERRAE_BIOME, new Biome.BiomeBuilder()
            .hasPrecipitation(false)
            .temperature(0.0F)
            .downfall(0.0F)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                        .fogColor(0x0A0A0A)
                        .waterColor(0x393e6b)
                        .waterFogColor(0x393e6b)
                        .skyColor(0x000000)
                        .build()
            )
            .mobSpawnSettings(
                new MobSpawnSettings.Builder()
                        .build()
            )
            .generationSettings(
                new BiomeGenerationSettings.PlainBuilder()
                        .build()
            ).build()
        );
    }
}
