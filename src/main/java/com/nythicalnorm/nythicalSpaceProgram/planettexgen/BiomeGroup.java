package com.nythicalnorm.nythicalSpaceProgram.planettexgen;

import java.awt.*;

public class BiomeGroup {
    private final String name;
    private final  BiomeGradient[] biomeGradients;
    private final float minValue;
    private final float maxValue;

    public BiomeGroup(String name,float minValue, float maxValue, BiomeGradient[] biomeGradients) {
        this.name = name;
        this.biomeGradients = biomeGradients;
        this.minValue = minValue;
        this.maxValue = maxValue;

        for (BiomeGradient biomeGradient : this.biomeGradients) {
            biomeGradient.adjustMinMaxValBasedOnGroup(this.minValue, this.maxValue);
        }
    }

    public BiomeGradient[] getBiomeGradients() {
        return biomeGradients;
    }

    public boolean isValueInRange(float noiseValue) {
        return noiseValue <= this.maxValue && noiseValue > this.minValue;
    }
}
