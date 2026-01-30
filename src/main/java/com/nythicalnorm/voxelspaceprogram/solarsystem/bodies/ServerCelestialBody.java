package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface ServerCelestialBody {
    default void initServerPlanet() {
        if (SolarSystem.get() != null) {
            setLevel(SolarSystem.get().getServer().getLevel(getDimension()));
        }
    }

    String getName();
    CompletableFuture<byte[]> getPlanetMainTexBytes();
    void setPlanetMainTexBytes(CompletableFuture<byte[]> bytes);
    Path getPlanetTextureFolder();
    void setPlanetTextureFolder(Path file);
    File getPlanetDataFile();
    void setPlanetDataFile(File dataFile);
    ResourceKey<Level> getDimension();
    ServerLevel getLevel();
    void setLevel(ServerLevel level);
}
