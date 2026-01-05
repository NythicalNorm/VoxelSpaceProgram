package com.nythicalnorm.voxelspaceprogram.network.assembler;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundAssemblerProblems {
    private final Component[] problemComponents;

    public ClientboundAssemblerProblems(Component[] problemComponents) {
        this.problemComponents = problemComponents;
    }

    public ClientboundAssemblerProblems(FriendlyByteBuf friendlyByteBuf) {
       int length =  friendlyByteBuf.readVarInt();
       this.problemComponents = new Component[length];
        for (int i = 0; i < length; i++) {
            problemComponents[i] = friendlyByteBuf.readComponent();
        }
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(problemComponents.length);
        for (Component problemComponent : problemComponents) {
            friendlyByteBuf.writeComponent(problemComponent);
        }
    }

    public Component[] getProblemComponents() {
        return problemComponents;
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            VoxelSpaceProgram.getCelestialStateSupplier().ifPresent(css -> {
                css.getScreenManager().handleMenuPacket(this);
            });
        });
        context.setPacketHandled(true);
    }
}
