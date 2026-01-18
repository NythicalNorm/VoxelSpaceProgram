package com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.planetshine.map.MapRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class MapRenderable {
    protected final MapRelativeState relativeState;
    Orbit parentBody;
    protected List<MapRenderable> childRenderables;

    public MapRenderable(MapRelativeState mapRelativeState, Orbit parentBody) {
        this.relativeState = mapRelativeState;
        this.parentBody = parentBody;
        this.childRenderables = new ArrayList<>();
    }

    public void addChildRenderable(MapRenderable renderableInMap) {
        this.childRenderables.add(renderableInMap);
    }

    public void propagateRender(PoseStack poseStack, Matrix4f projectionMatrix, Vector3f parentPos) {
        poseStack.pushPose();
        if (relativeState.equals(MapRelativeState.AlwaysParentRelative)) {
            poseStack.translate(parentPos.x, parentPos.y, parentPos.z);
        }
        Vector3f parentBodyPos = render(poseStack, projectionMatrix);
        poseStack.popPose();

        for (MapRenderable childRenderable : childRenderables) {
            childRenderable.propagateRender(poseStack, projectionMatrix, parentBodyPos);
        }
    }

    protected Vector3f getPos(Orbit bodyToPlace, Orbit currentFocusedBody) {
        Vector3d returnPos = switch (relativeState) {
            case AbsolutePos -> bodyToPlace.getAbsolutePos().sub(currentFocusedBody.getAbsolutePos());
            case RelativePos, AlwaysParentRelative -> bodyToPlace.getRelativePos();
            case FocusedBodyParent -> currentFocusedBody.getRelativePos().negate();
            default -> new Vector3d(0f, 0f, 0f);
        };

        return MapRenderer.toMapCoordinate(returnPos);
    }

    public abstract Vector3f render(PoseStack poseStack, Matrix4f projectionMatrix);
}

