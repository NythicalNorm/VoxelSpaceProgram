package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

public class PlanetAtmosphere {
    protected final boolean hasAtmosphere;
    protected final int surfaceColor;
    protected final int atmoColor;
    protected final double atmosphereHeight;
    protected final float atmosphereAlpha;
    protected final float alphaNight;
    protected final float alphaDay;

    public PlanetAtmosphere(boolean hasAtmosphere, int surfaceColor, int atmoColor, double atmosphereHeight, float atmosphereAlpha, float alphaNight, float alphaDay) {
        this.hasAtmosphere = hasAtmosphere;
        this.surfaceColor = surfaceColor;
        this.atmoColor = atmoColor;
        this.atmosphereHeight = atmosphereHeight;
        this.atmosphereAlpha = atmosphereAlpha;
        this.alphaNight = alphaNight;
        this.alphaDay = alphaDay;
    }

    public boolean hasAtmosphere() {
        return hasAtmosphere;
    }

    public float[] getOverlayColor(float alpha)
    {
        return getRGBAFloats(surfaceColor, alpha);
    }

    public float[] getAtmoColor() {
        return getRGBAFloats(atmoColor, 1.0f);
    }

    public double getAtmosphereHeight() {
        return atmosphereHeight;
    }

    public float getAlphaNight() {
        return alphaNight;
    }

    public float getAlphaDay() {
        return alphaDay;
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
