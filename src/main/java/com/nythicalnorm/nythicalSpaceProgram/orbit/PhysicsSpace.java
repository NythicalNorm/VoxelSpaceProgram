package com.nythicalnorm.nythicalSpaceProgram.orbit;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class PhysicsSpace extends PhysicsContext{

    public PhysicsSpace(Entity playerEntity, EntitySpacecraftBody body) {
        super(playerEntity, body);
    }

    @Override
    public void applyAcceleration(double accelerationX, double accelerationY, double accelerationZ, Vector3f angularAcceleration) {
        Quaterniond rotationQuaternion = new Quaterniond(orbitBody.rotation.x, orbitBody.rotation.y, orbitBody.rotation.z, orbitBody.rotation.w);
        double xRotated = accelerationX*Mth.sin(playerEntity.getYRot() * (Mth.PI / 180F));
        double zRotated = accelerationZ*Mth.cos(playerEntity.getYRot() * (Mth.PI / 180F));

        Vector3d Acceleration = new Vector3d(xRotated, accelerationY, zRotated);

        Vector3d totalVelocity = this.orbitBody.getRelativeVelocity().add(Acceleration.rotate(rotationQuaternion));
        Vector3f totalAngularVelocity = this.orbitBody.getAngularVelocity().add(angularAcceleration);
        totalAngularVelocity.mul(Minecraft.getInstance().getDeltaFrameTime());
        this.orbitBody.setVelocityForUpdate(totalVelocity, totalAngularVelocity);
    }
}
