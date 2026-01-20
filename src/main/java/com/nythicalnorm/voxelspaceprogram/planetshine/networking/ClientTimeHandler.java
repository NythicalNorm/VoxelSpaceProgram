package com.nythicalnorm.voxelspaceprogram.planetshine.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTimeHandler {
    private static long serverSolarSystemTimeTarget = 0L;
    private static double lerpVar = 0d;

    private static float deltaTime = 0f;

    private static volatile long serverNewSolarSystemTimeTarget = 0L;
    private static volatile boolean isServerUpdated = false;

    public static void UpdateState(long serverTime){
        serverNewSolarSystemTimeTarget = serverTime;

        isServerUpdated = true;
    }

    public static long calculateCurrentTime(long currentTime) {
        if (isServerUpdated) {
            serverSolarSystemTimeTarget = serverNewSolarSystemTimeTarget;
            lerpVar = 0;
            isServerUpdated = false;
        }

        deltaTime = Minecraft.getInstance().getDeltaFrameTime();
        lerpVar = lerpVar + deltaTime;
        lerpVar = Mth.clamp(lerpVar, 0f, 1f);

        currentTime = lerpTime(lerpVar, currentTime, serverSolarSystemTimeTarget);

        return currentTime;
    }

    public static long lerpTime(double pDelta, long pStart, long pEnd) {
        double diff = (double) (pEnd - pStart);
        return pStart + (long) (pDelta * diff);
    }
}
