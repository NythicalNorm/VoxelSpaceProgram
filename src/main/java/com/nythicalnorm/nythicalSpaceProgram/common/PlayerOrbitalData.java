package com.nythicalnorm.nythicalSpaceProgram.common;

import com.nythicalnorm.nythicalSpaceProgram.util.DayNightCycleHandler;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class PlayerOrbitalData extends OrbitalData {
    private float sunAngle = 0f;

    public PlayerOrbitalData() {
        absoluteOrbitalPosition = new Vector3d();
        relativeOrbitalPosition = new Vector3d();
        Rotation = new Quaternionf();
    }

    public PlayerOrbitalData(Player player) {
        this();
        updatePlayerPosRot(player);
    }

    public void updatePlayerPosRot(Player player) {
        updatePlanetPos(player.level(), player.position());
        updatePlanetRot(new Quaternionf());
        sunAngle = DayNightCycleHandler.getSunAngle(this.relativeOrbitalPosition, this.absoluteOrbitalPosition);
    }

    public float getSunAngle() {
        return sunAngle;
    }
}
