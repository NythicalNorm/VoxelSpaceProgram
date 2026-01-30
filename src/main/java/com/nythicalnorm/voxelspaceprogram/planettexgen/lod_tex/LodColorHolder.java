package com.nythicalnorm.voxelspaceprogram.planettexgen.lod_tex;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Map;
import java.util.Optional;

public class LodColorHolder {
    private static final Map<ResourceKey<Biome>, Integer> biomeColorMap = new Object2IntOpenHashMap<>();

    public static void init() {
        //Oceans
        biomeColorMap.put(Biomes.OCEAN, 0x3F76E4);
        biomeColorMap.put(Biomes.DEEP_OCEAN, 0x3F76E4);
        biomeColorMap.put(Biomes.WARM_OCEAN, 0x43D5EE);
        biomeColorMap.put(Biomes.LUKEWARM_OCEAN, 0x45ADF2);
        biomeColorMap.put(Biomes.DEEP_LUKEWARM_OCEAN, 0x45ADF2);
        biomeColorMap.put(Biomes.COLD_OCEAN, 0x3D57D6);
        biomeColorMap.put(Biomes.DEEP_COLD_OCEAN, 0x3D57D6);
        biomeColorMap.put(Biomes.FROZEN_OCEAN, 0x83a8e9);
        biomeColorMap.put(Biomes.DEEP_FROZEN_OCEAN, 0x83a8e9);
        biomeColorMap.put(Biomes.MUSHROOM_FIELDS, 0x6d646a);

        //Highlands
        biomeColorMap.put(Biomes.JAGGED_PEAKS, 0x8d9191);
        biomeColorMap.put(Biomes.FROZEN_PEAKS, 0xbfbfbf);
        biomeColorMap.put(Biomes.STONY_PEAKS, 0x5d5d5d);
        biomeColorMap.put(Biomes.MEADOW, 0x506d4c);
        biomeColorMap.put(Biomes.CHERRY_GROVE, 0xa1567e);
        biomeColorMap.put(Biomes.GROVE, 0x90667b);
        biomeColorMap.put(Biomes.SNOWY_SLOPES, 0xe4eced);
        biomeColorMap.put(Biomes.WINDSWEPT_HILLS, 0x485f47);
        biomeColorMap.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, 0x7e7978);
        biomeColorMap.put(Biomes.WINDSWEPT_FOREST, 0x4d664c);

        //Woodland Biomes
        biomeColorMap.put(Biomes.FOREST, 0x3f7e20);
        biomeColorMap.put(Biomes.FLOWER_FOREST, 0x346b1a);
        biomeColorMap.put(Biomes.TAIGA, 0x294129);
        biomeColorMap.put(Biomes.OLD_GROWTH_PINE_TAIGA, 0x5f3f16);
        biomeColorMap.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, 0x5f3f16);
        biomeColorMap.put(Biomes.SNOWY_TAIGA, 0x5f3f16);
        biomeColorMap.put(Biomes.BIRCH_FOREST, 0x577942);
        biomeColorMap.put(Biomes.OLD_GROWTH_BIRCH_FOREST, 0x5f3f16);
        biomeColorMap.put(Biomes.DARK_FOREST, 0x284e19);
        biomeColorMap.put(Biomes.JUNGLE, 0x0d3502);
        biomeColorMap.put(Biomes.SPARSE_JUNGLE, 0x1a5303);
        biomeColorMap.put(Biomes.BAMBOO_JUNGLE, 0x507f18);

        // Wetland biomes
        biomeColorMap.put(Biomes.RIVER, 0x3F76E4);
        biomeColorMap.put(Biomes.FROZEN_RIVER, 0x708bb9);
        biomeColorMap.put(Biomes.SWAMP, 0x232317);
        biomeColorMap.put(Biomes.MANGROVE_SWAMP, 0x465712);
        biomeColorMap.put(Biomes.BEACH, 0xdad0a5);
        biomeColorMap.put(Biomes.STONY_SHORE, 0x787878);

        //Flatland biomes
        biomeColorMap.put(Biomes.PLAINS, 0x5d793c);
        biomeColorMap.put(Biomes.SNOWY_PLAINS, 0xe7e9e8);
        biomeColorMap.put(Biomes.ICE_SPIKES, 0x6887bb);

        // Aridland biomes
        biomeColorMap.put(Biomes.DESERT, 0xdacfa3);
        biomeColorMap.put(Biomes.SAVANNA, 0x69632e);
        biomeColorMap.put(Biomes.SAVANNA_PLATEAU, 0x6f6930);
        biomeColorMap.put(Biomes.WINDSWEPT_SAVANNA, 0x694b32);
        biomeColorMap.put(Biomes.BADLANDS, 0xbb6521);
        biomeColorMap.put(Biomes.WOODED_BADLANDS, 0x654831);
        biomeColorMap.put(Biomes.ERODED_BADLANDS, 0x975d44);
    }

    public static int getColorForBiome(Optional<ResourceKey<Biome>> biomeResourceKey) {
        if (biomeResourceKey.isPresent() && biomeColorMap.containsKey(biomeResourceKey.get())) {
            return biomeColorMap.get(biomeResourceKey.get());
        }
        return 0;
    }
}
