package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;

public abstract class Stage {
    protected long currentTime; // time passed since start in 1000 times currentTick, in milliTicks if you will.
    protected long timePassPerTick;
    protected final PlanetsProvider planetsProvider;

    protected Stage(PlanetsProvider planetsProvider) {
        this.planetsProvider = planetsProvider;
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

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getTimePassPerTick() {
        return timePassPerTick;
    }

    public void setTimePassPerTick(long timePassPerTick) {
        this.timePassPerTick = timePassPerTick;
    }
}
