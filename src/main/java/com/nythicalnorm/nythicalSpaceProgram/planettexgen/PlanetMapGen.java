package com.nythicalnorm.nythicalSpaceProgram.planettexgen;


import org.joml.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlanetMapGen {
    private static final int size = 1024;
    private static final int imageLength = size * 3;

    public static BufferedImage GenerateMap(long seed, PlanetGradient gradient) {
        BufferedImage image = new BufferedImage(imageLength, imageLength, BufferedImage.TYPE_INT_ARGB);
        NoiseHandler ns = new NoiseHandler(5, seed);

        for (int side = 0; side < 6; side++){
            int xOffset = (side % 3) * size;
            int yOffset = (side / 3) * size;

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    float u = (float) x / size;
                    float v = (float) y / size;
                    Vector3f spherePos = NoiseHandler.getSquarePos(u,v,side);

                    float baseNoise = ns.getNoiseAt(spherePos);

                    Color imgColor = getMapColor(baseNoise, gradient);

                    image.setRGB(xOffset + x,yOffset + y, imgColor.getRGB());
                }
            }
        }
        return image;
    }

    private static Color getMapColor(float noiseVal, PlanetGradient planetGradient) {
        Color myColor = Color.BLACK;

        for (BiomeGroup group : planetGradient.biomes) {
            if (group.isValueInRange(noiseVal)) {
                for (BiomeGradient biomeGradient : group.getBiomeGradients()) {
                    if (biomeGradient.isValueInRange(noiseVal)) {
                        myColor = biomeGradient.getBiomeColor();
                    }
                }
            }
        }

        return myColor;
    }

    private static Color mixColors(Color color1, Color color2, double percent){
        if (percent > 1) {
            percent = 1;
        }

        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed()*percent + color2.getRed()*inverse_percent);
        int greenPart = (int) (color1.getGreen()*percent + color2.getGreen()*inverse_percent);
        int bluePart = (int) (color1.getBlue()*percent + color2.getBlue()*inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    private static int clampColor(int color) {
        if (color < 0){
            return 0;
        }
        else if (color > 255) {
            return 255;
        }
        return color;
    }
}
