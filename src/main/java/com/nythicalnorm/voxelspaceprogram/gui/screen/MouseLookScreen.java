package com.nythicalnorm.voxelspaceprogram.gui.screen;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.util.KeyBindings;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseLookScreen extends Screen implements GuiEventListener {
    protected float cameraYrot = 0f;
    protected float cameraXrot = 0f;
    protected float zoomLevel = 2f;
    protected double radiusZoomLevel = 0f;

    protected MouseLookScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == 1) {
            float sensitivity = 1.40041507642f;

            cameraYrot = cameraYrot + (float) -Math.sin(sensitivity*(pDragX/width));
            cameraXrot = cameraXrot + (float) -Math.sin(sensitivity*(pDragY/height));
            cameraXrot = Mth.clamp(cameraXrot, -Mth.HALF_PI, Mth.HALF_PI);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        zoomLevel =  zoomLevel * (float) Math.pow(1.1, -pDelta);
        float maxDistanceZoom = 1424600000000f/((float) radiusZoomLevel);
        zoomLevel = Mth.clamp(zoomLevel, 1.000001f, maxDistanceZoom);
        return true;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (KeyBindings.INC_TIME_WARP_KEY.matches(pKeyCode, pScanCode)) {
            CelestialStateSupplier.getInstance().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(true)));
            return true;
        }

        else if (KeyBindings.DEC_TIME_WARP_KEY.matches(pKeyCode, pScanCode)) {
            CelestialStateSupplier.getInstance().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(false)));
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
