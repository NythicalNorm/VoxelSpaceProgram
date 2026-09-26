package com.nythicalnorm.voxelspaceprogram.rendering.plumes;

public class PlumeSettings {
    private final double startYPixelOffset;
    private final double fadeOutLength;
    private final double startingWidth;
    private final double endSeaLevelWidth;
    private final double endVacuumWidth;
    private final int engineGlowColor;

    public PlumeSettings(double startYPixelOffset, double fadeOutLength, double startingWidth, double endSeaLevelWidth, double endVacuumWidth, int engineGlowColor) {
        this.startYPixelOffset = startYPixelOffset;
        this.fadeOutLength = fadeOutLength;
        this.startingWidth = startingWidth;
        this.endSeaLevelWidth = endSeaLevelWidth;
        this.endVacuumWidth = endVacuumWidth;
        this.engineGlowColor = engineGlowColor;
    }

    public double getStartYPixelOffset() {
        return startYPixelOffset;
    }

    public double getFadeOutLength() {
        return fadeOutLength;
    }

    public double getStartingWidth() {
        return startingWidth;
    }

    public double getEndSeaLevelWidth() {
        return endSeaLevelWidth;
    }

    public double getEndVacuumWidth() {
        return endVacuumWidth;
    }

    public int getEngineGlowColor() {
        return engineGlowColor;
    }
}
