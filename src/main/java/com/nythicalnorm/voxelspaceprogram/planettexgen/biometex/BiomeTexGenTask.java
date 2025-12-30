package com.nythicalnorm.voxelspaceprogram.planettexgen.biometex;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.planettexgen.TexGenTask;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BiomeTexGenTask extends TexGenTask {
    public static final int textureResolution = 512;
    public static final int textureSizeJumpExponent = 32;
    private final ServerLevel level;
    private final BlockPos centerChunkPos;
    private final int size;
    private final Path planetTexDir;

    public BiomeTexGenTask(ServerLevel level, BlockPos centerChunkPos, int size, Path planetTexDir) {
        this.level = level;
        this.centerChunkPos = centerChunkPos;
        this.size = size;
        this.planetTexDir = planetTexDir;
    }


    public BufferedImage generateBiomeTex() {
        BufferedImage genTexture = new BufferedImage(textureResolution, textureResolution, BufferedImage.TYPE_INT_RGB);
        long beforeTimes = Util.getNanos();

        for (int x = 0; x < textureResolution; x++) {
            for (int z = 0; z < textureResolution; z++) {
                BlockPos pos = getBlockPosForImage(x, z, centerChunkPos, size);
                Holder<Biome> biomeAtPos = level.getUncachedNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
                int BiomeColor = BiomeColorHolder.getColorForBiome(biomeAtPos.unwrapKey());
                genTexture.setRGB(x,z, BiomeColor);
            }
        }

        long currentTime = Util.getNanos() - beforeTimes;
        VoxelSpaceProgram.log("time Took for biomeTex: " + currentTime);
        return genTexture;
    }

    private static BlockPos getBlockPosForImage(int x, int z, BlockPos centerChunkPos, int size) {
        int Xdist = (x - textureResolution/2) * (int) (Math.pow(textureSizeJumpExponent, size));
        int Zdist = (z - textureResolution/2) * (int) (Math.pow(textureSizeJumpExponent, size));

        return new BlockPos(centerChunkPos.getX() + (Xdist * 16), 128, centerChunkPos.getZ() + (Zdist * 16));
    }

    @Override
    public byte[] get() {
        Path planetTexPath = planetTexDir.resolve("bumi_0_1" + ".png");
        File planetTexFileLocation = new File(planetTexPath.toUri());

        if (!planetTexFileLocation.exists()) {
            BufferedImage planetMap = generateBiomeTex();

            try (FileOutputStream fileWriter = new FileOutputStream(planetTexFileLocation)) {
                byte[] imageBytes = convertBufferedImageToPngBytes(planetMap);
                fileWriter.write(imageBytes);
                return imageBytes;
            } catch (IOException e) {
                VoxelSpaceProgram.logError("Can't write " + " Textures to file");
            }
        } else {
            try {
                return Files.readAllBytes(planetTexPath);
            } catch (IOException e) {
                VoxelSpaceProgram.logError("Can't load " + " planet's Textures");
            }
        }
        return null;
    }
}
