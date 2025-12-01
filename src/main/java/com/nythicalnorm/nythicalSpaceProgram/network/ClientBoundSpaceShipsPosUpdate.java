package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientBoundSpaceShipsPosUpdate {
    private final double currenttime;
    private final double timePassPerSecond;

    public ClientBoundSpaceShipsPosUpdate(double currenttime, double timePassPerSecond) {
        this.currenttime = currenttime;
        this.timePassPerSecond = timePassPerSecond;
    }

    public ClientBoundSpaceShipsPosUpdate(FriendlyByteBuf friendlyByteBuf) {
        this.currenttime = friendlyByteBuf.readDouble();
        this.timePassPerSecond = friendlyByteBuf.readDouble();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeDouble(this.currenttime);
        friendlyByteBuf.writeDouble(this.timePassPerSecond);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT ) {
            NetworkEvent.Context context = contextSupplier.get();
            NythicalSpaceProgram.getCelestialStateSupplier().ifPresent( celestialStateSupplier -> {
                context.enqueueWork(() -> celestialStateSupplier.UpdateState(currenttime, timePassPerSecond));
            });
            context.setPacketHandled(true);
        }
    }
}
