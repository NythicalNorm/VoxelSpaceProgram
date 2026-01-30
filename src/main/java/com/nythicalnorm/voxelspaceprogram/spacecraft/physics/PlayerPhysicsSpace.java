package com.nythicalnorm.voxelspaceprogram.spacecraft.physics;

import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityOrbitBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class PlayerPhysicsSpace extends PhysicsContext{
    private static final double SIGNIFIACANT_ACCELERATION = 1e-9;

    public PlayerPhysicsSpace(Entity playerEntity, EntityOrbitBody body) {
        super(playerEntity, body);
    }

    @Override
    public boolean applyAcceleration(double accelerationX, double accelerationY, double accelerationZ, Vector3f angularAcceleration) {
        if (isInsignifanctForce(accelerationX) && isInsignifanctForce(accelerationY) && isInsignifanctForce(accelerationZ)) {
            return false;
        }

        Quaterniond rotationQuaternion = Calcs.quaternionFtoD(orbitBody.getRotation());

        double xRotated = accelerationX*Mth.sin(playerEntity.getYRot() * (Mth.PI / 180F));
        double zRotated = accelerationZ*Mth.cos(playerEntity.getYRot() * (Mth.PI / 180F));

        Vector3d Acceleration = new Vector3d(xRotated, accelerationY, zRotated);

        Vector3d totalVelocity = this.orbitBody.getRelativeVelocity().add(Acceleration.rotate(rotationQuaternion));
        Vector3f totalAngularVelocity = this.orbitBody.getAngularVelocity().add(angularAcceleration);
        totalAngularVelocity.mul(Minecraft.getInstance().getDeltaFrameTime());
        this.orbitBody.setVelocityForUpdate(totalVelocity, totalAngularVelocity);
        return true;
    }

    private boolean isInsignifanctForce(double acceleration) {
        return Math.abs(acceleration) < SIGNIFIACANT_ACCELERATION;
    }
}
