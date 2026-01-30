package com.nythicalnorm.voxelspaceprogram.util;

import org.joml.Vector2i;

public class LodTexUtils {
    public static final int textureResolution = 256;
    public static final int texInOneAxisCount = 16;
    public static final int textureSizeJumpExponent = 32;

    public static Vector2i textureIndexToAtlasCoordinate(int texIndex) {
        int x = texIndex % texInOneAxisCount;
        int y = texIndex / texInOneAxisCount;
        return new Vector2i(x * textureResolution, y * textureResolution);
    }
}
