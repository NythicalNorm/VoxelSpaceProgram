package com.nythicalnorm.voxelspaceprogram.gui.widgets;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Axis;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.gui.screen.PlayerSpacecraftScreen;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.planetshine.generators.QuadSphereModelGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class NavballWidget extends AbstractWidget {
    private static final ResourceLocation NAVBALL_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoxelSpaceProgram.MODID,
            "textures/gui/navballwidget.png");

    private static final ResourceLocation NAVBALL_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoxelSpaceProgram.MODID,
            "textures/gui/navball.png");

    public NavballWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int xPos = getX() - 47;
        int yPos = getY() - 86;

        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier -> renderNavBall(celestialStateSupplier, pGuiGraphics));

        pGuiGraphics.blit(NAVBALL_GUI_TEXTURE, xPos, yPos,0,0,94,86);
        PlayerSpacecraftScreen spacecraftScreen = CelestialStateSupplier.getInstance().get().getScreenManager().getSpacecraftScreen();

        if (spacecraftScreen != null) {
            renderThrottleBar(pGuiGraphics, xPos, yPos, spacecraftScreen);
            renderButtons(pGuiGraphics, xPos, yPos, spacecraftScreen);
            renderGForceBar(pGuiGraphics, xPos, yPos);
            renderRelativeVelocity(pGuiGraphics, xPos, yPos);
        }
    }

    private void renderNavBall(CelestialStateSupplier css, GuiGraphics pGuiGraphics) {
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();

        PoseStack navballPosestack = new PoseStack();
        Window gameWindow =  Minecraft.getInstance().getWindow();

        // 70f is the pixel distance from the center of the sphere's place to the bottom
        float Yheight = 70f/gameWindow.getGuiScaledHeight();

        float aspectRatio = (float) pGuiGraphics.guiWidth() / pGuiGraphics.guiHeight();
        float Orthosize = 1f;
        Matrix4f projectionMatrix = new Matrix4f().setOrtho(-Orthosize*aspectRatio, Orthosize*aspectRatio, -Orthosize, Orthosize, 0.001f, 10.0f);

        navballPosestack.translate(0f,-1f+Yheight,-1f);

        float navballScale = (float) gameWindow.getGuiScale() * (124f/gameWindow.getHeight());
        navballPosestack.scale(navballScale, navballScale, navballScale);

        if (css.getPlayerOrbit() != null) {
            if (css.getPlayerOrbit().getRotation() != null) {
                navballPosestack.mulPose(Axis.YP.rotation(Mth.HALF_PI));
                navballPosestack.mulPose(css.getPlayerOrbit().getRotation());
            }
        }

        QuadSphereModelGenerator.getSphereBuffer().bind();
        RenderSystem.setShaderTexture(0, NAVBALL_TEXTURE);
        ShaderInstance shad = GameRenderer.getPositionTexShader();

        QuadSphereModelGenerator.getSphereBuffer().drawWithShader(navballPosestack.last().pose(), projectionMatrix, shad);
        VertexBuffer.unbind();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);
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
        if (CelestialStateSupplier.getInstance().get().weInSpaceDim()) {
            int speed = (int) CelestialStateSupplier.getInstance().get().getPlayerOrbit().getRelativeVelocity().length();
            Component orbitalSpeedComp = Component.translatable("voxelspaceprogram.screen.orbital_speed", speed);
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
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

    }
}
