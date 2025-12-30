package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class ClientboundPlanetTexturePacket {
    private final String planetName;
    private final byte[] planetTexture;

    public ClientboundPlanetTexturePacket(String planetname, byte[] planetTex) {
        this.planetName = planetname;
        this.planetTexture = planetTex;
    }

    public ClientboundPlanetTexturePacket(FriendlyByteBuf friendlyByteBuf) {
        int stringSize = friendlyByteBuf.readVarInt();
        this.planetName = friendlyByteBuf.readCharSequence(stringSize, StandardCharsets.US_ASCII).toString();
        this.planetTexture = friendlyByteBuf.readByteArray();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(planetName.length());
        friendlyByteBuf.writeCharSequence(planetName, StandardCharsets.US_ASCII);
        friendlyByteBuf.writeByteArray(this.planetTexture);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();

            VoxelSpaceProgram.getCelestialStateSupplier().ifPresent(celestialStateSupplier -> {
                context.enqueueWork(() -> celestialStateSupplier.getPlanetTexManager().incomingPlanetTexture(planetName, planetTexture));
            });
        }
    }
}
