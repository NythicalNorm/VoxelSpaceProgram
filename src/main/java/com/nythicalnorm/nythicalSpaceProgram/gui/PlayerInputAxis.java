package com.nythicalnorm.nythicalSpaceProgram.gui;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerInputAxis {
    private float AxisValue;
    private float AxisRateOfChange;
    private final float AxisMinValue;
    private final float AxisMaxValue;
    private final float AxisMultiplier;
    private final float RateOfChangeMax;
    private final KeyMapping DecreaseKey;
    private final KeyMapping IncreaseKey;


    public PlayerInputAxis(float axisMultiplier, float minAxis, float maxAxis, float rateOfChangeMax, float initalAxisValue,
                           KeyMapping decreaseKey, KeyMapping increaseKey) {
        this.AxisMultiplier = axisMultiplier;
        this.AxisValue = initalAxisValue;
        this.AxisMinValue = minAxis;
        this.AxisMaxValue = maxAxis;
        this.AxisRateOfChange = 0f;
        this.DecreaseKey = decreaseKey;
        this.IncreaseKey = increaseKey;
        this.RateOfChangeMax = rateOfChangeMax;
    }

    private void changeThrottle(float dir) {
        AxisRateOfChange += (dir * AxisMultiplier * Minecraft.getInstance().getFrameTime());
        AxisRateOfChange = Mth.clamp(AxisRateOfChange, -RateOfChangeMax, RateOfChangeMax);
        AxisValue = AxisValue + AxisRateOfChange;
        AxisValue = Mth.clamp(AxisValue, AxisMinValue, AxisMaxValue);
    }

    public void resetKeys(int pKeyCode, int pScanCode) {
        if (IncreaseKey.matches(pKeyCode, pScanCode) || DecreaseKey.matches(pKeyCode, pScanCode)) {
            AxisRateOfChange = 0f;
        }
    }

    public float getAxisValue() {
        return AxisValue;
    }

    public boolean keyPressCheck(int pKeyCode, int pScanCode) {
        if (IncreaseKey.matches(pKeyCode, pScanCode)) {
            changeThrottle(1f);
            return true;
        } else if (DecreaseKey.matches(pKeyCode, pScanCode)) {
            changeThrottle(-1f);
            return true;
        }
        return false;
    }
}
