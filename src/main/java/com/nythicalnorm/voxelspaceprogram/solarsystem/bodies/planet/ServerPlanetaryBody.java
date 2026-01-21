package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ServerCelestialBody;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ServerPlanetaryBody extends PlanetaryBody implements ServerCelestialBody {
    CompletableFuture<byte[]> planetTexBytes;
    Path planetFolder;

    public ServerPlanetaryBody(PlanetBuilder planetBuilder) {
        super(planetBuilder);
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
