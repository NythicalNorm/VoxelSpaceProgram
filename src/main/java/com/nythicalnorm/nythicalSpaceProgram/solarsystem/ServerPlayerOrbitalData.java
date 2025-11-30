package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import com.nythicalnorm.nythicalSpaceProgram.common.OrbitalData;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

import javax.swing.text.html.parser.Entity;

public class ServerPlayerOrbitalData extends OrbitalData {
    private Entity playerEntity;
    private String UUid;
    private boolean isStableOrbit;
    private boolean isShipBound;

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    public String getUUid() {
        return UUid;
    }

    public boolean isStableOrbit() {
        return isStableOrbit;
    }
}
