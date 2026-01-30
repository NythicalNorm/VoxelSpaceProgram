package com.nythicalnorm.voxelspaceprogram.spacecraft.physics;

import com.nythicalnorm.voxelspaceprogram.spacecraft.EntityOrbitBody;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

public abstract class PhysicsContext {
    protected final Entity playerEntity;
    protected final EntityOrbitBody orbitBody;

    public PhysicsContext(Entity playerEntity, EntityOrbitBody orbitBody) {
        this.playerEntity = playerEntity;
        this.orbitBody = orbitBody;
    }

    public abstract boolean applyAcceleration(double accelerationX, double accelerationY, double accelerationZ, Vector3f angularAcceleration);
}
