package com.nythicalnorm.voxelspaceprogram.gui.screen;

import com.nythicalnorm.voxelspaceprogram.gui.input.PlayerInputAxis;
import com.nythicalnorm.voxelspaceprogram.gui.input.PlayerInputDirection;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.AltitudeWidget;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.LeftPanelWidget;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.NavballWidget;
import com.nythicalnorm.voxelspaceprogram.gui.widgets.TimeWarpWidget;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.ClientPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.util.KeyBindings;
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

@OnlyIn(Dist.CLIENT)
public class PlayerSpacecraftScreen extends MouseLookScreen {
    private ItemStack jetpackItem;
    private final LocalPlayer player;
    private final CelestialStateSupplier css;
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

    public PlayerSpacecraftScreen(ItemStack spacesuitItem, LocalPlayer player, CelestialStateSupplier css) {
        super(Component.empty());
        this.jetpackItem = spacesuitItem;
        this.player = player;
        this.css = css;
        this.minecraftOptions = Minecraft.getInstance().options;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new TimeWarpWidget(0,0, width, height, Component.empty()));
        this.addRenderableWidget(new NavballWidget(width/2, height, width, height, Component.empty()));
        this.addRenderableWidget(new LeftPanelWidget(0, height, width, height, Component.empty()));
        this.addRenderableWidget(new AltitudeWidget(width/2, 0, width, height, Component.empty()));

        minecraftOptions.setCameraType(CameraType.THIRD_PERSON_BACK);
        minecraftOptions.hideGui = true;
        player.setXRot(0f);
        zoomLevel = 4f;

        initialYLookDir = player.getViewYRot(0f);
        cameraYrot = (float) -Math.toRadians(initialYLookDir);
        player.setYBodyRot(initialYLookDir);

        css.getScreenManager().setOpenSpacecraftScreen(this);
        throttleAxis = new PlayerInputAxis(0.05f, 0f, 1f, 0.08f,0f,
                KeyBindings.DECREASE_THROTTLE_KEY, KeyBindings.INCREASE_THROTTLE_KEY);

        SWAxis = new PlayerInputDirection(minecraftOptions.keyDown, minecraftOptions.keyUp);
        ADAxis = new PlayerInputDirection(minecraftOptions.keyLeft, minecraftOptions.keyRight);
        QEAxis = new PlayerInputDirection(KeyBindings.ANTI_CLOCKWISE_SPIN_KEY, KeyBindings.CLOCKWISE_SPIN_KEY);
        CtrlShiftAxis = new PlayerInputDirection(KeyBindings.DECREASE_THROTTLE_KEY, KeyBindings.INCREASE_THROTTLE_KEY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean keyPressed = false;

        if (KeyBindings.USE_PLAYER_JETPACK_KEY.matches(pKeyCode, pScanCode)) {
            this.onClose();
            keyPressed = true;
        } else if (KeyBindings.OPEN_SOLAR_SYSTEM_MAP_KEY.matches(pKeyCode, pScanCode)) {
            Minecraft.getInstance().setScreen(new MapSolarSystemScreen(true));
            keyPressed = true;
        }  else if (KeyBindings.RCS_TOGGLE_KEY.matches(pKeyCode, pScanCode)) {
            RCS = !RCS;
        } else if (KeyBindings.SAS_TOGGLE_KEY.matches(pKeyCode, pScanCode)) {
            SAS = !SAS;
        } else if (KeyBindings.DOCKING_MODE_TOGGLE_KEY.matches(pKeyCode, pScanCode)) {
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

    public float getThrottleSetting() {
        return throttleAxis.getAxisValue();
    }

    public boolean isSAS() {
        return SAS;
    }

    public boolean isRCS() {
        return RCS;
    }

    public boolean isDockingMode() {
        return dockingMode;
    }

    public PlayerInputDirection getSWAxis() {
        return SWAxis;
    }

    public PlayerInputDirection getADAxis() {
        return ADAxis;
    }

    public PlayerInputDirection getQEAxis() {
        return QEAxis;
    }

    public PlayerInputDirection getCtrlShiftAxis() {
        return CtrlShiftAxis;
    }

    public void onClose() {
        css.getScreenManager().closeSpacecraftScreen();
        super.onClose();
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        player.setYBodyRot(initialYLookDir);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public float getViewYrot() {
        return -cameraYrot*57.29577951308232f;
    }

    public float getViewXrot() {
        return -cameraXrot*57.29577951308232f;
    }

    public void sendInputs(ClientPlayerOrbitBody body) {
        body.processLocalMovement(jetpackItem,ADAxis.getAxisValue(), SWAxis.getAxisValue(), QEAxis.getAxisValue(), CtrlShiftAxis.getAxisValue(), throttleAxis.getAxisValue(), SAS, RCS, dockingMode);
    }
}
