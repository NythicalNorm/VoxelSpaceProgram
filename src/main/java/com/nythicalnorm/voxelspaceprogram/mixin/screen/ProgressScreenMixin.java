package com.nythicalnorm.voxelspaceprogram.mixin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ProgressScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public class ProgressScreenMixin {
    @Inject( method = "render", at = @At("HEAD"))
    public void renderScreen(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
//        if (CelestialStateSupplier.get() != null && CelestialStateSupplier.get().doRender()) {
//            ci.cancel();
//        }
    }
}
