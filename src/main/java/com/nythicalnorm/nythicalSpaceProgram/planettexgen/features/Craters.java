package com.nythicalnorm.nythicalSpaceProgram.planettexgen.features;

import com.nythicalnorm.nythicalSpaceProgram.planettexgen.PlanetFeatures;
import org.joml.Vector3f;

public class Craters extends PlanetFeatures {

    public Craters(int imageLength) {
        super(imageLength);
    }

    public void initGen() {

    }

    @Override
    public float preGenerateAt(float u, float v, float squareSides, Vector3f SpherePos, float baseHeight) {
        return 0;
    }

    private class potentialCrater {
        Vector3f spherePos;
        float baseHeight;
        float size;
    }
}
