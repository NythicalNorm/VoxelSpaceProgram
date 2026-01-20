package com.nythicalnorm.voxelspaceprogram.network.time;

import com.nythicalnorm.voxelspaceprogram.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundTimeWarpUpdate {
    private final boolean successfullyChanged;
    private final int setTimeWarpSpeed;

    public ClientboundTimeWarpUpdate(boolean successfullyChanged, int pSetTimeWarpSpeed)
    {
        this.successfullyChanged = successfullyChanged;
        this.setTimeWarpSpeed = pSetTimeWarpSpeed;
    }

    public ClientboundTimeWarpUpdate(FriendlyByteBuf friendlyByteBuf) {
        this.successfullyChanged = friendlyByteBuf.readBoolean();
        this.setTimeWarpSpeed = friendlyByteBuf.readInt();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBoolean(this.successfullyChanged);
        friendlyByteBuf.writeInt(this.setTimeWarpSpeed);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (contextSupplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT ) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ClientPacketHandler.timeWarpSetFromServer(this.successfullyChanged, this.setTimeWarpSpeed)));

            context.setPacketHandled(true);
        }
    }
}
