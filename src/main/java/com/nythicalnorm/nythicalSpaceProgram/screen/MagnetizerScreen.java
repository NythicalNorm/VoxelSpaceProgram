package com.nythicalnorm.nythicalSpaceProgram.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MagnetizerScreen extends AbstractContainerScreen<MagnetizerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID,
            "textures/gui/magnetizer.png");

    public MagnetizerScreen(MagnetizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        //this.inventoryLabelX = 10000;
        //this.titleLabelY = 10000;

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) /2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 52, y + 35, 176, 0, menu.getScaledProgress(), 14);
        }
        int bar_length =  menu.getEnergyProgress();
        guiGraphics.blit(TEXTURE, x + 150, y + 65 - bar_length, 176, 71 - bar_length, 12, bar_length);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        int energyStored = this.menu.getCurrentEnergy();
        int maxStored = this.menu.getMaxEnergy();

        if(isHovering(150,8,12, 58, pMouseX, pMouseY)) {
            Component text = Component.literal(energyStored + "/" + maxStored + " FE");
            pGuiGraphics.renderTooltip(this.font, text, pMouseX, pMouseY);
        }
    }
}
