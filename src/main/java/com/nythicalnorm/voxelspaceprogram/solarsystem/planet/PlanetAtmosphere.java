package com.nythicalnorm.voxelspaceprogram.solarsystem.planet;

public class PlanetAtmosphere {
    protected final boolean hasAtmosphere;
    protected final int overlayColor;
    protected final int atmoColor;
    protected final double atmosphereHeight;
    protected final float atmosphereAlpha;
    protected final float exposureNight;
    protected final float exposureDay;

    public PlanetAtmosphere(boolean hasAtmosphere, int overlayColor, int atmoColor, double atmosphereHeight, float atmosphereAlpha, float exposureNight, float exposureDay) {
        this.hasAtmosphere = hasAtmosphere;
        this.overlayColor = overlayColor;
        this.atmoColor = atmoColor;
        this.atmosphereHeight = atmosphereHeight;
        this.atmosphereAlpha = atmosphereAlpha;
        this.exposureNight = exposureNight;
        this.exposureDay = exposureDay;
    }

    public boolean hasAtmosphere() {
        return hasAtmosphere;
    }

    public float[] getOverlayColor(float alpha)
    {
        return getRGBAFloats(overlayColor, alpha);
    }

    public float[] getAtmoColor() {
        return getRGBAFloats(atmoColor, 1.0f);
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

    public float getAtmosphereAlpha() {
        return atmosphereAlpha;
    }

    private float[] getRGBAFloats(int val, float alpha) {
        float[] rgbaColor = new float[4];

        int red = (val >> 16) & 0xFF;
        int green = (val >> 8) & 0xFF;
        int blue = (val >> 0) & 0xFF;

        rgbaColor[0] = ((float)red)/255f;
        rgbaColor[1] = ((float)green)/255f;
        rgbaColor[2] = ((float)blue)/255f;
        rgbaColor[3] = alpha;

        return rgbaColor;
    }
}
