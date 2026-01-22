package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;

import java.util.List;

public abstract class Stage {
    public static final long WORLD_START_TIME = 0;
    public static final List<Long> timeWarpSettings = List.of(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L);
    public static PlanetsProvider anyPlanetsProvider;

    protected long currentTime = WORLD_START_TIME; // time passed since start in 1000 times currentTick, in milliTicks if you will.
    protected long timePassPerTick = Calcs.TickToMilliTick;
    protected int currentTimeWarpSetting = 0;
    protected final PlanetsProvider planetsProvider;

    protected Stage(PlanetsProvider planetsProvider) {
        this.planetsProvider = planetsProvider;
        if (anyPlanetsProvider == null) {
            anyPlanetsProvider = planetsProvider;
        }
    }

    // Returns the server planets provider when on singleplayer & on Dedicated Server, and returns the client planets provider in multiplayer
    public static PlanetsProvider getAnyPlanetsProvider() {
        return anyPlanetsProvider;
    }

    protected static void close() {
        anyPlanetsProvider = null;
    }

    public PlanetsProvider getPlanetsProvider() {
        return planetsProvider;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public double getCurrentTimeInSec() {
        return Calcs.timeLongToDouble(currentTime);
    }

    public int getCurrentTimeWarpSetting() {
        return currentTimeWarpSetting;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getTimePassPerTick() {
        return timePassPerTick;
    }

    public void setTimePassPerTick(long timePassPerTick) {
        this.timePassPerTick = timePassPerTick;
        currentTimeWarpSetting = timeWarpSettings.indexOf(Calcs.TimePerMilliTickToTick(getTimePassPerTick()));
    }
}
