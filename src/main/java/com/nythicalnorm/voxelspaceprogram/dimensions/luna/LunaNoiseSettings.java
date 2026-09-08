package com.nythicalnorm.voxelspaceprogram.dimensions.luna;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.List;

public class LunaNoiseSettings {
    public static final ResourceKey<NoiseGeneratorSettings> LUNA_NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, VoxelSpaceProgram.rl("luna_noise"));

    public static void bootstrap(BootstapContext<NoiseGeneratorSettings> context) {

        /*
         *  Moon vertical world:
         *
         *      Y = 0     bottom
         *      Y = 128   surface region
         *      Y = 256   top
         *
         *  You can change these later.
         */
        NoiseSettings noiseSettings = NoiseSettings.create(
                -64,      // min Y
                384,    // height
                1,      // horizontal noise size
                2       // vertical noise size
        );


        var noiseRouter = LunaNoiseRouterBuilder.create(context);

        /*
         * Lunar surface.
         *
         * We use stone everywhere, with a thin layer of
         * custom lunar regolith.
         *
         * Replace Blocks.STONE / Blocks.GRAVEL with your
         * own Moon blocks if you have them.
         */
        SurfaceRules.RuleSource surfaceRule =
                SurfaceRules.sequence(
                        // Top 1 block
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.state(
                                        NSPBlocks.LUNAR_REGOLITH.get().defaultBlockState()
                                )
                        ),

                        // Next few blocks
                        SurfaceRules.ifTrue(
                                SurfaceRules.UNDER_FLOOR,
                                SurfaceRules.state(
                                        NSPBlocks.LUNAR_REGOLITH.get().defaultBlockState()
                                )
                        ),

                        SurfaceRules.ifTrue(SurfaceRules.verticalGradient(
                                "luna_bedrock_floor",
                                VerticalAnchor.bottom(),
                                VerticalAnchor.aboveBottom(5)),
                                SurfaceRules.state((Blocks.BEDROCK).defaultBlockState())),

                        // Everything else
                        SurfaceRules.state(
                                Blocks.STONE.defaultBlockState()
                        )
                );

        NoiseGeneratorSettings lunaSettings =
                new NoiseGeneratorSettings(
                        noiseSettings,

                        // Default solid block
                        NSPBlocks.LUNAR_REGOLITH.get().defaultBlockState(),

                        // Default fluid
                        Blocks.AIR.defaultBlockState(),

                        noiseRouter,

                        surfaceRule,

                        // Spawn targets
                        List.of(),

                        // Sea level
                        0,

                        // Disable mob generation
                        true,

                        // Aquifers
                        false,

                        // Ore veins
                        false,

                        // Legacy random source
                        false
                );

        context.register(LUNA_NOISE, lunaSettings);
    }
}