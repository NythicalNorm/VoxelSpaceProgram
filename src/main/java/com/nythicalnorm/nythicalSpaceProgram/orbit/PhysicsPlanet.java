package com.nythicalnorm.nythicalSpaceProgram.orbit;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PhysicsPlanet extends PhysicsContext{
    public PhysicsPlanet(Entity playerEntity, EntitySpacecraftBody orbitBody) {
        super(playerEntity, orbitBody);
    }

    @Override
    public void applyAcceleration(double accelerationX, double accelerationY, double accelerationZ, Vector3f angularAcceleration) {
        if (playerEntity instanceof LocalPlayer player) {
            player.travel(new Vec3(-accelerationX, accelerationY, -accelerationZ));
        }
    }
}
