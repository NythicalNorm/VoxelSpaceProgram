package com.nythicalnorm.nythicalSpaceProgram.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class CryogenicAirSeparatorScreen extends AbstractContainerScreen<CryogenicAirSeparatorMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID,
            "textures/gui/cryogenic_air_separator.png");

    public CryogenicAirSeparatorScreen(CryogenicAirSeparatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelX = 10000;
        this.titleLabelY = 10000;
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
        renderFluids(pGuiGraphics, x, y);
    }


    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        int bar_length =  menu.getEnergyProgress();
        guiGraphics.blit(TEXTURE, x + 155, y + 62 - bar_length, 176, 57 - bar_length, 12, bar_length);
    }
    private void renderFluids(GuiGraphics guiGraphics, int x, int y) {
        for (int i = 0; i < 3; i++) {
            FluidStack fluidStack = new FluidStack(this.menu.getFluidManufacture(i), 1000);
            int fluidAmount = this.menu.getFluidAmount(i);
            int tankOffsetX = 102 + i*18;

            if (fluidAmount > 0 && fluidStack.getFluid() != Fluids.EMPTY) {
                IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
                ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
                if (stillTexture != null) {
                    TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
                    if (sprite == null) {
                        continue;
                    }

                    int tintColor = fluidTypeExtensions.getTintColor(fluidStack);
                    float alpha = ((tintColor >> 24) & 0xFF) / 255f;
                    float red = ((tintColor >> 16) & 0xFF) / 255f;
                    float green = ((tintColor >> 8) & 0xFF) / 255f;
                    float blue = (tintColor & 0xFF) / 255f;

                    guiGraphics.setColor(red, green, blue, alpha);
                    int fluidHeight = this.menu.getFluidProgress(i);

                    guiGraphics.blit(x + tankOffsetX, y + 62 - fluidHeight, 0, 10, fluidHeight, sprite);
                    guiGraphics.setColor(1f, 1f, 1f, 1f);
                }
            }
            guiGraphics.blit(TEXTURE, x + tankOffsetX - 1, y + 5, 176, 58, 12,58);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        int energyStored = this.menu.getCurrentEnergy();
        int maxStored = this.menu.getMaxEnergy();

        if(isHovering(155,5,12, 58, pMouseX, pMouseY)) {
            Component text = Component.literal(energyStored + "/" + maxStored + " FE");
            pGuiGraphics.renderTooltip(this.font, text, pMouseX, pMouseY);
        }
        renderTankTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private void renderTankTooltip(GuiGraphics guiGraphics, int x, int y) {
        for (int i = 0; i < 3; i++) {
            int fluidStored = this.menu.getFluidAmount(i);
            int fluidCapacity = this.menu.getFluidCapacity(i);
            int tankOffset = 101 + i*18;
            FluidStack fluidStack = new FluidStack(this.menu.getFluidManufacture(i), 1000);

            if (isHovering(tankOffset, 5, 12, 58, x, y)) {
                Component text = Component.translatable(fluidStack.getTranslationKey())
                        .append(" " + fluidStored + "/" + fluidCapacity + " mB");
                guiGraphics.renderTooltip(this.font, text, x, y);
                return;
            }
        }
    }
}
