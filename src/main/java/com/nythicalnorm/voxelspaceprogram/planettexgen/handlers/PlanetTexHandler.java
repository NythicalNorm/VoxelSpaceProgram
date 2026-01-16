package com.nythicalnorm.voxelspaceprogram.planettexgen.handlers;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.network.ClientboundPlanetTexturePacket;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.planettexgen.GradientSupplier;
import com.nythicalnorm.voxelspaceprogram.planettexgen.biometex.BiomeTexGenTask;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class PlanetTexHandler {
    private static final String modSaveDirPath = "voxelspaceprogram";
    private static final String planetTexDirPath = "planets";

    private static File modDir;
    private static File planetDir;
    private static ExecutorService texExecuter;

    HashMap<OrbitId, CompletableFuture<byte[]>> planetTexturesBytes;

    public void loadOrCreatePlanetTex(MinecraftServer server, PlanetsProvider planets) {
        Path modSubFolder = server.getWorldPath(LevelResource.ROOT).resolve(modSaveDirPath);
        modDir = getOrCreateDir(modSubFolder);

        Path planetDir = modSubFolder.resolve(planetTexDirPath);
        PlanetTexHandler.planetDir = getOrCreateDir(planetDir);

        if (modSubFolder == null || PlanetTexHandler.planetDir == null) {
            return;
        }

        RandomSource randomSource = RandomSource.create(server.getLevel(Level.OVERWORLD).getSeed());
        planetTexturesBytes = new HashMap<>();
        texExecuter = Executors.newSingleThreadExecutor();

        server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.planetgen_start"), true);

        for (PlanetaryBody planetaryBody : planets.getAllPlanetaryBodies().values()) {
            CompletableFuture<byte[]> planetImgData = CompletableFuture.supplyAsync(
                    new WholePlanetTexGenTask(planetDir, planetaryBody.getName(), randomSource.nextLong(), GradientSupplier.textureForPlanets.get(planetaryBody.getName())), texExecuter);
            planetTexturesBytes.put(planetaryBody.getOrbitId(), planetImgData);

            planetImgData.thenRun(() -> {
                server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.planetgen_end",
                        planetaryBody.getName()), true);
            });
        }
    }

    public static ExecutorService getTexExecuter() {
        return texExecuter;
    }


    public static void sendBiomeTexToPlayer(ServerPlayer player, PlanetaryBody playerOnPlanet) {
        Vec3 plrPos = player.position();
        int texSize = 3;

        double cellSize = Calcs.getSquareCellSize(playerOnPlanet.getRadius());
        int sizeMultiplier = (int) Math.pow(32, texSize);
        int texturePixelSize = (int) Math.ceil(cellSize / sizeMultiplier);

        int xIndex = Calcs.getCellIndex(texturePixelSize, plrPos.x);
        int zIndex = Calcs.getCellIndex(texturePixelSize, plrPos.z);
        File biomeTexLocation = getFilePath(playerOnPlanet.getName(), planetDir.toPath(), texSize, xIndex, zIndex);

        CompletableFuture.supplyAsync(
                new BiomeTexGenTask(player, texSize,  xIndex, zIndex, texturePixelSize, biomeTexLocation), texExecuter);
    }

    private static File getFilePath(String planetName, Path rootDir, int texSize, int xIndex, int zIndex) {

        String fileName = planetName + "_" + texSize + "_" + xIndex + "_" + zIndex;
        Path biomeTexPath = rootDir.resolve(fileName + ".png");
        return new File(biomeTexPath.toUri());
    }

    public void sendAllTexToPlayer(ServerPlayer player) {
        for (Map.Entry<OrbitId, CompletableFuture<byte[]>> texfuture : planetTexturesBytes.entrySet()) {
            texfuture.getValue().thenAccept(texBytes -> sendToPlayer(player, texfuture.getKey(), texBytes));
        }
    }

    private void sendToPlayer(ServerPlayer player, OrbitId planetID, byte[] texture) {
        PacketHandler.sendToPlayer(new ClientboundPlanetTexturePacket(planetID, texture), player);
    }

    private File getOrCreateDir (Path folderPath) {
        File folderDir = new File(folderPath.toUri());

        if (!folderDir.exists()) {
            boolean wasCreated = folderDir.mkdir();
            if (!wasCreated) {
                VoxelSpaceProgram.logError(folderDir.getPath() + " Directory Creation Failed.");
                return null;
            }
        }
        return folderDir;
    }
}
