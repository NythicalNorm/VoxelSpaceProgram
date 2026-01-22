package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface ServerCelestialBody {
    String getName();
    CompletableFuture<byte[]> getPlanetMainTexBytes();
    void setPlanetMainTexBytes(CompletableFuture<byte[]> bytes);
    Path getPlanetTextureFolder();
    void setPlanetTextureFolder(Path file);
    File getPlanetDataFile();
    void setPlanetDataFile(File dataFile);
}
