package com.nythicalnorm.nythicalSpaceProgram.planetdimgen;

import com.nythicalnorm.nythicalSpaceProgram.mixin.MultiNoiseBiomeSourceMixin;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Optional;

public class OverworldBiomeGen {
    private static Holder<Biome> biomePlain = null;

    public static void setBiome(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        HolderGetter<Biome> biomesGetter = registryAccess.lookup(Registries.BIOME).get();
        Holder<Biome> biome = biomesGetter.getOrThrow(Biomes.WINDSWEPT_HILLS);
        biomePlain = biome;
    }
    public static Optional<Holder<Biome>> getBiome() {
        if (biomePlain != null) {
            return Optional.of(biomePlain);
        }
        return Optional.empty();
    }
}
