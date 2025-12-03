package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.util.Calcs;
import com.nythicalnorm.nythicalSpaceProgram.util.DayNightCycleHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ClientPlayerSpacecraftBody extends EntityOrbitalBody {
    private float sunAngle = 0f;

    public ClientPlayerSpacecraftBody() {
        absoluteOrbitalPos = new Vector3d();
        relativeOrbitalPos = new Vector3d();
        rotation = new Quaternionf();
        orbitalElements = new OrbitalElements(0f,0f, 0f, 0f, 0f, 0f);
    }

    public ClientPlayerSpacecraftBody(EntityOrbitalBody playerData) {
        absoluteOrbitalPos = playerData.absoluteOrbitalPos;
        relativeOrbitalPos = playerData.relativeOrbitalPos;
        rotation = playerData.getRotation();
        orbitalElements = playerData.orbitalElements;
    }

    public void updatePlayerPosRot(Player player, PlanetaryBody currentPlanetOn) {
        updatePlanetPos(player.level(), player.position(), currentPlanetOn);
        updatePlanetRot(new Quaternionf(), currentPlanetOn);
        sunAngle = DayNightCycleHandler.getSunAngle(this.relativeOrbitalPos, this.absoluteOrbitalPos);
    }

    private void updatePlanetRot(Quaternionf existingrotation, PlanetaryBody currentPlanet) {
        //quaternion to rotate the output of lookalong function to the correct -y direction.
        this.rotation = new Quaternionf(new AxisAngle4f(Calcs.hPI,1f,0f,0f));
        Vector3f playerRelativePos = new Vector3f((float) relativeOrbitalPos.x, (float) relativeOrbitalPos.y, (float) relativeOrbitalPos.z);
        playerRelativePos.normalize();
        Vector3f upVector = Calcs.getUpVectorForPlanetRot(new Vector3f(playerRelativePos), currentPlanet);
        this.rotation.lookAlong(playerRelativePos, upVector);
    }

    private void updatePlanetPos(Level level, Vec3 position, PlanetaryBody currentPlanetOn) {
        relativeOrbitalPos = Calcs.planetDimPosToNormalizedVector(position, currentPlanetOn.getRadius(), currentPlanetOn.getRotation(), false);
        Vector3d newAbs = currentPlanetOn.getAbsolutePos();
        absoluteOrbitalPos = newAbs.add(relativeOrbitalPos);
    }

    public float getSunAngle() {
        return sunAngle;
    }
}
