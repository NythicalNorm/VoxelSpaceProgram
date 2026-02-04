package com.nythicalnorm.voxelspaceprogram.network;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundHostOrbitSet;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundOrbitRemove;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundOrbitSOIChange;
import com.nythicalnorm.voxelspaceprogram.network.orbitaldata.ClientboundLoginSolarSystemState;
import com.nythicalnorm.voxelspaceprogram.network.spacecraft.ServerboundSpacecraftMove;
import com.nythicalnorm.voxelspaceprogram.network.textures.ClientboundLodTexturePacket;
import com.nythicalnorm.voxelspaceprogram.network.textures.ClientboundPlanetTexturePacket;
import com.nythicalnorm.voxelspaceprogram.network.time.ClientboundSolarSystemTimeUpdate;
import com.nythicalnorm.voxelspaceprogram.network.time.ClientboundTimeWarpUpdate;
import com.nythicalnorm.voxelspaceprogram.network.time.ServerboundTimeWarpChange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(VoxelSpaceProgram.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 500;

        INSTANCE.messageBuilder(ClientboundLoginSolarSystemState.class, ++id)
                .encoder(ClientboundLoginSolarSystemState::encode)
                .decoder(ClientboundLoginSolarSystemState::new)
                .consumerMainThread(ClientboundLoginSolarSystemState::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundSolarSystemTimeUpdate.class, ++id)
                .encoder(ClientboundSolarSystemTimeUpdate::encode)
                .decoder(ClientboundSolarSystemTimeUpdate::new)
                .consumerMainThread(ClientboundSolarSystemTimeUpdate::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundOrbitSOIChange.class, ++id)
                .encoder(ClientboundOrbitSOIChange::encode)
                .decoder(ClientboundOrbitSOIChange::new)
                .consumerMainThread(ClientboundOrbitSOIChange::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundOrbitRemove.class, ++id)
                .encoder(ClientboundOrbitRemove::encode)
                .decoder(ClientboundOrbitRemove::new)
                .consumerMainThread(ClientboundOrbitRemove::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundHostOrbitSet.class, ++id)
                .encoder(ClientboundHostOrbitSet::encode)
                .decoder(ClientboundHostOrbitSet::new)
                .consumerMainThread(ClientboundHostOrbitSet::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundTimeWarpUpdate.class, ++id)
                .encoder(ClientboundTimeWarpUpdate::encode)
                .decoder(ClientboundTimeWarpUpdate::new)
                .consumerMainThread(ClientboundTimeWarpUpdate::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundPlanetTexturePacket.class, ++id)
                .encoder(ClientboundPlanetTexturePacket::encode)
                .decoder(ClientboundPlanetTexturePacket::new)
                .consumerMainThread(ClientboundPlanetTexturePacket::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundLodTexturePacket.class, ++id)
                .encoder(ClientboundLodTexturePacket::encode)
                .decoder(ClientboundLodTexturePacket::new)
                .consumerMainThread(ClientboundLodTexturePacket::handle)
                .add();


        // Server to Client
        INSTANCE.messageBuilder(ServerboundSpacecraftMove.class, ++id)
                .encoder(ServerboundSpacecraftMove::encode)
                .decoder(ServerboundSpacecraftMove::new)
                .consumerMainThread(ServerboundSpacecraftMove::handle)
                .add();

        INSTANCE.messageBuilder(ServerboundTimeWarpChange.class, ++id)
                .encoder(ServerboundTimeWarpChange::encode)
                .decoder(ServerboundTimeWarpChange::new)
                .consumerMainThread(ServerboundTimeWarpChange::handle)
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
