package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 500;

        INSTANCE.messageBuilder(ClientBoundLoginSolarSystemState.class, ++id)
                .encoder(ClientBoundLoginSolarSystemState::encode)
                .decoder(ClientBoundLoginSolarSystemState::new)
                .consumerMainThread(ClientBoundLoginSolarSystemState::handle)
                .add();

        INSTANCE.messageBuilder(ClientBoundSolarSystemTimeUpdate.class, ++id)
                .encoder(ClientBoundSolarSystemTimeUpdate::encode)
                .decoder(ClientBoundSolarSystemTimeUpdate::new)
                .consumerMainThread(ClientBoundSolarSystemTimeUpdate::handle)
                .add();

        INSTANCE.messageBuilder(ClientBoundTrackedOrbitUpdate.class, ++id)
                .encoder(ClientBoundTrackedOrbitUpdate::encode)
                .decoder(ClientBoundTrackedOrbitUpdate::new)
                .consumerMainThread(ClientBoundTrackedOrbitUpdate::handle)
                .add();

        INSTANCE.messageBuilder(ClientBoundTimeWarpUpdate.class, ++id)
                .encoder(ClientBoundTimeWarpUpdate::encode)
                .decoder(ClientBoundTimeWarpUpdate::new)
                .consumerMainThread(ClientBoundTimeWarpUpdate::handle)
                .add();

        INSTANCE.messageBuilder(ServerBoundTimeWarpChange.class, ++id)
                .encoder(ServerBoundTimeWarpChange::encode)
                .decoder(ServerBoundTimeWarpChange::new)
                .consumerMainThread(ServerBoundTimeWarpChange::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
