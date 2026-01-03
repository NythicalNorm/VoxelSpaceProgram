package com.nythicalnorm.voxelspaceprogram.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_VOXEL_SPACE_PROGRAM = "key.category.voxelspaceprogram.main";
    public static final String KEY_INCREASE_TIME_WARP = "key.voxelspaceprogram.increase_time_warp";
    public static final String KEY_DECREASE_TIME_WARP = "key.voxelspaceprogram.decrease_time_warp";
    public static final String KEY_OPEN_SOLAR_SYSTEM_MAP = "key.voxelspaceprogram.open_solar_system_map";
    public static final String KEY_USE_PLAYER_JETPACK = "key.voxelspaceprogram.use_player_jetpack_key";

    public static final KeyMapping INC_TIME_WARP_KEY = new KeyMapping(KEY_INCREASE_TIME_WARP, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, KEY_CATEGORY_VOXEL_SPACE_PROGRAM);

    public static final KeyMapping DEC_TIME_WARP_KEY = new KeyMapping(KEY_DECREASE_TIME_WARP, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, KEY_CATEGORY_VOXEL_SPACE_PROGRAM);

    public static final KeyMapping OPEN_SOLAR_SYSTEM_MAP_KEY = new KeyMapping(KEY_OPEN_SOLAR_SYSTEM_MAP, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, KEY_CATEGORY_VOXEL_SPACE_PROGRAM);

    public static final KeyMapping USE_PLAYER_JETPACK_KEY = new KeyMapping(KEY_USE_PLAYER_JETPACK, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, KEY_CATEGORY_VOXEL_SPACE_PROGRAM);

    public static final String KEY_CATEGORY_SPACECRAFT_CONTROL = "key.category.voxelspaceprogram.spacecraft_control";
    public static final String KEY_INCREASE_THROTTLE = "key.voxelspaceprogram.increase_throttle";
    public static final String KEY_DECREASE_THROTTLE = "key.voxelspaceprogram.decrease_throttle";
    public static final String KEY_STAGING = "key.voxelspaceprogram.staging";
    public static final String KEY_RCS_TOGGLE = "key.voxelspaceprogram.rcs_toggle";
    public static final String KEY_SAS_TOGGLE = "key.voxelspaceprogram.sas_toggle";
    public static final String KEY_DOCKING_MODE_TOGGLE = "key.voxelspaceprogram.docking_mode_toggle";

    public static final String KEY_CLOCKWISE_SPIN = "key.voxelspaceprogram.clockwise_spin";
    public static final String KEY_ANTI_CLOCKWISE_SPIN = "key.voxelspaceprogram.anti_clockwise_spin";

    public static final KeyMapping INCREASE_THROTTLE_KEY = new KeyMapping(KEY_INCREASE_THROTTLE, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping DECREASE_THROTTLE_KEY = new KeyMapping(KEY_DECREASE_THROTTLE, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping STAGING_KEY = new KeyMapping(KEY_STAGING, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping RCS_TOGGLE_KEY = new KeyMapping(KEY_RCS_TOGGLE, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping SAS_TOGGLE_KEY = new KeyMapping(KEY_SAS_TOGGLE, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_T, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping DOCKING_MODE_TOGGLE_KEY = new KeyMapping(KEY_DOCKING_MODE_TOGGLE, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping CLOCKWISE_SPIN_KEY = new KeyMapping(KEY_CLOCKWISE_SPIN, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_E, KEY_CATEGORY_SPACECRAFT_CONTROL);

    public static final KeyMapping ANTI_CLOCKWISE_SPIN_KEY = new KeyMapping(KEY_ANTI_CLOCKWISE_SPIN, KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Q, KEY_CATEGORY_SPACECRAFT_CONTROL);

}
