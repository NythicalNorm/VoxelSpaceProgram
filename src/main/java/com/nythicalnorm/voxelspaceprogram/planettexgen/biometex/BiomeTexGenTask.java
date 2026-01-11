package com.nythicalnorm.voxelspaceprogram.planettexgen.biometex;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.planettexgen.TexGenTask;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class BiomeTexGenTask extends TexGenTask {
    public static final int textureResolution = 256;
    public static final int textureSizeJumpExponent = 32;

    private final ServerPlayer player;
    private final int texSizeIndex;
    private final int xIndex;
    private final int zIndex;
    private final int texturePixelSize;
    private final File biomeTexLoc;

    public BiomeTexGenTask(ServerPlayer player, int texSize, int xIndex, int zIndex, int texturePixelSize, File saveFilePath) {
        this.player = player;
        this.texSizeIndex = texSize;
        this.xIndex = xIndex;
        this.zIndex = zIndex;
        this.texturePixelSize = texturePixelSize;
        this.biomeTexLoc = saveFilePath;
    }

    public BufferedImage generateBiomeTex() {
        BufferedImage genTexture = new BufferedImage(textureResolution, textureResolution, BufferedImage.TYPE_INT_RGB);
        long beforeTimes = Util.getNanos();
        //int cellSizeWithResolution = texturePixelSize * textureResolution;

        double minPosX = xIndex*texturePixelSize - ((double) texturePixelSize /2);
        double minPosZ = zIndex*texturePixelSize - ((double) texturePixelSize /2);

        for (int x = 0; x < textureResolution; x++) {
            for (int z = 0; z < textureResolution; z++) {


                BlockPos pos = getBlockPosForImage(x, z, minPosX, minPosZ, texturePixelSize);
                Holder<Biome> biomeAtPos = player.level().getUncachedNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()));
                int BiomeColor = BiomeColorHolder.getColorForBiome(biomeAtPos.unwrapKey());
                genTexture.setRGB(x, z, BiomeColor);
            }
        }

        long currentTime = Util.getNanos() - beforeTimes;
        VoxelSpaceProgram.log("time Took for biomeTex: " + currentTime);
        return genTexture;
    }

    private BlockPos getBlockPosForImage(int x, int z, double minPosX, double minPosZ, int texturePixelSize) {
        double xDist = minPosX + (((float)x / textureResolution) * texturePixelSize);
        double zDist = minPosZ + (((float)z / textureResolution) * texturePixelSize);
        return new BlockPos((int) Math.floor(xDist), 128,(int)  Math.floor(zDist));
    }

    private static BlockPos getBlockPosForImages(int x, int z, BlockPos centerChunkPos, int size) {
        int Xdist = (x - textureResolution/2) * (int) (Math.pow(textureSizeJumpExponent, size));
        int Zdist = (z - textureResolution/2) * (int) (Math.pow(textureSizeJumpExponent, size));

        return new BlockPos(centerChunkPos.getX() + (Xdist * 16), 128, centerChunkPos.getZ() + (Zdist * 16));
    }

    @Override
    public byte[] get() {
        if (!biomeTexLoc.exists()) {
            BufferedImage planetMap = generateBiomeTex();

            try (FileOutputStream fileWriter = new FileOutputStream(biomeTexLoc)) {
                byte[] imageBytes = convertBufferedImageToPngBytes(planetMap);
                fileWriter.write(imageBytes);
                return imageBytes;
            } catch (IOException e) {
                VoxelSpaceProgram.logError("Can't write " + " Textures to file");
            }
        } else {
            try {
                return Files.readAllBytes(biomeTexLoc.toPath());
            } catch (IOException e) {
                VoxelSpaceProgram.logError("Can't load " + " planet's Textures");
            }
        }
        return null;
    }
}
