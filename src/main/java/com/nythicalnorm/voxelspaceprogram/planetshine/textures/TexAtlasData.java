package com.nythicalnorm.voxelspaceprogram.planetshine.textures;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.util.LodTexUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.Map;

public class TexAtlasData {
    private final boolean[][] atlasInUse;
    private final Map<Vector2i, Vector2i> pos2AtlasPosMap;

    public TexAtlasData() {
        this.atlasInUse = new boolean[LodTexUtils.texInOneAxisCount][LodTexUtils.texInOneAxisCount];
        pos2AtlasPosMap = new Object2ObjectOpenHashMap<>(LodTexUtils.texInOneAxisCount*LodTexUtils.texInOneAxisCount);
    }

    public Map<Vector2i, Vector2i> getPos2AtlasPosMap() {
        return pos2AtlasPosMap;
    }

    public @Nullable Vector2i getIndexToPut() {
        for (int x = 0; x < atlasInUse.length; x++) {
            for (int y = 0; y < atlasInUse[x].length; y++) {
                if (!atlasInUse[x][y]) {
                    return new Vector2i(x,y);
                }
            }
        }

        return null;
    }

    public void addTexture(Vector2i worldPos, Vector2i textureCoords) {
        if (textureCoords != null) {
            pos2AtlasPosMap.put(worldPos, textureCoords);
            this.atlasInUse[textureCoords.x][textureCoords.y] = true;
        }
    }

    public void removeTexture(Vector2i worldPos) {
        Vector2i atlasCoords = pos2AtlasPosMap.get(worldPos);
        if (atlasCoords != null) {
            atlasInUse[atlasCoords.x][atlasCoords.y] = false;
            pos2AtlasPosMap.remove(worldPos);
        } else {
            VoxelSpaceProgram.logError("can't remove texture that was already removed");
        }
    }

    public void removeAll() {
        for (int x = 0; x < atlasInUse.length; x++) {
            Arrays.fill(atlasInUse[x], false);
        }
        pos2AtlasPosMap.clear();
    }
}
