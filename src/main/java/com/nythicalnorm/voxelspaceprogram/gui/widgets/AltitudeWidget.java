package com.nythicalnorm.voxelspaceprogram.gui.widgets;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class AltitudeWidget extends AbstractWidget {
    private static final ResourceLocation Altitude_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(VoxelSpaceProgram.MODID,
            "textures/gui/altitudewidget.png");

    ScrollingNumber[] scrollingNumbers;
    private static final int AmountOfNumberDisplays = 9;

    public AltitudeWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        scrollingNumbers = new ScrollingNumber[AmountOfNumberDisplays];

        for (int i = 0; i < scrollingNumbers.length; i++) {
            scrollingNumbers[i] = new ScrollingNumber(i*8);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int x = getX() - 46;
        int y = getY();

        pGuiGraphics.blit(Altitude_GUI_TEXTURE, x, y,0,0,92,28);

        VoxelSpaceProgram.getCelestialStateSupplier().ifPresent(css -> renderAltitudeNumbers(css, pGuiGraphics, x, y));

        pGuiGraphics.blit(Altitude_GUI_TEXTURE, x + 10, y + 15,96,0,5,13);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    private void renderAltitudeNumbers(CelestialStateSupplier css, GuiGraphics pGuiGraphics, int xPos, int yPos) {
        //altitude = 100000000 - altitude;
        double altitude = 0;
        Optional<PlanetaryBody> playerParent = css.getCurrentPlanetSOIin();

        if (css.getPlayerOrbit() != null && playerParent.isPresent()) {
            altitude = css.getPlayerOrbit().getRelativePosDistance() + 0.5d - playerParent.get().getRadius();
            altitude = Math.abs(altitude);
        }

        String altitudeMeters =  Long.toString((long) altitude);
        int altitudeUnitIndex;

        for (altitudeUnitIndex = 0; altitudeUnitIndex < 4; altitudeUnitIndex++){
            // 6 significant digits required for meter to km but 9 for other unit transitions
            if (altitudeMeters.length() <= 6 && altitudeUnitIndex == 0) {
                break;
            } else if (altitudeMeters.length() <= 9 && altitudeUnitIndex > 0) {
                break;
            }
            if (altitudeMeters.length() > 3) {
                altitudeMeters = altitudeMeters.substring(0, altitudeMeters.length() - 3);
            } else {
                break;
            }
        }

        //drawing the distance unit
        pGuiGraphics.blit(Altitude_GUI_TEXTURE, xPos + 74, yPos + 2,16, 32 + (altitudeUnitIndex * 12),16,12);

        float deltaTime = Minecraft.getInstance().getDeltaFrameTime();

        for (int i = 0; i < scrollingNumbers.length; i++) {
            int altitudeCharIndex = (altitudeMeters.length() - 1) - i;
            int scrollingNumbersIndex = (scrollingNumbers.length - 1) - i;

            if (altitudeCharIndex >= 0) {
                char num = altitudeMeters.charAt(altitudeCharIndex);
                scrollingNumbers[scrollingNumbersIndex].setNum(Character.getNumericValue(num));
            } else {
                scrollingNumbers[scrollingNumbersIndex].setNum(0);
            }

            scrollingNumbers[scrollingNumbersIndex].drawToScreen(pGuiGraphics, xPos, yPos, deltaTime);
        }
    }

    private class ScrollingNumber {
        private int currentSetNum;
        private final int xOffset;
        private float currentY;
        private float YGoal;
        private static final float scrollSpeed = 1f;
        private static final int numbersTexHeight = 120;

        public ScrollingNumber(int xOffset) {
            this.xOffset = 2 + xOffset;
            currentSetNum = 0;
            currentY = 0;
        }

        public void setNum(int num) {
            currentSetNum = num % 10;
            YGoal = currentSetNum * 12;
        }

        public void drawToScreen(GuiGraphics pGuiGraphics, int xPos, int yPos, float partialTick) {
            float tickAmount = partialTick * scrollSpeed;
            float actualYGoal = YGoal;

            if (currentY - YGoal > ((float) numbersTexHeight / 2)) {
                actualYGoal = actualYGoal + numbersTexHeight;
            } else if (YGoal - currentY > ((float) numbersTexHeight / 2)) {
                actualYGoal = actualYGoal - numbersTexHeight;
            }

            float distance = Math.abs(actualYGoal - currentY);

            if (currentY < actualYGoal && (currentY + tickAmount) < actualYGoal) {
                currentY += tickAmount*distance;
            } else if (currentY > actualYGoal && (currentY - tickAmount) > actualYGoal) {
                currentY -= tickAmount*distance;
            }

            if (currentY > numbersTexHeight) {
                currentY = currentY - numbersTexHeight;
            } else if (currentY < 0) {
                currentY = currentY + numbersTexHeight;
            }

            int yTex = 32 + Math.round(currentY) ;

            pGuiGraphics.blit(Altitude_GUI_TEXTURE, xPos + xOffset, yPos + 2,0, yTex,8,12);
        }
    }
}
