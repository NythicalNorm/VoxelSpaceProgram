package com.nythicalnorm.voxelspaceprogram.gui.screen;

import com.nythicalnorm.planetshine.PSClient;
import com.nythicalnorm.planetshine.gui.input.PlayerInputAxis;
import com.nythicalnorm.planetshine.gui.input.PlayerInputDirection;
import com.nythicalnorm.planetshine.gui.screen.ISpacecraftControlStateDisplay;
import com.nythicalnorm.planetshine.gui.screen.MapSolarSystemScreen;
import com.nythicalnorm.planetshine.gui.screen.PSSpacecraftScreen;
import com.nythicalnorm.planetshine.gui.widgets.LeftPanelWidget;
import com.nythicalnorm.planetshine.spacecraft.player.ClientPlayerOrbitBody;
import com.nythicalnorm.planetshine.util.PSKeyBinds;
import com.nythicalnorm.voxelspaceprogram.util.VSPKeyBinds;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.lang.Math;

@OnlyIn(Dist.CLIENT)
public class PlayerSpacecraftScreen extends PSSpacecraftScreen implements ISpacecraftControlStateDisplay {
    private ItemStack jetpackItem;
    private final LocalPlayer player;
    private final PSClient psClient;
    private final Options minecraftOptions;
    private float initialYLookDir;
    private boolean SAS = false;
    private boolean RCS = false;
    private boolean dockingMode = true;

    //these axis are based on the default keymappings
    private PlayerInputDirection SWAxis;
    private PlayerInputDirection ADAxis;
    private PlayerInputDirection QEAxis;
    private PlayerInputDirection CtrlShiftAxis;
    private PlayerInputAxis throttleAxis;

    public PlayerSpacecraftScreen(ItemStack spacesuitItem, LocalPlayer player, PSClient psClient) {
        super(Component.empty(), psClient.getControllingBody(), psClient.getScreenManager(), FacingDirection.North);
        this.jetpackItem = spacesuitItem;
        this.player = player;
        this.psClient = psClient;
        this.minecraftOptions = Minecraft.getInstance().options;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new LeftPanelWidget(0, height, width, height, Component.empty()));

        minecraftOptions.setCameraType(CameraType.THIRD_PERSON_BACK);
        minecraftOptions.hideGui = true;
        player.setXRot(0f);
        zoomLevel = 4f;

        initialYLookDir = player.getViewYRot(0f);
        cameraYrot = (float) -Math.toRadians(initialYLookDir);
        player.setYBodyRot(initialYLookDir);

        throttleAxis = new PlayerInputAxis(0.05f, 0f, 1f, 0.08f,0f,
                VSPKeyBinds.DECREASE_THROTTLE_KEY, VSPKeyBinds.INCREASE_THROTTLE_KEY);

        SWAxis = new PlayerInputDirection(minecraftOptions.keyDown, minecraftOptions.keyUp);
        ADAxis = new PlayerInputDirection(minecraftOptions.keyLeft, minecraftOptions.keyRight);
        QEAxis = new PlayerInputDirection(VSPKeyBinds.ANTI_CLOCKWISE_SPIN_KEY, VSPKeyBinds.CLOCKWISE_SPIN_KEY);
        CtrlShiftAxis = new PlayerInputDirection(VSPKeyBinds.DECREASE_THROTTLE_KEY, VSPKeyBinds.INCREASE_THROTTLE_KEY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean keyPressed = false;

        if (PSKeyBinds.OPEN_SPACECRAFT_HUD_KEY.matches(pKeyCode, pScanCode)) {
            this.onClose();
            keyPressed = true;
        } else if (PSKeyBinds.OPEN_SOLAR_SYSTEM_MAP_KEY.matches(pKeyCode, pScanCode)) {
            Minecraft.getInstance().setScreen(new MapSolarSystemScreen(true, psClient.getScreenManager().getMapState()));
            keyPressed = true;
        }  else if (VSPKeyBinds.RCS_TOGGLE_KEY.matches(pKeyCode, pScanCode)) {
            RCS = !RCS;
        } else if (VSPKeyBinds.SAS_TOGGLE_KEY.matches(pKeyCode, pScanCode)) {
            SAS = !SAS;
        } else if (VSPKeyBinds.DOCKING_MODE_TOGGLE_KEY.matches(pKeyCode, pScanCode)) {
            dockingMode = !dockingMode;
        } else if (SWAxis.keyPressCheck(pKeyCode, pScanCode) || ADAxis.keyPressCheck(pKeyCode, pScanCode)
                || QEAxis.keyPressCheck(pKeyCode, pScanCode)) {
            keyPressed = true;
        }
        if (dockingMode) {
            if (CtrlShiftAxis.keyPressCheck(pKeyCode, pScanCode)) {
                keyPressed = true;
            }
        } else if (throttleAxis.keyPressCheck(pKeyCode, pScanCode)) {
            keyPressed = true;
        }

        if (keyPressed) {
            return keyPressed;
        }else {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    @Override
    public void afterKeyboardAction() {
        super.afterKeyboardAction();
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        throttleAxis.resetKeys(pKeyCode, pScanCode);
        SWAxis.resetKeys(pKeyCode, pScanCode);
        ADAxis.resetKeys(pKeyCode, pScanCode);
        QEAxis.resetKeys(pKeyCode, pScanCode);
        CtrlShiftAxis.resetKeys(pKeyCode, pScanCode);
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public float getThrottleSetting() {
        return throttleAxis.getAxisValue();
    }

    @Override
    public boolean isSAS() {
        return SAS;
    }

    @Override
    public boolean isRCS() {
        return RCS;
    }

    @Override
    public boolean isDockingMode() {
        return dockingMode;
    }

    @Override
    public PlayerInputDirection getSWAxis() {
        return SWAxis;
    }

    @Override
    public PlayerInputDirection getADAxis() {
        return ADAxis;
    }

    @Override
    public PlayerInputDirection getQEAxis() {
        return QEAxis;
    }

    @Override
    public PlayerInputDirection getCtrlShiftAxis() {
        return CtrlShiftAxis;
    }

    @Override
    public boolean movePlayerCamera() {
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        player.setYBodyRot(initialYLookDir);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void sendInputs(ClientPlayerOrbitBody body) {
    }
}
