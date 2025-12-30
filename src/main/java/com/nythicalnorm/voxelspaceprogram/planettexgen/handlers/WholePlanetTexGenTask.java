package com.nythicalnorm.voxelspaceprogram.planettexgen.handlers;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.planettexgen.PlanetGradient;
import com.nythicalnorm.voxelspaceprogram.planettexgen.PlanetMapGen;
import com.nythicalnorm.voxelspaceprogram.planettexgen.TexGenTask;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WholePlanetTexGenTask extends TexGenTask {
    private final Path planetsDir;
    private final String planetName;
    private final long seed;
    private final PlanetGradient gradient;

    public WholePlanetTexGenTask(Path planetsDir, String planetName, long seed, PlanetGradient gradient) {
        this.planetsDir = planetsDir;
        this.planetName = planetName;
        this.seed = seed;
        this.gradient = gradient;
    }

    @Override
    public byte[] get() {
        Path planetTexPath = planetsDir.resolve(planetName + ".png");
        File planetTexFileLocation = new File(planetTexPath.toUri());

        if (!planetTexFileLocation.exists()) {
            BufferedImage planetMap = PlanetMapGen.GenerateMap(seed, gradient);

            try (FileOutputStream fileWriter = new FileOutputStream(planetTexFileLocation)) {
                byte[] imageBytes = convertBufferedImageToPngBytes(planetMap);
                fileWriter.write(imageBytes);
                return imageBytes;
            } catch (IOException e) {
                VoxelSpaceProgram.logError("Can't write " + planetName + " planet's Textures to file");
            }
        } else {
            try {
                return Files.readAllBytes(planetTexPath);
            } catch (IOException e) {
                VoxelSpaceProgram.logError("Can't load " + planetName + " planet's Textures");
            }
        }
        return null;
    }
}
