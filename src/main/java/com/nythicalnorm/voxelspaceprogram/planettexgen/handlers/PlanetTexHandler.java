package com.nythicalnorm.voxelspaceprogram.planettexgen.handlers;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.network.textures.ClientboundPlanetTexturePacket;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.planettexgen.GradientSupplier;
import com.nythicalnorm.voxelspaceprogram.planettexgen.biometex.BiomeTexGenTask;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
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
import java.util.Map;
import java.util.concurrent.*;

public class PlanetTexHandler {
    private static final String modSaveDirPath = "voxelspaceprogram";
    private static final String planetDirPath = "planets";

    private static File planetsDir;
    private static ExecutorService texExecuter;

    public void loadOrCreatePlanetTex(MinecraftServer server, PlanetsProvider planets) {
        Path modSubFolder = server.getWorldPath(LevelResource.ROOT).resolve(modSaveDirPath);
        getOrCreateDir(modSubFolder);

        Path planetsPath = modSubFolder.resolve(planetDirPath);
        PlanetTexHandler.planetsDir = getOrCreateDir(planetsPath);

        if (PlanetTexHandler.planetsDir == null) {
            return;
        }

        RandomSource randomSource = RandomSource.create(server.getLevel(Level.OVERWORLD).getSeed());
        texExecuter = Executors.newSingleThreadExecutor();

        server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.planetgen_start"), true);

        for (CelestialBody celestialBody : planets.getAllPlanetaryBodies().values()) {
            ServerCelestialBody serverCelestialBody = (ServerCelestialBody) celestialBody;
            Path celestialBodyDir = planetsPath.resolve(serverCelestialBody.getName());
            getOrCreateDir(celestialBodyDir);

            CompletableFuture<byte[]> planetImgData = CompletableFuture.supplyAsync(
                    new WholePlanetTexGenTask(celestialBodyDir, serverCelestialBody.getName(), randomSource.nextLong(), GradientSupplier.textureForPlanets.get(serverCelestialBody.getName())), texExecuter);

            serverCelestialBody.setPlanetFolder(celestialBodyDir);
            serverCelestialBody.setPlanetMainTexBytes(planetImgData);

            planetImgData.thenRun(() -> {
                server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.planetgen_end",
                        serverCelestialBody.getName()), true);
            });
        }
    }

    public static ExecutorService getTexExecuter() {
        return texExecuter;
    }


    public static void sendBiomeTexToPlayer(ServerPlayer player, CelestialBody playerOnPlanet) {
        Vec3 plrPos = player.position();
        int texSize = 3;

        double cellSize = Calcs.getSquareCellSize(playerOnPlanet.getRadius());
        int sizeMultiplier = (int) Math.pow(32, texSize);
        int texturePixelSize = (int) Math.ceil(cellSize / sizeMultiplier);

        int xIndex = Calcs.getCellIndex(texturePixelSize, plrPos.x);
        int zIndex = Calcs.getCellIndex(texturePixelSize, plrPos.z);
        File biomeTexLocation = getFilePath(playerOnPlanet.getName(), planetsDir.toPath(), texSize, xIndex, zIndex);

        CompletableFuture.supplyAsync(
                new BiomeTexGenTask(player, texSize,  xIndex, zIndex, texturePixelSize, biomeTexLocation), texExecuter);
    }

    private static File getFilePath(String planetName, Path rootDir, int texSize, int xIndex, int zIndex) {

        String fileName = planetName + "_" + texSize + "_" + xIndex + "_" + zIndex;
        Path biomeTexPath = rootDir.resolve(fileName + ".png");
        return new File(biomeTexPath.toUri());
    }

    public void sendAllTexToPlayer(ServerPlayer player, Map<OrbitId, CelestialBody> allPlanetaryBodies) {
        for (CelestialBody celestialBody : allPlanetaryBodies.values()) {
            ((ServerCelestialBody)celestialBody).getPlanetMainTexBytes().thenAccept(texBytes -> sendToPlayer(player,
                    celestialBody.getOrbitId(), texBytes));
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
