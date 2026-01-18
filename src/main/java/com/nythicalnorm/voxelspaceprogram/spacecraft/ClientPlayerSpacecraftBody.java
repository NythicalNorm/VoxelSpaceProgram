package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.ServerboundSpacecraftMove;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PhysicsContext;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class ClientPlayerSpacecraftBody extends AbstractPlayerSpacecraftBody {
    private float sunAngle = 0f;

    public ClientPlayerSpacecraftBody(Player player) {
        super(player);
    }

    public ClientPlayerSpacecraftBody(EntitySpacecraftBody playerData, Player player) {
        this(player);

        absoluteOrbitalPos = playerData.getAbsolutePos();
        relativeOrbitalPos = playerData.getRelativePos();
        relativeVelocity = playerData.getRelativeVelocity();
        rotation = playerData.getRotation();
        orbitalElements = playerData.getOrbitalElements();
        angularVelocity = playerData.getAngularVelocity();
    }

    public void updatePlayerPosRot(Player player, PlanetaryBody currentPlanetOn) {
        updatePlanetPos(player.level(), player.position(), currentPlanetOn);
        updatePlanetRot(new Quaternionf(), currentPlanetOn);
        sunAngle = DayNightCycleHandler.getSunAngle(this.relativeOrbitalPos, this.absoluteOrbitalPos);
    }

    private void updatePlanetRot(Quaternionf existingrotation, PlanetaryBody currentPlanet) {
        //quaternion to rotate the output of lookalong function to the correct -y direction.
        this.rotation = new Quaternionf(new AxisAngle4f(Mth.HALF_PI,1f,0f,0f));
        Vector3f playerRelativePos = new Vector3f((float) relativeOrbitalPos.x, (float) relativeOrbitalPos.y, (float) relativeOrbitalPos.z);
        playerRelativePos.normalize();
        Vector3f upVector = Calcs.getUpVectorForPlanetRot(new Vector3f(playerRelativePos), currentPlanet);
        this.rotation.lookAlong(playerRelativePos, upVector);
    }

    private void updatePlanetPos(Level level, Vec3 position, PlanetaryBody currentPlanetOn) {
        double seaLevel = level.getMinBuildHeight() + 127;
        position = new Vec3(position.x, position.y - seaLevel, position.z);

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

        if (currentContext.applyAcceleration(accelerationX, accelerationY, accelerationZ, angularAcceleration)) {
            SpacecraftControlState controlState = new SpacecraftControlState(throttle, SAS, RCS, inDockingMode, this.relativeOrbitalPos, this.relativeVelocity, this.rotation, this.angularVelocity);
            PacketHandler.sendToServer(new ServerboundSpacecraftMove(this.id, controlState));
        }
    }

    public float getSunAngle() {
        return sunAngle;
    }
}
