package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.OrbitId;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PhysicsContext;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PlayerPhysicsPlanet;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PlayerPhysicsSpace;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractPlayerSpacecraftBody extends EntitySpacecraftBody {
    protected static final float JetpackRotationalForce = 0.1f;
    protected static final double JetpackTranslationForce = 1d;
    protected static final double JetpackThrottleForce = 25d;
    protected Player player;

    public AbstractPlayerSpacecraftBody(Player pPlayer) {
        super();
        this.player = pPlayer;
        this.id = new OrbitId(pPlayer);
    }

    @Override
    public PhysicsContext getPhysicsContext() {
        //temporary dimension check will be changed to allow for different contexts
        if (player.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
            return new PlayerPhysicsSpace(player, this);
        }
        else {
            return new PlayerPhysicsPlanet(player, this);
        }
    }

    public void setPlayerEntity(Player playerNew) {
        this.player = playerNew;
    }

    public Player getPlayerEntity() {
        return player;
    }
}
