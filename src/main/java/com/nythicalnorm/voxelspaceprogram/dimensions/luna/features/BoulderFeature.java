package com.nythicalnorm.voxelspaceprogram.dimensions.luna.features;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class BoulderFeature extends Feature<BoulderConfiguration> {

    public BoulderFeature() {
        super(BoulderConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<BoulderConfiguration> context) {

        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BoulderConfiguration config = context.config();

        int radiusX = Mth.nextInt(
                random,
                config.minRadius(),
                config.maxRadius()
        );

        int radiusY = Mth.nextInt(
                random,
                config.minHeight(),
                config.maxHeight()
        );

        int radiusZ = Mth.nextInt(
                random,
                config.minRadius(),
                config.maxRadius()
        );

        int finalY = 320;

        // Find surface.
        for (int x = -(radiusX - 1); x <= (radiusX - 1); x++) {
            for (int z = -(radiusZ - 1); z <= (radiusZ - 1); z++) {
                int yNext = level.getHeight (
                    Heightmap.Types.WORLD_SURFACE_WG,
                    x + origin.getX(),
                    z + origin.getZ()
                );

                if (yNext < finalY) {
                    finalY = yNext;
                }
            }
        }

        BlockPos center = new BlockPos(
                origin.getX(),
                finalY,
                origin.getZ()
        );

        // Random rotation of the boulder.
        float rotation = random.nextFloat() * Mth.TWO_PI;

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int yOffset = 0; yOffset <= radiusY; yOffset++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {

                    double nx = x / (double) radiusX;
                    double ny = yOffset / (double) radiusY;
                    double nz = z / (double) radiusZ;

                    // Ellipsoid.
                    double distance =
                            nx * nx +
                                    ny * ny +
                                    nz * nz;

                    if (distance > 1.0)
                        continue;

                    // Rotate horizontally.
                    double rx =
                            x * Math.cos(rotation)
                                    - z * Math.sin(rotation);

                    double rz =
                            x * Math.sin(rotation)
                                    + z * Math.cos(rotation);

                    BlockPos pos = center.offset(
                            Mth.floor(rx),
                            yOffset,
                            Mth.floor(rz)
                    );

                    if (!level.ensureCanWrite(pos))
                        continue;

//                    int terrainYHeight = level.getHeight(
//                            Heightmap.Types.WORLD_SURFACE_WG,
//                            pos.getX(),
//                            pos.getZ()
//                    );;
//                    pos = new BlockPos(pos.getX(), terrainYHeight + yOffset, pos.getZ());

                    level.setBlock(
                            pos,
                            config.block().getState(random, pos),
                            2
                    );
                }
            }
        }

        return true;
    }
}