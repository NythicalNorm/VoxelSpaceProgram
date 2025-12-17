package com.nythicalnorm.nythicalSpaceProgram.gui;

import net.minecraft.client.KeyMapping;

public class PlayerInputDirection {
    private final KeyMapping DecreaseKey;
    private final KeyMapping IncreaseKey;
    private boolean incKeyDown;
    private boolean decKeyDown;

    public PlayerInputDirection(KeyMapping decreaseKey, KeyMapping increaseKey) {
        this.DecreaseKey = decreaseKey;
        this.IncreaseKey = increaseKey;
        this.incKeyDown = false;
        this.decKeyDown = false;
    }

    public float getAxisValue() {
        if (incKeyDown == decKeyDown) {
            return 0.0F;
        } else {
            return incKeyDown ? 1.0F : -1.0F;
        }
    }

    public boolean keyPressCheck(int pKeyCode, int pScanCode) {
        if (IncreaseKey.matches(pKeyCode, pScanCode)) {
            incKeyDown = true;
            return true;
        } else if (DecreaseKey.matches(pKeyCode, pScanCode)) {
            decKeyDown = true;
            return true;
        }
        return false;
    }

    public void resetKeys(int pKeyCode, int pScanCode) {
        if (IncreaseKey.matches(pKeyCode, pScanCode)) {
            incKeyDown = false;
        } else if (DecreaseKey.matches(pKeyCode, pScanCode)) {
            decKeyDown = false;
        }
    }
}
