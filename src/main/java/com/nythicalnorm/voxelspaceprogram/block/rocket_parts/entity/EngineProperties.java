package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity;

public class EngineProperties {
    private final int blockSize;
    private final float pixelHeight;
    private final float pixelWidth;

    public EngineProperties(Builder builder) {
        this.blockSize = builder.blockSize;
        this.pixelHeight = builder.pixelHeight;
        this.pixelWidth = builder.pixelWidth;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public float getPixelHeight() {
        return pixelHeight;
    }

    public float getPixelWidth() {
        return pixelWidth;
    }

    public static class Builder {
        private int blockSize;
        private float pixelHeight;
        private float pixelWidth;

        public Builder BlockSize(int size) {
            this.blockSize = size;
            return this;
        }

        public Builder PixelHeight(int height) {
            this.pixelHeight = height;
            return this;
        }

        public Builder PixelWidth(int width) {
            this.pixelWidth = width;
            return this;
        }

        public EngineProperties build() {
            return new EngineProperties(this);
        }
    }
}
