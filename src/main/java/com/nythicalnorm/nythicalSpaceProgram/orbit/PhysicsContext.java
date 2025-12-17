package com.nythicalnorm.nythicalSpaceProgram.orbit;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public abstract class PhysicsContext {
    protected final Entity playerEntity;
    protected final EntitySpacecraftBody orbitBody;

    public PhysicsContext(Entity playerEntity, EntitySpacecraftBody orbitBody) {
        this.playerEntity = playerEntity;
        this.orbitBody = orbitBody;
    }

    public abstract void applyAcceleration(double accelerationX, double accelerationY, double accelerationZ, Vector3f angularAcceleration);
}
