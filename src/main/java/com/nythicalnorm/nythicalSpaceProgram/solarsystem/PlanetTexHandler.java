package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planettexgen.GradientSupplier;
import com.nythicalnorm.nythicalSpaceProgram.planettexgen.PlanetMapGen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class PlanetTexHandler {
    private static final String modSaveDirPath = "nythicalspaceprogram";
    private static final String planetTexDirPath = "planets";

    private static File modDir;
    private static File planettexDir;

    public void loadOrCreateTex(MinecraftServer server, Planets planets) {
        try {
            Path modSubFolder = server.getWorldPath(LevelResource.ROOT).resolve(modSaveDirPath);
            modDir = new File(modSubFolder.toUri());

            if (!modDir.exists()) {
                boolean wasCreated = modDir.mkdir();
                if (!wasCreated) {
                    NythicalSpaceProgram.logError("Mod Directory Creation Failed.");
                    return;
                }
            }
            Path planetDir = modSubFolder.resolve(planetTexDirPath);
            planettexDir = new File(planetDir.toUri());

            if (!planettexDir.exists()) {
                boolean wasCreated = planettexDir.mkdir();
                if (!wasCreated) {
                    NythicalSpaceProgram.logError("Planet Directory Creation Failed.");
                    return;
                }
            }
            createPlanetTextures(server, planetDir, planets);

        } catch (IOException e) {
            NythicalSpaceProgram.logError(e.toString());
        }
    }

    private void createPlanetTextures(MinecraftServer server, Path planetDir, Planets planets) throws IOException {
        RandomSource randomSource = RandomSource.create(server.getLevel(Level.OVERWORLD).getSeed());
        BufferedImage planetMap = PlanetMapGen.GenerateMap(randomSource.nextLong(), GradientSupplier.NILA_GRADIENT);
        byte[] imageBytes = convertBufferedImageToPngBytes(planetMap);

        Path planetTexPath = planetDir.resolve("nila.png");
        File planetTexFileLocation = new File(planetTexPath.toUri());

        try (FileOutputStream fileWriter = new FileOutputStream(planetTexFileLocation)) {
            fileWriter.write(imageBytes);
        } catch (IOException e) {
            NythicalSpaceProgram.logError("Can't write Planet Textures to file");;
        }
    }

    public static byte[] convertBufferedImageToPngBytes(BufferedImage image) throws IOException {
        // Use a ByteArrayOutputStream to write the image data to an in-memory buffer
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Write the BufferedImage to the ByteArrayOutputStream in PNG format
            boolean success = ImageIO.write(image, "png", outputStream);

            if (!success) {
                // Handle cases where no appropriate writer is found
                NythicalSpaceProgram.logError("There is no png writer in this environment, Well then this mod won't work now will it?");
                return null;
            }

            // Get the byte array containing the PNG data
            byte[] imageBytes = outputStream.toByteArray();
            return imageBytes;

        } catch (IOException e) {
            NythicalSpaceProgram.logError("Error writing planet texture to buffer");
            throw e;
        } finally {
            outputStream.close();
        }
    }
}
