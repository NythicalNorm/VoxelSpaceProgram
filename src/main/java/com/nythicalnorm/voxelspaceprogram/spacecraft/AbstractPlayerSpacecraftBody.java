package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBodyType;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PhysicsContext;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PlayerPhysicsPlanet;
import com.nythicalnorm.voxelspaceprogram.spacecraft.physics.PlayerPhysicsSpace;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public abstract class AbstractPlayerSpacecraftBody extends EntitySpacecraftBody {
    protected static final float JetpackRotationalForce = 0.1f;
    protected static final double JetpackTranslationForce = 1d;
    protected static final double JetpackThrottleForce = 25d;
    protected Player player;

    public AbstractPlayerSpacecraftBody(PlayerSpacecraftBuilder playerSpacecraftBuilder) {
        super(playerSpacecraftBuilder, playerSpacecraftBuilder.angularVelocity);
        this.player = playerSpacecraftBuilder.player;
    }

    @Override
    public OrbitalBodyType<? extends OrbitalBody, ? extends Builder<?>> getType() {
        return CelestialBodyTypes.PLAYER_SPACECRAFT_BODY;
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

    public void setPlayer(@NotNull Player player) {
        this.player = player;
    }

    public Player getPlayerEntity() {
        return player;
    }

    public static class PlayerSpacecraftBuilder extends OrbitalBody.Builder<AbstractPlayerSpacecraftBody> {
        Player player = null;

        public PlayerSpacecraftBuilder() {
        }

        public void setPlayer(Player player) {
            this.player = player;
            this.displayName = player.getDisplayName();
            this.id = new OrbitId(player);
        }

        Vector3f angularVelocity = new Vector3f();

        public void setAngularVelocity(Vector3f angularVelocity) {
            this.angularVelocity = angularVelocity;
        }

        @Override
        public AbstractPlayerSpacecraftBody build() {
            return new ServerPlayerSpacecraftBody(this);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public AbstractPlayerSpacecraftBody buildClientSide() {
            return new ClientPlayerSpacecraftBody(this);
        }
    }
}
