package com.nythicalnorm.voxelspaceprogram.storage;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import com.nythicalnorm.voxelspaceprogram.util.Stage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class VSPCommonSaveData extends SavedData {
    private long currentTime;
    private long timeWarp;

    public VSPCommonSaveData() {
        currentTime = Stage.WORLD_START_TIME;
        timeWarp = Calcs.TickToMilliTick;
        if (SolarSystem.get() != null) {
            currentTime = SolarSystem.get().getCurrentTime();
            timeWarp = SolarSystem.get().getTimePassPerTick();
        }
    }

    public VSPCommonSaveData(long currentTime, long timeWarp) {
        this.currentTime = currentTime;
        this.timeWarp = timeWarp;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public long getTimeWarp() {
        return timeWarp;
    }

    public static VSPCommonSaveData load(CompoundTag pCompoundTag) {
        long currTime = pCompoundTag.getLong("current_time");
        long currTimeWarp = pCompoundTag.getLong("current_time_warp");

        if (!Stage.timeWarpSettings.contains(Calcs.TimePerMilliTickToTick(currTimeWarp))) {
            currTimeWarp = Calcs.TickToMilliTick;
        }

        return new VSPCommonSaveData(currTime, currTimeWarp);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
        SolarSystem solarSystem = SolarSystem.get();
        pCompoundTag.putLong("current_time", solarSystem.getCurrentTime());
        pCompoundTag.putLong("current_time_warp", solarSystem.getTimePassPerTick());
        return pCompoundTag;
    }

    @Override
    public boolean isDirty() {
        return SolarSystem.get() != null;
    }
}
