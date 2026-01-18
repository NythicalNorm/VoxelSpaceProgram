package com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.planetshine.map.MapRenderer;
import com.nythicalnorm.voxelspaceprogram.util.RenderingCommon;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MapRenderableIcon extends MapRenderable {
    private final ResourceLocation spacecraftTextureLoc;
    private final EntitySpacecraftBody spacecraftBody;
    private int[] screenPos;

    public MapRenderableIcon(EntitySpacecraftBody playerBody, ResourceLocation playerHeadTex, MapRelativeState mapRelativeState, Orbit parentBody) {
        super(mapRelativeState, parentBody);
        this.spacecraftTextureLoc = playerHeadTex;
        this.spacecraftBody = playerBody;
        screenPos = new int[2];
    }

    //This doesn't actually render it just calculates the position so it can be rendered with guiGraphics in the future
    @Override
    public Vector3f render(PoseStack poseStack, Matrix4f projectionMatrix) {
        if (MapRenderer.getCurrentOpenScreen() == null) {
            return null;
        }
        Vector3f pos = getPos(spacecraftBody, MapRenderer.getCurrentFocusedBody());

        screenPos = RenderingCommon.worldToScreenCoordinate(pos,
                poseStack, projectionMatrix, MapRenderer.getCurrentOpenScreen().width, MapRenderer.getCurrentOpenScreen().height);

        return null;
    }

    public int[] getScreenPos() {
        return screenPos;
    }

    public EntitySpacecraftBody getPlayerBody() {
        return spacecraftBody;
    }

    public ResourceLocation getPlayerTextureLoc() {
        return spacecraftTextureLoc;
    }
}
