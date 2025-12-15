package com.nythicalnorm.nythicalSpaceProgram.planettexgen;

import org.joml.Vector3f;

import java.awt.image.BufferedImage;

public abstract class PlanetFeatures {
    public BufferedImage featureMask;

    public PlanetFeatures(int imageLength) {
        featureMask = new BufferedImage(imageLength, imageLength, BufferedImage.TYPE_INT_ARGB);
    }

    public abstract void initGen();

    public abstract float preGenerateAt(float u, float v, float squareSides, Vector3f SpherePos, float baseHeight);
}
