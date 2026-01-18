package com.nythicalnorm.voxelspaceprogram.planetshine.map;

import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.gui.screen.MapSolarSystemScreen;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.planetshine.PlanetShine;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderTypes.*;
import com.nythicalnorm.voxelspaceprogram.planetshine.renderers.AtmosphereRenderer;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class MapRenderer {
    public static final float SCALE_FACTOR = 1/1000000000f;
    private static MapRenderable renderTree;
    private static Orbit currentFocusedBody;
    private static MapSolarSystemScreen currentOpenScreen;
    private static ArrayList<MapRenderableIcon> iconsList;
    private static MapRenderableIcon homePlanetPlayerDisplay;

    public static void setupBuffers() {
        OrbitDrawer.generateCircle(2048);
        OrbitDrawer.generateHyperbola(2048);
    }

    public static void renderSkybox(PoseStack mapPosestack, Matrix4f projectionMatrix) {
        AtmosphereRenderer.renderSpaceSky(mapPosestack, projectionMatrix);
        PlanetShine.drawStarBuffer(mapPosestack, projectionMatrix, 1.0f);
    }

    public static void renderMapObjects(GuiGraphics graphics, PoseStack poseStack, Matrix4f projectionMatrix, Vector3d cameraPos, Orbit currentFocus, CelestialStateSupplier css) {
        currentFocusedBody = currentFocus;
        if (renderTree == null || currentFocusedBody == null) {
            return;
        }

        Vector3f mapCameraPos = toMapCoordinate(cameraPos);
        poseStack.translate(-mapCameraPos.x, -mapCameraPos.y, -mapCameraPos.z);
        renderTree.propagateRender(poseStack, projectionMatrix, null);

        for (MapRenderableIcon icon : iconsList) {
            renderIcon(graphics, icon.getScreenPos(), icon.getPlayerTextureLoc(), 64);
        }
    }

    public static void updateMapRenderables(CelestialStateSupplier css, Orbit currentFocusedBody) {
        PlanetaryBody rootStar = css.getPlanetsProvider().getRootStar();
        iconsList = new ArrayList<>();
        MapRelativeState starMapState = MapRelativeState.AbsolutePos;
        if (rootStar.hasChild(currentFocusedBody)) {
            starMapState = MapRelativeState.FocusedBodyParent;
        } else if (rootStar.equals(currentFocusedBody)) {
            starMapState = MapRelativeState.FocusedBody;
        }

        MapRenderable starRenderInMap = new MapRenderablePlanet(rootStar, starMapState, null);

        Optional<PlanetaryBody> planetOn = css.getCurrentPlanet();
        if (planetOn.isPresent()) {
            homePlanetPlayerDisplay = new MapRenderableIcon(css.getPlayerOrbit(), Minecraft.getInstance().player.getSkinTextureLocation(),
                    MapRelativeState.AlwaysParentRelative, planetOn.get());
            iconsList.add(homePlanetPlayerDisplay);
        }

        renderTree = traverseAndPopulateList(css.getPlanetsProvider().getRootStar(), currentFocusedBody, starRenderInMap);
    }

    private static MapRenderable traverseAndPopulateList(Orbit parentBody, Orbit currentFocusedBody, MapRenderable parentRenderableInMap) {
        Collection<Orbit> OrbitChildren = parentBody.getChildren();

        if (homePlanetPlayerDisplay != null) {
            if (parentBody.equals(VoxelSpaceProgram.getCelestialStateSupplier().get().getCurrentPlanet().get())) {
                parentRenderableInMap.addChildRenderable(homePlanetPlayerDisplay);
            }
        }

        if (OrbitChildren != null) {
            for (Orbit childBody : OrbitChildren) {
                boolean isCurrentFocusedBody = childBody.equals(currentFocusedBody);
                MapRelativeState mapState = MapRelativeState.AbsolutePos;
                if (isCurrentFocusedBody) {
                    mapState = MapRelativeState.FocusedBody;
                } else if (currentFocusedBody.hasChild(childBody)) {
                    mapState = MapRelativeState.RelativePos;
                } else if (childBody.hasChild(currentFocusedBody)) {
                    mapState = MapRelativeState.FocusedBodyParent;
                }
                MapRenderable renderInMap = null;

                if (childBody.getOrbitalElements() != null) {
                    parentRenderableInMap.addChildRenderable(new MapRenderableOrbit(MapRelativeState.AlwaysParentRelative, childBody, parentBody));
                }

                if (childBody instanceof PlanetaryBody planetaryBody) {
                    renderInMap = new MapRenderablePlanet(planetaryBody, mapState, parentBody);
                } else if (childBody instanceof EntitySpacecraftBody clientBody) {
                    ResourceLocation playerHeadTexture = Minecraft.getInstance().player.getSkinTextureLocation();
                    MapRenderableIcon iconMap = new MapRenderableIcon(clientBody, playerHeadTexture, mapState, parentBody);
                    iconsList.add(iconMap);
                    renderInMap = iconMap;
                }

                if (renderInMap != null) {
                    parentRenderableInMap.addChildRenderable(traverseAndPopulateList(childBody, currentFocusedBody, renderInMap));
                }
            }
        }

        return parentRenderableInMap;
    }

    private static void renderIcon(GuiGraphics graphics, int[] screenPos, ResourceLocation TextureLoc, float size) {
        float relativeHeadSize = size/8;
        graphics.blit(TextureLoc, (int) (screenPos[0] - relativeHeadSize*0.5f), (int) (screenPos[1] - relativeHeadSize*0.5f), (int) relativeHeadSize,
                (int) relativeHeadSize,(int) relativeHeadSize, (int) relativeHeadSize,  (int) size, (int) size);
    }

    public static Orbit getCurrentFocusedBody() {
        return currentFocusedBody;
    }

    public static Vector3f toMapCoordinate(Vector3d position) {
        position.mul(SCALE_FACTOR);
        return new Vector3f((float) position.x, (float) position.y, (float) position.z);
    }

    public static MapSolarSystemScreen getCurrentOpenScreen() {
        return currentOpenScreen;
    }

    public static void setScreen(MapSolarSystemScreen mapSolarSystem) {
        if (mapSolarSystem == null) {
            homePlanetPlayerDisplay = null;
            iconsList = new ArrayList<>();
        }
        currentOpenScreen = mapSolarSystem;
    }
}
