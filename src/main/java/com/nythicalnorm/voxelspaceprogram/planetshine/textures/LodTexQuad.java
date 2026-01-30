package com.nythicalnorm.voxelspaceprogram.planetshine.textures;

import org.joml.Vector2i;

public class LodTexQuad {
    private final int texAtlasIndex;
    private final Vector2i texAtlasCoords;
    private final int textureInfo;

    public LodTexQuad(int texAtlasIndex, Vector2i texAtlasCoords, int textureInfo) {
        this.texAtlasIndex = texAtlasIndex;
        this.texAtlasCoords = texAtlasCoords;
        this.textureInfo = textureInfo;
    }

    public int getTexAtlasIndex() {
        return texAtlasIndex;
    }

    public int getTextureInfo() {
        return textureInfo;
    }

    public Vector2i getTexAtlasCoords() {
        return texAtlasCoords;
    }
}
