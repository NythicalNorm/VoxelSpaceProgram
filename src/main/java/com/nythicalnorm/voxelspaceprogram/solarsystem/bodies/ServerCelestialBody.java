package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface ServerCelestialBody {
    public String getName();
    public CompletableFuture<byte[]> getPlanetMainTexBytes();
    public void setPlanetMainTexBytes(CompletableFuture<byte[]> bytes);
    public Path getPlanetFolder();
    public void setPlanetFolder(Path file);
}
