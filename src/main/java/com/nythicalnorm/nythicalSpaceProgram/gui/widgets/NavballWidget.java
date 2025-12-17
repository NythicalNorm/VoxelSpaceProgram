package com.nythicalnorm.nythicalSpaceProgram.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.gui.PlayerSpacecraftScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NavballWidget extends AbstractWidget {
    private static final ResourceLocation NAVBALL_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID,
            "textures/gui/navballwidget.png");

    public NavballWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, NAVBALL_GUI_TEXTURE);
        int xPos = getX() - 48;
        int yPos = getY() - 86;

        pGuiGraphics.blit(NAVBALL_GUI_TEXTURE, xPos, yPos,0,0,94,86);
        PlayerSpacecraftScreen spacecraftScreen = NythicalSpaceProgram.getCelestialStateSupplier().get().getScreenManager().getSpacecraftScreen();

        if (spacecraftScreen != null) {
            renderThrottleBar(pGuiGraphics, xPos, yPos, spacecraftScreen);
            renderButtons(pGuiGraphics, xPos, yPos, spacecraftScreen);
            renderGForceBar(pGuiGraphics, xPos, yPos);
            renderRelativeVelocity(pGuiGraphics, xPos, yPos);
        }
    }

    private void renderButtons(GuiGraphics pGuiGraphics, int xPos, int yPos, PlayerSpacecraftScreen spacecraftScreen) {
        if (spacecraftScreen.isRCS()) {
            pGuiGraphics.blit(NAVBALL_GUI_TEXTURE, xPos + 17, yPos + 18, 96, 0, 12, 6);
        }
        if (spacecraftScreen.isSAS()) {
            pGuiGraphics.blit(NAVBALL_GUI_TEXTURE, xPos + 65, yPos + 18,96,8, 12, 6);
        }
    }

    private void renderRelativeVelocity(GuiGraphics pGuiGraphics, int xPos, int yPos) {
        if (NythicalSpaceProgram.getCelestialStateSupplier().get().weInSpace()) {
            int speed = (int)NythicalSpaceProgram.getCelestialStateSupplier().get().getPlayerOrbit().getRelativeVelocity().length();
            Component orbitalSpeedComp = Component.translatable("nythicalspaceprogram.screen.orbital_speed", speed);
            pGuiGraphics.drawString(Minecraft.getInstance().font, orbitalSpeedComp,xPos + 22, yPos + 5, 0x00ff2b, false);
        }
    }


    private void renderThrottleBar(GuiGraphics graphics, int xPos, int yPos, PlayerSpacecraftScreen spacecraftScreen) {
            int barHeight = Math.round(Mth.lerp(spacecraftScreen.getThrottleSetting(), 0, 70));
            int pointerHeight = Mth.clamp(barHeight,1, 67);
            //drawing the blue bar
            graphics.blit(NAVBALL_GUI_TEXTURE, xPos + 3, yPos + 72 - barHeight,95,105 - barHeight,9, barHeight);
            //drawing the arrow
            graphics.blit(NAVBALL_GUI_TEXTURE, xPos - 3, yPos + 66 - pointerHeight,95,23,14,12);
    }

    private void renderGForceBar(GuiGraphics pGuiGraphics, int xPos, int yPos) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
