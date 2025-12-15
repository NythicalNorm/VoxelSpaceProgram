package com.nythicalnorm.nythicalSpaceProgram.planettexgen;

import java.awt.*;

public class BiomeGradient {
    float minValue;
    float maxValue;
    float minLatitude;
    float maxLatitude;
    Color biomeColor;
    float latitudeOpacity;

    public BiomeGradient(float minValue, float maxValue, float minLatitude, float maxLatitude, float latitudeOpacity, Color biomeColor) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.biomeColor = biomeColor;
        this.latitudeOpacity = latitudeOpacity;
    }

    public void adjustMinMaxValBasedOnGroup(float minGroup, float maxGroup) {
        float range = Math.abs(maxGroup - minGroup);
        this.minValue = minGroup + this.minValue*range;
        this.maxValue = minGroup + this.maxValue*range;
    }

    public boolean isValueInRange(float noiseValue) {
        return noiseValue <= this.maxValue && noiseValue > this.minValue;
    }

    public Color getBiomeColor() {
        return biomeColor;
    }
}
