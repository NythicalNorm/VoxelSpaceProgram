package com.nythicalnorm.voxelspaceprogram.network.time;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSolarSystemTimeUpdate {
    private final long currenttime;
    private final long timePassPerSecond;

    public ClientboundSolarSystemTimeUpdate(long currenttime, long timePassPerSecond) {
        this.currenttime = currenttime;
        this.timePassPerSecond = timePassPerSecond;
    }

    public ClientboundSolarSystemTimeUpdate(FriendlyByteBuf friendlyByteBuf) {
        this.currenttime = friendlyByteBuf.readLong();
        this.timePassPerSecond = friendlyByteBuf.readLong();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeLong(this.currenttime);
        friendlyByteBuf.writeLong(this.timePassPerSecond);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT ) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.UpdateTimeState(currenttime, timePassPerSecond)));

            context.setPacketHandled(true);
        }
    }
}
