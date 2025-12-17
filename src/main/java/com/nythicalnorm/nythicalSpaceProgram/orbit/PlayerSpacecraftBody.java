package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public abstract class PlayerSpacecraftBody extends EntitySpacecraftBody {
    protected static final float JetpackRotationalForce = 0.1f;
    protected static final double JetpackTranslationForce = 1d;
    protected static final double JetpackThrottleForce = 25d;
    protected Player player;

    public PlayerSpacecraftBody() {
        this.angularVelocity = new Vector3f();
        this.velocityChangedLastFrame = false;
    }

    public void processMovement(SpacecraftControlState state) {

    }

    @Override
    public PhysicsContext getPhysicsContext() {
        //temporary dimension check will be changed to allow for different contexts
        if (player.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
            return new PhysicsSpace(player, this);
        }
        else {
            return new PhysicsPlanet(player, this);
        }
    }

    public void setPlayerEntity(Player playerNew) {
        this.player = playerNew;
    }

    public Player getPlayerEntity() {
        return player;
    }
}
