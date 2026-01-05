package com.nythicalnorm.voxelspaceprogram.block.gse.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.gse.AssemblerState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VehicleAssemblerScreen extends AbstractContainerScreen<VehicleAssemblerMenu> {
    private static final ResourceLocation INFO_PANEL = VoxelSpaceProgram.rl(
            "textures/gui/vehicleassembler/info_panel_display.png");

    private static final ResourceLocation STAGE_DISPLAY = VoxelSpaceProgram.rl(
            "textures/gui/vehicleassembler/stage_display.png");

    private static final ResourceLocation ERROR_DISPLAY = VoxelSpaceProgram.rl(
            "textures/gui/vehicleassembler/error_display.png");


    private static final int texWidth = 384;
    private static final int texHeight = 248;
    private int xPos = 0;
    private int yPos = 0;

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
        if (menu.getVehicleAssemblerBE() == null) {
            this.onClose();
        }
        xPos = (width - texWidth) / 2;
        yPos = (height - texHeight) / 2;

        AssemblerState state = menu.getVehicleAssemblerBE().getState();

        ResourceLocation currentBG;
        switch (state) {
            case JUST_PLACED:
                addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, (button) -> {
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
                }).bounds(xPos, yPos + 60, 150, 20).build());
                break;
            case ASSEMBLY_AREA_READY:
                break;
            default:
        }

        renderCommonBG(pGuiGraphics, xPos, yPos, state == AssemblerState.JUST_PLACED);
    }

    private void renderCommonBG(GuiGraphics graphics, int x, int y, boolean isJustPlaced) {
        if (isJustPlaced) {
            RenderSystem.setShaderColor(0.25f,0.25f,0.25f,1.0f);
        }

        graphics.blit(STAGE_DISPLAY, x, y, 0, 0,256,152);
        graphics.blit(INFO_PANEL, x + 256, y, 0, 0,128,152);

        RenderSystem.setShaderColor(1f,1f,1f,1f);
        graphics.blit(ERROR_DISPLAY, x, y + 152, 0, 0,256,96);
        graphics.blit(ERROR_DISPLAY, x + 256, y + 152, 0, 104,128,96);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (menu.getProblems() != null) {
            renderProblems(pGuiGraphics, menu.getProblems());
        }

        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private void renderProblems(GuiGraphics pGuiGraphics, Component[] components) {
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            pGuiGraphics.drawString(Minecraft.getInstance().font, comp, xPos + 14, yPos + 162 + (i*50), comp.getStyle().getColor().getValue());
        }
    }
}
