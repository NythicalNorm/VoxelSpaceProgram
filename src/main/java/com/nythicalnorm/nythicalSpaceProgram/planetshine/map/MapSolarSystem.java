package com.nythicalnorm.nythicalSpaceProgram.planetshine.map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.util.KeyBindings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class MapSolarSystem extends Screen implements GuiEventListener {
    public MapSolarSystem(Component pTitle) {
        super(pTitle);
        NythicalSpaceProgram.getCelestialStateSupplier().ifPresent (celestialStateSupplier -> {
            celestialStateSupplier.setMapScreenOpen(true);
        });
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PoseStack mapPosestack = new PoseStack();
        Matrix4f projectionMatrix = new Matrix4f().setPerspective(90, (float) graphics.guiWidth()/graphics.guiHeight(), 0.0f, 100.0f);;
        RenderSystem.disableDepthTest();

        MapRenderer.renderSkybox(mapPosestack, projectionMatrix);
        MapRenderer.renderPlanets(mapPosestack, projectionMatrix);

        RenderSystem.enableDepthTest();
        //super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        NythicalSpaceProgram.getCelestialStateSupplier().ifPresent (celestialStateSupplier -> {
            celestialStateSupplier.setMapScreenOpen(false);
        });
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (KeyBindings.INC_TIME_WARP_KEY.matches(pKeyCode, pScanCode)) {
            NythicalSpaceProgram.getCelestialStateSupplier().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(true)));
        }

        if (KeyBindings.INC_TIME_WARP_KEY.matches(pKeyCode, pScanCode)) {
                NythicalSpaceProgram.getCelestialStateSupplier().ifPresent((celestialStateSupplier ->
                        celestialStateSupplier.TryChangeTimeWarp(false)));
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void afterKeyboardAction() {
        super.afterKeyboardAction();
    }
}
