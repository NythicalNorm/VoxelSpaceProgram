package com.nythicalnorm.voxelspaceprogram.network.orbitaldata;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.NetworkEncoders;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBody;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.AbstractPlayerSpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.spacecraft.ServerPlayerSpacecraftBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ClientboundLoginSolarSystemState {
    private final long currentTime;
    private final long currentTimeWarp;
    private final AbstractPlayerSpacecraftBody playerData;
    private final List<CelestialBody> allPlanetaryBodies;
    private final OrbitId playerParentOrbit;

    public ClientboundLoginSolarSystemState(@Nullable ServerPlayerSpacecraftBody playerData, List<CelestialBody> allPlanetaryBodies,
                                            long currentTime, long timeWarp) {
        this.currentTime = currentTime;
        this.currentTimeWarp = timeWarp;
        this.playerData = playerData;

        OrbitId parentID = null;
        if (playerData != null) {
            if (playerData.getParent() != null) {
                parentID = playerData.getParent().getOrbitId();
            }
        }
        playerParentOrbit = parentID;
        this.allPlanetaryBodies = allPlanetaryBodies;
    }


    public ClientboundLoginSolarSystemState(FriendlyByteBuf friendlyByteBuf) {
        currentTime = friendlyByteBuf.readLong();
        currentTimeWarp = friendlyByteBuf.readLong();
        OrbitId playerParent = null;
        AbstractPlayerSpacecraftBody playerSpacecraftBody = null;

        if (friendlyByteBuf.readBoolean()) {
            OrbitalBody entityOrbit = NetworkEncoders.readOrbitalBodyClient(friendlyByteBuf);
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
        friendlyByteBuf.writeLong(currentTime);
        friendlyByteBuf.writeLong(currentTimeWarp);
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
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.StartClientPacket(currentTime, currentTimeWarp, playerData, playerParentOrbit, allPlanetaryBodies)));
            context.setPacketHandled(true);
        }
    }
}
