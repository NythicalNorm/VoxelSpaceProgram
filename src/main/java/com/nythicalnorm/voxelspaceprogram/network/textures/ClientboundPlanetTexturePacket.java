package com.nythicalnorm.voxelspaceprogram.network.textures;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundPlanetTexturePacket {
    private final OrbitId planetID;
    private final byte[] planetTexture;

    public ClientboundPlanetTexturePacket(OrbitId planetId, byte[] planetTex) {
        this.planetID = planetId;
        this.planetTexture = planetTex;
    }

    public ClientboundPlanetTexturePacket(FriendlyByteBuf friendlyByteBuf) {
        this.planetID = new OrbitId(friendlyByteBuf);
        this.planetTexture = friendlyByteBuf.readByteArray();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        planetID.encodeToBuffer(friendlyByteBuf);
        friendlyByteBuf.writeByteArray(this.planetTexture);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.incomingPlanetTexture(planetID, planetTexture)));
        }
    }
}
