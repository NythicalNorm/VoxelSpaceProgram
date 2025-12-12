package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.networking.ClientTimeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientBoundSolarSystemTimeUpdate {
    private final double currenttime;
    private final double timePassPerSecond;

    public ClientBoundSolarSystemTimeUpdate(double currenttime, double timePassPerSecond) {
        this.currenttime = currenttime;
        this.timePassPerSecond = timePassPerSecond;
    }

    public ClientBoundSolarSystemTimeUpdate(FriendlyByteBuf friendlyByteBuf) {
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
                context.enqueueWork(() -> ClientTimeHandler.UpdateState(currenttime, timePassPerSecond));
            });
            context.setPacketHandled(true);
        }
    }
}
