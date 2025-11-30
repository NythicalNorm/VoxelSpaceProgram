package com.nythicalnorm.nythicalSpaceProgram.planet;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class PlanetAtmosphere {
    private final boolean hasAtmosphere;
    private final float[] colorTransitionOne;
    private final float[] colorTransitionTwo;
    private final double atmosphereHeight;
    private final float exposureNight;
    private final float exposureDay;

    public PlanetAtmosphere(boolean hasAtmosphere, float[] colorTransitionOne, float[] colorTransitionTwo, double atmosphereHeight, float exposureNight, float exposureDay) {
        this.hasAtmosphere = hasAtmosphere;
        this.colorTransitionOne = colorTransitionOne;
        this.colorTransitionTwo = colorTransitionTwo;
        this.atmosphereHeight = atmosphereHeight;
        this.exposureNight = exposureNight;
        this.exposureDay = exposureDay;
    }

    public boolean hasAtmosphere() {
        return hasAtmosphere;
    }

    public float[] getColorTransitionOne() {
        return colorTransitionOne;
    }

    public float[] getColorTransitionTwo() {
        return colorTransitionTwo;
    }

    public double getAtmosphereHeight() {
        return atmosphereHeight;
    }

    public float getExposureNight() {
        return exposureNight;
    }

    public float getExposureDay() {
        return exposureDay;
    }
}
