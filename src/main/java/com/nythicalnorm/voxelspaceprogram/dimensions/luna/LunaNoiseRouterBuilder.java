package com.nythicalnorm.voxelspaceprogram.dimensions.luna;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class LunaNoiseRouterBuilder {
    public static final ResourceKey<NormalNoise.NoiseParameters> LARGE_TERRAIN =
            ResourceKey.create(
                    Registries.NOISE,
                    VoxelSpaceProgram.rl("luna_large_terrain")
            );

    public static final ResourceKey<NormalNoise.NoiseParameters> MEDIUM_TERRAIN =
            ResourceKey.create(
                    Registries.NOISE,
                    VoxelSpaceProgram.rl("luna_medium_terrain")
            );

    public static final ResourceKey<NormalNoise.NoiseParameters> SMALL_TERRAIN =
            ResourceKey.create(
                    Registries.NOISE,
                    VoxelSpaceProgram.rl("luna_small_terrain")
            );

    public static final ResourceKey<NormalNoise.NoiseParameters> LARGE_CRATER =
            ResourceKey.create(
                    Registries.NOISE,
                    VoxelSpaceProgram.rl("luna_large_crater")
            );

    public static final ResourceKey<NormalNoise.NoiseParameters> MEDIUM_CRATER =
            ResourceKey.create(
                    Registries.NOISE,
                    VoxelSpaceProgram.rl("luna_medium_crater")
            );

    public static final ResourceKey<NormalNoise.NoiseParameters> SMALL_CRATER =
            ResourceKey.create(
                    Registries.NOISE,
                    VoxelSpaceProgram.rl("luna_small_crater")
            );

    public static void bootstrapNoiseParameters(BootstapContext<NormalNoise.NoiseParameters> context) {
        context.register(LARGE_TERRAIN,
            new NormalNoise.NoiseParameters(
                    -8,
                    1.0
            )
        );

        context.register(MEDIUM_TERRAIN,
                new NormalNoise.NoiseParameters(
                        -5,
                        1.0
                )
        );

        context.register(SMALL_TERRAIN,
                new NormalNoise.NoiseParameters(
                        -2,
                        1.0
                )
        );

        //Craters
        context.register(LARGE_CRATER,
                new NormalNoise.NoiseParameters(
                        -9,
                        1.0
                )
        );

        context.register(MEDIUM_CRATER,
                new NormalNoise.NoiseParameters(
                        -6,
                        1.0
                )
        );

        context.register(SMALL_CRATER,
                new NormalNoise.NoiseParameters(
                        -3,
                        1.0
                )
        );
    }

    /*
     * Main terrain density.
     */
    public static NoiseRouter create(BootstapContext<NoiseGeneratorSettings> context) {

        DensityFunction terrain = createLunarTerrain(context);

        return new NoiseRouter(

                // barrier noise
                DensityFunctions.zero(),

                // fluid level floodedness
                DensityFunctions.zero(),

                // fluid level spread
                DensityFunctions.zero(),

                // lava
                DensityFunctions.zero(),

                // temperature
                DensityFunctions.constant(0.0),

                // vegetation
                DensityFunctions.constant(0.0),

                // continents
                DensityFunctions.constant(0.0),

                // erosion
                DensityFunctions.constant(0.0),

                // depth
                DensityFunctions.constant(0.0),

                // ridges
                DensityFunctions.constant(0.0),

                // initial density
                terrain,

                // final density
                terrain,

                // vein toggle
                DensityFunctions.zero(),

                // vein ridged
                DensityFunctions.zero(),

                // vein gap
                DensityFunctions.zero()
        );
    }


    private static DensityFunction createLunarTerrain(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<NormalNoise.NoiseParameters> noises =
                context.lookup(Registries.NOISE);
        /*
         * Large-scale elevation.
         */
        DensityFunction largeTerrain =
                DensityFunctions.mul(
                        DensityFunctions.noise(
                                noises.getOrThrow(LARGE_TERRAIN),
                                0.02d,
                                0.15d
                        ),
                        DensityFunctions.constant(96.0)
                );


        /*
         * Medium terrain variation.
         */
        DensityFunction mediumTerrain =
                DensityFunctions.mul(
                        DensityFunctions.noise(
                                noises.getOrThrow(MEDIUM_TERRAIN),
                                0.3d,
                                0.8d
                        ),
                        DensityFunctions.constant(35.0)
                );


        /*
         * Small surface variation.
         */
        DensityFunction smallTerrain =
                DensityFunctions.mul(
                        DensityFunctions.noise(
                                noises.getOrThrow(SMALL_TERRAIN),
                                0.09d,
                                0.35d
                        ),
                        DensityFunctions.constant(10.0)
                );


        /*
         * Combine normal lunar terrain.
         */
        DensityFunction terrain =
                DensityFunctions.add(
                        largeTerrain,
                        DensityFunctions.add(
                                mediumTerrain,
                                smallTerrain
                        )
                );


        /*
         * Convert height into density.
         *
         * Positive = solid
         * Negative = air
         *
         * Surface is approximately Y=100.
         */
        DensityFunction height =
                DensityFunctions.add(
                        DensityFunctions.constant(-64.0),
                        terrain
                );


        /*
         * Convert Y into a density value.
         *
         * At Y=surface:
         *
         *      100 - Y = 0
         *
         * Below surface:
         *
         *      positive
         *
         * Above surface:
         *
         *      negative
         */
        DensityFunction baseDensity =
                DensityFunctions.add(
                        height,
                        DensityFunctions.mul(
                                DensityFunctions.yClampedGradient(
                                        -64,
                                        320,
                                        1.0,
                                        -1.0
                                ),
                                DensityFunctions.constant(384.0)
                        )
                );


        /*
         * Impact craters.
         */
        DensityFunction craters = createCraters(noises);


        return DensityFunctions.add(
                baseDensity,
                craters
        );
    }

    private static DensityFunction createCraterField(
            Holder<NormalNoise.NoiseParameters> craterNoiseParams,
            double frequency,
            double depth
    ) {

        DensityFunction craterNoise =
                DensityFunctions.noise(craterNoiseParams,
                        frequency,
                        frequency
                );

        /*
         * Convert noise into sharp crater-like depressions.
         *
         * clamp(-1, 0) means:
         *
         *      positive noise -> 0
         *      negative noise -> negative
         */
        DensityFunction depression =
                DensityFunctions.min(
                        craterNoise,
                        DensityFunctions.constant(0.0)
                );


        /*
         * Increase contrast.
         */
        depression =
                DensityFunctions.mul(
                        depression,
                        DensityFunctions.constant(4.0)
                );


        /*
         * Make the crater depth.
         */
        return DensityFunctions.mul(
                depression,
                DensityFunctions.constant(depth)
        );
    }

    private static DensityFunction createCraters(HolderGetter<NormalNoise.NoiseParameters> noises) {

        DensityFunction large =
                createCraterField(
                        noises.getOrThrow(LARGE_CRATER),
                        0.07,
                        36.0
                );

        DensityFunction medium =
                createCraterField(
                        noises.getOrThrow(MEDIUM_CRATER),
                        0.2,
                        16.0
                );

        DensityFunction small =
                createCraterField(
                        noises.getOrThrow(SMALL_CRATER),
                        0.6,
                        7.0
                );

        return DensityFunctions.add(
                large,
                DensityFunctions.add(
                        medium,
                        small
                )
        );
    }
}
