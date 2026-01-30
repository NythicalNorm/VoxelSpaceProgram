package com.nythicalnorm.voxelspaceprogram.network.textures;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundLodTexturePacket {
    private final ResourceKey<Level> dimensionID;
    private final int textureID;
    private final int textureSize;
    private final byte[] biomeTexture;

    public ClientboundLodTexturePacket(ResourceKey<Level> pDimensionID, int pTextureID, int pTextureSize, byte[] tex) {
        this.dimensionID = pDimensionID;
        this.textureID = pTextureID;
        this.textureSize = pTextureSize;
        this.biomeTexture = tex;
    }

    public ClientboundLodTexturePacket(FriendlyByteBuf friendlyByteBuf) {
        this.dimensionID = friendlyByteBuf.readResourceKey(Registries.DIMENSION);
        this.textureID = friendlyByteBuf.readInt();
        this.textureSize = friendlyByteBuf.readInt();
        this.biomeTexture = friendlyByteBuf.readByteArray();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceKey(this.dimensionID);
        friendlyByteBuf.writeInt(this.textureID);
        friendlyByteBuf.writeInt(this.textureSize);
        friendlyByteBuf.writeByteArray(this.biomeTexture);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.incomingLodTexture(dimensionID, textureID, textureSize, biomeTexture)));
        }
    }
}
