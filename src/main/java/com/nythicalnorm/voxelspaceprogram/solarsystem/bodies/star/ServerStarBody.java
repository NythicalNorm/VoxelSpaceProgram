package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;
import net.minecraft.server.level.ServerLevel;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ServerStarBody extends StarBody implements ServerCelestialBody {
    CompletableFuture<byte[]> planetTexBytes;
    Path planetFolder;
    File planetDataFile;
    ServerLevel level;

    public ServerStarBody(StarBuilder starBuilder) {
        super(starBuilder);
    }

    @Override
    public CompletableFuture<byte[]> getPlanetMainTexBytes() {
        return planetTexBytes;
    }

    @Override
    public void setPlanetMainTexBytes(CompletableFuture<byte[]> bytes) {
        planetTexBytes = bytes;
    }

    @Override
    public Path getPlanetTextureFolder() {
        return planetFolder;
    }

    @Override
    public void setPlanetTextureFolder(Path folder) {
        planetFolder = folder;
    }

    @Override
    public File getPlanetDataFile() {
        return planetDataFile;
    }

    @Override
    public void setPlanetDataFile(File dataFile) {
        this.planetDataFile = dataFile;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(ServerLevel level) {
        this.level = level;
    }
}
