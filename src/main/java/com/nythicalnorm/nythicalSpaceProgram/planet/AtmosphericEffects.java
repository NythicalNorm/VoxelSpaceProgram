package com.nythicalnorm.nythicalSpaceProgram.planet;

import org.joml.Vector3f;

public class AtmosphericEffects {
    private final boolean hasAtmosphere;
    private final Vector3f colorTransitionOne;
    private final Vector3f colorTransitionTwo;
    private final double atmosphereHeight;
    private final float exposureNight;
    private final float exposureDay;

    public AtmosphericEffects(boolean hasAtmosphere, Vector3f colorTransitionOne, Vector3f colorTransitionTwo, double atmosphereHeight, float exposureNight, float exposureDay) {
        this.hasAtmosphere = hasAtmosphere;
        this.colorTransitionOne = colorTransitionOne;
        this.colorTransitionTwo = colorTransitionTwo;
        this.atmosphereHeight = atmosphereHeight;
        this.exposureNight = exposureNight;
        this.exposureDay = exposureDay;
    }
}
