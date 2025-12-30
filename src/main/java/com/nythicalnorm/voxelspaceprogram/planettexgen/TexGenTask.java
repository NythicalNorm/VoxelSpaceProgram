package com.nythicalnorm.voxelspaceprogram.planettexgen;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public abstract class TexGenTask  implements Supplier<byte[]> {
    public static byte[] convertBufferedImageToPngBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, "png", outputStream);

            if (!success) {
                VoxelSpaceProgram.logError("There is no png writer in this environment, Well then this mod won't work now will it?");
                return null;
            }

            return outputStream.toByteArray();

        } catch (IOException e) {
            VoxelSpaceProgram.logError("Error writing planet texture to buffer");
            throw e;
        }
    }
}
