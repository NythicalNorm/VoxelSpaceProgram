package com.nythicalnorm.voxelspaceprogram.gui.widgets;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;

@OnlyIn(Dist.CLIENT)
public class TimeWarpWidget extends AbstractWidget {
    private static final ResourceLocation TIME_WARP_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoxelSpaceProgram.MODID,
            "textures/gui/timewarpwidget.png");

    public TimeWarpWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(TIME_WARP_TEXTURE, getX(), getY(),0,0,136,34);

        CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier -> {
            Component timeComp = parseTime(celestialStateSupplier.getCurrentTimeInSec(), celestialStateSupplier.getPlanetsProvider().getOverworldPlanet());
            pGuiGraphics.drawString(Minecraft.getInstance().font, timeComp,13, 6, 0x00ff2b, false);

            int timeWarpSettingAmount = celestialStateSupplier.getTimeWarpSetting() + 1;
            int ArrowXVal = 15;

            for (int i = 0; i < timeWarpSettingAmount; i++) {
                pGuiGraphics.blit(TIME_WARP_TEXTURE, getX() + ArrowXVal, getY() + 20,15,34,6,9);
                ArrowXVal += 10;
            }
        });
    }

    private Component parseTime(Double currentTime, PlanetaryBody overworldPlanet) {
        if (overworldPlanet == null) {
            return Component.empty();
        }

       double yearTime = (2*Math.PI)/overworldPlanet.getOrbitalElements().MeanAngularMotion;
       // this is not sidereal rotation period the values need to be changed and this calculation also needs to be changed.
       double dayTime = Calcs.timeLongToDouble(overworldPlanet.getRotationPeriod());
       double hourTime = dayTime/24;
       double minuteTime = hourTime/60;

       int year = (int) Math.floor(currentTime/yearTime) + 1;
       int remainderYear = (int) (currentTime%yearTime);
       int day = (int) Math.floor(remainderYear/dayTime);
       int remainderHour = (int) (remainderYear % dayTime);
       int hour = (int) Math.floor(remainderHour/hourTime);
       int remainderMinute = (int) (remainderHour % hourTime);
       int minute = (int) Math.floor(remainderMinute/minuteTime);

        DecimalFormat twodigits = new DecimalFormat("00");

        return Component.translatable("voxelspaceprogram.screen.time",
                year, day, twodigits.format(hour), twodigits.format(minute));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
