package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientSyncer;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ClientboundLoginSolarSystemState {
    private final EntitySpacecraftBody playerData;
    private final List<PlanetaryBody> allPlanetaryBodies;

    public ClientboundLoginSolarSystemState(EntitySpacecraftBody playerData, List<PlanetaryBody> allPlanetaryBodies) {
        this.playerData = playerData;
        this.allPlanetaryBodies = allPlanetaryBodies;
    }

    public ClientboundLoginSolarSystemState(List<PlanetaryBody> allPlanetaryBodies) {
        this.playerData = null;
        this.allPlanetaryBodies = allPlanetaryBodies;
    }

    public ClientboundLoginSolarSystemState(FriendlyByteBuf friendlyByteBuf) {
        EntitySpacecraftBody playerDataIncoming = new EntitySpacecraftBody();

        if (friendlyByteBuf.readBoolean()) {
            Orbit entityOrbit = NetworkEncoders.readOrbitalBody(friendlyByteBuf);
            if (entityOrbit instanceof EntitySpacecraftBody spacecraftBody) {
                playerDataIncoming = spacecraftBody;
            }
        }
        this.playerData = playerDataIncoming;
        allPlanetaryBodies = NetworkEncoders.readPlanetaryBodyList(friendlyByteBuf);
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        if (playerData != null) {
            friendlyByteBuf.writeBoolean(true);
            NetworkEncoders.writeOrbitalBody(friendlyByteBuf, playerData);
        } else {
            friendlyByteBuf.writeBoolean(false);
        }
        NetworkEncoders.writePlanetaryBodyList(friendlyByteBuf, allPlanetaryBodies);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSyncer.StartClientPacket(playerData, allPlanetaryBodies)));
            context.setPacketHandled(true);
        }
    }
}
