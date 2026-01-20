package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;

import java.util.concurrent.Callable;

public class SidedCallsUtil {
    public static Callable<Float> getPlayerSunAngle() {
        return () -> {
            if (CelestialStateSupplier.getInstance().isPresent()) {
                if (CelestialStateSupplier.getInstance().get().isOnPlanet()) {
                    return CelestialStateSupplier.getInstance().get().getSunAngle();
                }
            }
            return null;
        };
    }
}
