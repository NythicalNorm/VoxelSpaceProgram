package com.nythicalnorm.nythicalSpaceProgram.gui;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.map.MapRenderer;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.map.MapSolarSystem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModScreenManager {
    private boolean isMapScreenOpen = false;
    private PlayerSpacecraftScreen openSpacecraftScreen = null;

    public void setMapScreenOpen(boolean open) {
        this.isMapScreenOpen = open;
    }

    public void setOpenSpacecraftScreen(PlayerSpacecraftScreen opened) {
        this.openSpacecraftScreen = opened;
    }

    public boolean doPlanetShineDraw() {
        updateScreenState();
        return Minecraft.getInstance().screen instanceof MapSolarSystem;
    }

    public void updateScreenState() {
        if (Minecraft.getInstance().screen instanceof DeathScreen) {
            if (isMapScreenOpen) {
                closeMapScreen();
            }
            if (openSpacecraftScreen != null) {
                closeSpacecraftScreen();
            }
        }
    }

    public void closeMapScreen() {
        MapRenderer.setScreen(null);
        isMapScreenOpen = false;
    }

    public boolean isSpacecraftScreenOpen() {
        return openSpacecraftScreen != null;
    }

    public PlayerSpacecraftScreen getSpacecraftScreen() {
        return openSpacecraftScreen;
    }

    public void closeSpacecraftScreen() {
        Options minecraftOptions = Minecraft.getInstance().options;
        minecraftOptions.hideGui = false;
        minecraftOptions.setCameraType(CameraType.FIRST_PERSON);
        openSpacecraftScreen = null;
        NythicalSpaceProgram.getCelestialStateSupplier().get().setControllingBody(null);
    }
}
