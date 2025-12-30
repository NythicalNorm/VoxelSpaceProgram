package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundBiomeTexturePacket {
    private final ResourceKey<Level> dimensionID;
    private final int textureID;
    private final short textureSize;
    private final byte[] biomeTexture;

    public ClientboundBiomeTexturePacket(ResourceKey<Level> pDimensionID, int pTextureID, short pTextureSize, byte[] tex) {
        this.dimensionID = pDimensionID;
        this.textureID = pTextureID;
        this.textureSize = pTextureSize;
        this.biomeTexture = tex;
    }

    public ClientboundBiomeTexturePacket(FriendlyByteBuf friendlyByteBuf) {
        this.dimensionID = friendlyByteBuf.readResourceKey(Registries.DIMENSION);
        this.textureID = friendlyByteBuf.readInt();
        this.textureSize = friendlyByteBuf.readShort();
        this.biomeTexture = friendlyByteBuf.readByteArray();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceKey(this.dimensionID);
        friendlyByteBuf.writeInt(this.textureID);
        friendlyByteBuf.writeShort(this.textureSize);
        friendlyByteBuf.writeByteArray(this.biomeTexture);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();

            VoxelSpaceProgram.getCelestialStateSupplier().ifPresent(celestialStateSupplier -> {
                context.enqueueWork(() -> celestialStateSupplier.getPlanetTexManager().incomingBiomeTexture(dimensionID, textureID, textureSize, biomeTexture));
            });
        }
    }
}
