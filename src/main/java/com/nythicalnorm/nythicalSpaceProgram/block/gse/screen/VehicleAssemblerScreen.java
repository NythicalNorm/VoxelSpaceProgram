package com.nythicalnorm.nythicalSpaceProgram.block.gse.screen;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VehicleAssemblerScreen extends AbstractContainerScreen<VehicleAssemblerMenu> {
    private static final ResourceLocation TEXTURE = NythicalSpaceProgram.rl(
            "textures/gui/vehicle_assembler.png");
    private static final int texWidth = 256;
    private static final int texHeight = 192;

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelX = 10000;
        this.titleLabelY = 10000;
    }

    public VehicleAssemblerScreen(VehicleAssemblerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - texWidth) / 2;
        int y = (height - texHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, texWidth, texHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
