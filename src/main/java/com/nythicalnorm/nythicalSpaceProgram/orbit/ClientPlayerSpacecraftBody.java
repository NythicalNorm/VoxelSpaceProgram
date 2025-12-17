package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.util.Calcs;
import com.nythicalnorm.nythicalSpaceProgram.util.DayNightCycleHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ClientPlayerSpacecraftBody extends PlayerSpacecraftBody {
    private float sunAngle = 0f;

    public ClientPlayerSpacecraftBody() {
        absoluteOrbitalPos = new Vector3d();
        relativeOrbitalPos = new Vector3d();
        rotation = new Quaternionf();
        angularVelocity = new Vector3f();
        orbitalElements = new OrbitalElements(0f,0f, 0f, 0f, 0f, 0f);
        this.player = Minecraft.getInstance().player;
    }

    public ClientPlayerSpacecraftBody(EntitySpacecraftBody playerData) {
        absoluteOrbitalPos = playerData.absoluteOrbitalPos;
        relativeOrbitalPos = playerData.relativeOrbitalPos;
        rotation = playerData.getRotation();
        orbitalElements = playerData.orbitalElements;
        this.player = Minecraft.getInstance().player;
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

    public void processLocalMovement(ItemStack jetpackItem, float inputAD, float inputSW, float inputQE, float inputShiftCTRL, float throttle, boolean SAS, boolean RCS, boolean inDockingMode) {
        if (!RCS) {
            return;
        }
        PhysicsContext currentContext = getPhysicsContext();
        Vector3f angularAcceleration = new Vector3f();
        double accelerationX = 0d;
        double accelerationY = 0d;
        double accelerationZ = 0d;


        if (inDockingMode) {
            accelerationX = inputAD*JetpackTranslationForce;
            accelerationY = inputShiftCTRL*JetpackTranslationForce;
            accelerationZ = inputSW*JetpackTranslationForce;
        } else {
            angularAcceleration = new Vector3f(inputAD, inputQE, inputSW);
            angularAcceleration.mul((JetpackRotationalForce));
            accelerationZ = inputShiftCTRL;
        }
        //Acceleration.mul(Minecraft.getInstance().getDeltaFrameTime());
        currentContext.applyAcceleration(accelerationX, accelerationY, accelerationZ, angularAcceleration);
    }

    public float getSunAngle() {
        return sunAngle;
    }
}
