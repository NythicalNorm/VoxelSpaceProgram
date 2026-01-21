package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ServerStarBody extends StarBody implements ServerCelestialBody {
    CompletableFuture<byte[]> planetTexBytes;
    Path planetFolder;

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
    public Path getPlanetFolder() {
        return planetFolder;
    }

    @Override
    public void setPlanetFolder(Path folder) {
        planetFolder = folder;
    }
}
