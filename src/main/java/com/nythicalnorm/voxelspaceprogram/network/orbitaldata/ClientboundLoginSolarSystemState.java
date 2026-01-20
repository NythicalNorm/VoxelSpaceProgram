package com.nythicalnorm.voxelspaceprogram.network.orbitaldata;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.spacecraft.AbstractPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ServerPlayerSpacecraftBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ClientboundLoginSolarSystemState {
    private final AbstractPlayerSpacecraftBody playerData;
    private final List<PlanetaryBody> allPlanetaryBodies;
    private final OrbitId playerParentOrbit;

    public ClientboundLoginSolarSystemState(ServerPlayerSpacecraftBody playerData, List<PlanetaryBody> allPlanetaryBodies) {
        this.playerData = playerData;
        if (playerData.getParent() != null) {
            this.playerParentOrbit = playerData.getParent().getOrbitId();
        } else {
            this.playerParentOrbit = null;
        }
        this.allPlanetaryBodies = allPlanetaryBodies;
    }

    public ClientboundLoginSolarSystemState(List<PlanetaryBody> allPlanetaryBodies) {
        this.playerData = null;
        this.playerParentOrbit = null;
        this.allPlanetaryBodies = allPlanetaryBodies;
    }

    public ClientboundLoginSolarSystemState(FriendlyByteBuf friendlyByteBuf) {
        OrbitId playerParent = null;
        AbstractPlayerSpacecraftBody playerSpacecraftBody = null;

        if (friendlyByteBuf.readBoolean()) {
            Orbit entityOrbit = NetworkEncoders.readOrbitalBodyClient(friendlyByteBuf);
            if (entityOrbit instanceof AbstractPlayerSpacecraftBody spacecraftBody) {
                playerSpacecraftBody = spacecraftBody;
                if (friendlyByteBuf.readBoolean()) {
                    playerParent = new OrbitId(friendlyByteBuf);
                }
            }
        }

        this.playerData = playerSpacecraftBody;
        this.playerParentOrbit = playerParent;
        allPlanetaryBodies = NetworkEncoders.readPlanetaryBodyList(friendlyByteBuf);
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        if (playerData != null) {
            friendlyByteBuf.writeBoolean(true);
            NetworkEncoders.writeOrbitalBody(friendlyByteBuf, playerData);
            if (playerData.getParent() != null) {
                friendlyByteBuf.writeBoolean(true);
                playerData.getParent().getOrbitId().encodeToBuffer(friendlyByteBuf);
            } else {
                friendlyByteBuf.writeBoolean(false);
            }
        } else {
            friendlyByteBuf.writeBoolean(false);
        }
        NetworkEncoders.writePlanetaryBodyList(friendlyByteBuf, allPlanetaryBodies);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.StartClientPacket(playerData, playerParentOrbit, allPlanetaryBodies)));
            context.setPacketHandled(true);
        }
    }
}
