package com.nythicalnorm.voxelspaceprogram.planettexgen.handlers;

import com.nythicalnorm.voxelspaceprogram.network.textures.ClientboundLodTexturePacket;
import com.nythicalnorm.voxelspaceprogram.network.textures.ClientboundPlanetTexturePacket;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.planettexgen.GradientSupplier;
import com.nythicalnorm.voxelspaceprogram.planettexgen.lod_tex.LodTexGenTask;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
import com.nythicalnorm.voxelspaceprogram.storage.SpacecraftDataStorage;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.*;

public class PlanetTexHandler {
    private static final String planetTexturesPath = "textures";

    private static File planetsDir;
    private static ExecutorService texExecuter;

    public void loadOrCreatePlanetTex(MinecraftServer server, PlanetsProvider planets, Path modSaveFolder) {

        Path planetsTexturesPath = modSaveFolder.resolve(planetTexturesPath);
        PlanetTexHandler.planetsDir = SpacecraftDataStorage.getOrCreateDir(planetsTexturesPath);

        if (PlanetTexHandler.planetsDir == null) {
            return;
        }

        RandomSource randomSource = RandomSource.create(server.getLevel(Level.OVERWORLD).getSeed());
        texExecuter = Executors.newSingleThreadExecutor();

        server.getPlayerList().broadcastSystemMessage(Component.translatable("voxelspaceprogram.state.planetgen_start"), true);

        for (CelestialBody celestialBody : planets.getAllPlanetaryBodies().values()) {
            ServerCelestialBody serverCelestialBody = (ServerCelestialBody) celestialBody;
            Path celestialBodyDir = planetsTexturesPath.resolve(serverCelestialBody.getName());
            SpacecraftDataStorage.getOrCreateDir(celestialBodyDir);

            CompletableFuture<byte[]> planetImgData = CompletableFuture.supplyAsync(
                    new WholePlanetTexGenTask(celestialBodyDir, serverCelestialBody.getName(), randomSource, GradientSupplier.textureForPlanets.get(serverCelestialBody.getName())), texExecuter);

            serverCelestialBody.setPlanetTextureFolder(celestialBodyDir);
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
        if (playerOnPlanet == null) {
            return;
        }

        Vec3 plrPos = player.position();
        int texSize = 3;

        double cellSize = Calcs.getSquareCellSize(playerOnPlanet.getRadius());
        int sizeMultiplier = (int) Math.pow(32, texSize);
        int texturePixelSize = (int) Math.ceil(cellSize / 1024f); // sizeMultiplier);

        int xIndex = Calcs.getCellIndex(texturePixelSize, plrPos.x);
        int zIndex = Calcs.getCellIndex(texturePixelSize, plrPos.z);

        File biomeTexLocation = getFilePath(((ServerCelestialBody)playerOnPlanet).getPlanetTextureFolder(), texSize, xIndex, zIndex);

        CompletableFuture<byte[]> biomeTex = CompletableFuture.supplyAsync(
                new LodTexGenTask(player, texSize,  xIndex, zIndex, texturePixelSize, biomeTexLocation), texExecuter);

        int index = xIndex*1024+zIndex;

        biomeTex.thenAccept(texBytes -> {
            PacketHandler.sendToPlayer(new ClientboundLodTexturePacket(playerOnPlanet.getDimension(), index, sizeMultiplier, texBytes), player);
        });
    }

    private static File getFilePath(Path rootDir, int texSize, int xIndex, int zIndex) {

        String fileName = texSize + "_" + xIndex + "_" + zIndex;
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
}
