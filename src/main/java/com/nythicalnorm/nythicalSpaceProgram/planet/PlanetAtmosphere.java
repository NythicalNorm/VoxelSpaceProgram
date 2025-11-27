package com.nythicalnorm.nythicalSpaceProgram.planet;

import org.joml.Vector3f;

public class PlanetAtmosphere {
    private final boolean hasAtmosphere;
    private final Vector3f colorTransitionOne;
    private final Vector3f colorTransitionTwo;
    private final double atmosphereHeight;
    private final float exposureNight;
    private final float exposureDay;

    public PlanetAtmosphere(boolean hasAtmosphere, Vector3f colorTransitionOne, Vector3f colorTransitionTwo, double atmosphereHeight, float exposureNight, float exposureDay) {
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

    public Vector3f getColorTransitionOne() {
        return colorTransitionOne;
    }

    public Vector3f getColorTransitionTwo() {
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
