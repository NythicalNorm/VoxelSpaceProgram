package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.orbit.EntitySpacecraftBody;
import com.nythicalnorm.nythicalSpaceProgram.orbit.ClientPlayerSpacecraftBody;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientBoundLoginSolarSystemState {
    private final ClientPlayerSpacecraftBody playerData;

    public ClientBoundLoginSolarSystemState(EntitySpacecraftBody playerData) {
        this.playerData = new ClientPlayerSpacecraftBody(playerData);
    }

    public ClientBoundLoginSolarSystemState(FriendlyByteBuf friendlyByteBuf) {
        this.playerData = new ClientPlayerSpacecraftBody();
        playerData.decode(friendlyByteBuf);
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        playerData.encode(friendlyByteBuf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> NythicalSpaceProgram.startClient(playerData));
            context.setPacketHandled(true);
        }
    }
}
