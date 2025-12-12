package com.nythicalnorm.nythicalSpaceProgram.planetshine.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class ClientTimeHandler {
    private static double clientSideSolarSystemTime = 0d;
    private static double lerpVar = 0d;
    private static double serverSolarSystemTimeTarget = 0d;

    private static float deltaTime = 0f;

    private static volatile double serverNewSolarSystemTimeTarget = 0d;
    public static volatile double lastUpdatedTimeWarpPerSec = 0;
    private static volatile boolean isServerUpdated = false;


    public static void UpdateState(double serverTime, double TimePassedPerSec){
        lastUpdatedTimeWarpPerSec = TimePassedPerSec;
        serverNewSolarSystemTimeTarget = serverTime;
        isServerUpdated = true;
    }

    public static double calculateCurrentTime(float partialTick) {
        if (isServerUpdated) {
            serverSolarSystemTimeTarget = serverNewSolarSystemTimeTarget;
            lerpVar = 0;
            isServerUpdated = false;
        }

        deltaTime = Minecraft.getInstance().getDeltaFrameTime();
        lerpVar = lerpVar + deltaTime;
        lerpVar = Mth.clamp(lerpVar, 0f, 1f);
        clientSideSolarSystemTime = Mth.lerp(lerpVar, clientSideSolarSystemTime, serverSolarSystemTimeTarget);

        return clientSideSolarSystemTime;
    }

    public static double getLastUpdatedTimeWarpPerSec() {
        return lastUpdatedTimeWarpPerSec;
    }

    public static double getClientSideSolarSystemTime() {
        return clientSideSolarSystemTime;
    }
}
