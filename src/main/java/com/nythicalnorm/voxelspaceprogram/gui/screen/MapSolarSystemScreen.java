package com.nythicalnorm.voxelspaceprogram.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.AltitudeWidget;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.LeftPanelWidget;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.NavballWidget;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.TimeWarpWidget;
import com.nythicalnorm.voxelspaceprogram.planetshine.map.MapRenderer;
import com.nythicalnorm.voxelspaceprogram.util.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;
import org.lwjgl.glfw.GLFW;

import java.lang.Math;

@OnlyIn(Dist.CLIENT)
public class MapSolarSystemScreen extends MouseLookScreen {
    private CelestialStateSupplier css;
    private Orbit[] FocusableBodies;
    private int currentFocusedBodyIndex;
    private final boolean isSpacecraftScreenOpen;

    public MapSolarSystemScreen(boolean PisSpacecraftScreenOpen) {
        super(Component.empty());
        VoxelSpaceProgram.getCelestialStateSupplier().ifPresent (celestialStateSupplier -> {
            css = celestialStateSupplier;
            celestialStateSupplier.getScreenManager().setMapScreenOpen(true);
        });
        this.isSpacecraftScreenOpen = PisSpacecraftScreenOpen;
    }

    @Override
    protected void init() {
        populateFocusedBodiesList();
        MapRenderer.setScreen(this);
        this.addRenderableWidget(new TimeWarpWidget(0,0, width, height, Component.empty()));
        if (isSpacecraftScreenOpen) {
            this.addRenderableWidget(new NavballWidget(width/2, height, width, height, Component.empty()));
            this.addRenderableWidget(new LeftPanelWidget(0, height, width, height, Component.empty()));
            this.addRenderableWidget(new AltitudeWidget(width/2, 0, width, height, Component.empty()));
        }
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.depthMask(false);
        PoseStack mapPosestack = new PoseStack();
        Matrix4f projectionMatrix = new Matrix4f().setPerspective(70, (float) graphics.guiWidth()/graphics.guiHeight(), 0.0000001f, 100.0f);

        Quaternionf dragCameraRot = new Quaternionf().rotateYXZ(cameraYrot, cameraXrot, 0f); //.mul(yRotQuaternion);
        Vector3d relativeCameraPos = new Vector3d(0d, 0d, zoomLevel * radiusZoomLevel);
        relativeCameraPos.rotate(new Quaterniond(dragCameraRot.x, dragCameraRot.y,dragCameraRot.z,dragCameraRot.w));
        //Vector3d absoluteCameraPos = currentFocusedBody.getAbsolutePos().add(relativeCameraPos);

        mapPosestack.pushPose();
        mapPosestack.mulPose(dragCameraRot.conjugate());

        MapRenderer.renderSkybox(mapPosestack, projectionMatrix);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        MapRenderer.renderMapObjects(graphics, mapPosestack, projectionMatrix, relativeCameraPos, FocusableBodies[currentFocusedBodyIndex], css);
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        mapPosestack.popPose();

        if (!mapPosestack.clear()) {
            throw new IllegalStateException("popped poses are not closed properly.");
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {

        if (KeyBindings.OPEN_SOLAR_SYSTEM_MAP_KEY.matches(pKeyCode, pScanCode)) {
            this.onClose();
            return true;
        }  else if (isSpacecraftScreenOpen) {
            if (css.getScreenManager().getSpacecraftScreen().keyPressed(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }

        if (GLFW.GLFW_KEY_TAB == pKeyCode){
            changeFocusBody(1);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (isSpacecraftScreenOpen) {
            if (css.getScreenManager().getSpacecraftScreen().keyReleased(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (isSpacecraftScreenOpen) {
            Minecraft.getInstance().setScreen(css.getScreenManager().getSpacecraftScreen());
        }
        css.getScreenManager().closeMapScreen();
    }

    public void changeFocusBody(int additionalIndex) {
        currentFocusedBodyIndex += additionalIndex;
        if (currentFocusedBodyIndex >= FocusableBodies.length) {
            currentFocusedBodyIndex = 0;
        }
        if (FocusableBodies[currentFocusedBodyIndex] instanceof PlanetaryBody) {
            if (css.getCurrentPlanet().isPresent()) {
                if (css.getCurrentPlanet().get().equals(FocusableBodies[currentFocusedBodyIndex])) {
                    Vector3d playerRelativePos = css.getPlayerOrbit().getRelativePos();
                    playerRelativePos.normalize();
                    cameraYrot = (float) Math.atan2(playerRelativePos.x,playerRelativePos.z);
                    cameraXrot = (float) Math.asin(playerRelativePos.y);
                }
            }
            radiusZoomLevel = ((PlanetaryBody) FocusableBodies[currentFocusedBodyIndex]).getRadius();
        } else if (FocusableBodies[currentFocusedBodyIndex] instanceof EntitySpacecraftBody) {
            radiusZoomLevel = 1000000;
        }

        MapRenderer.updateMapRenderables(css, FocusableBodies[currentFocusedBodyIndex]);
    }

    private void populateFocusedBodiesList() {
        Orbit currentFocusedBody = null;

        if (css.isOnPlanet()) {
            currentFocusedBody = css.getCurrentPlanet().get();
        } else if (css.weInSpaceDim() && css.getPlayerOrbit() != null) {
            currentFocusedBody = css.getPlayerOrbit();
        }

        int totalFocusAmount = css.getPlanetsProvider().getAllPlanetaryBodies().size();
        if (currentFocusedBody instanceof EntitySpacecraftBody) {
            totalFocusAmount += 1;
        }

        //setting the first element to the desired body and later filling in planets that aren't currentfocusedbody
        int index = 0;

        FocusableBodies = new Orbit[totalFocusAmount];
        FocusableBodies[index] = currentFocusedBody;
        currentFocusedBodyIndex = index;

        for (PlanetaryBody plnt : css.getPlanetsProvider().getAllPlanetaryBodies().values()) {
            if (plnt != currentFocusedBody) {
                index++;
                FocusableBodies[index] = plnt;
            }
        }
        changeFocusBody(0);
    }
}
