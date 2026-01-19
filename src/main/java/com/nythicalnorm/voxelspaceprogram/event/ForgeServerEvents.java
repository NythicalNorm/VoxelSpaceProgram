package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.commands.NSPTeleportCommand;
import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetDataResolver;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeServerEvents {

    @SubscribeEvent
    public static void OnTick(TickEvent.ServerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            SolarSystem.getInstance().ifPresent(SolarSystem::OnTick);
        }
    }

    @SubscribeEvent
    public static void onCommandsRegiser(RegisterCommandsEvent event) {
        new NSPTeleportCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void OnResourceReload(AddReloadListenerEvent event) {
        event.addListener(new PlanetDataResolver());
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!event.getLevel().isClientSide() && event.getLevel() instanceof ServerLevel serverLevel) {
            SolarSystem.getInstance().ifPresent(solarSystem -> {
                PlanetaryBody planetaryBody = solarSystem.getPlanetsProvider().getDimensionPlanet(serverLevel.dimension());
                ((PlanetAccessor) serverLevel).setPlanetaryBody(planetaryBody);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        VoxelSpaceProgram.log("Hello");
        SolarSystem.getInstance().ifPresent(solarSystem -> solarSystem.playerJoined(event.getEntity()));
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath() && event.getEntity() instanceof ServerPlayer serverPlayer) {
            SolarSystem.getInstance().ifPresent(solarSystem -> solarSystem.playerCloned(serverPlayer));
        }
    }

    @SubscribeEvent
    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        SolarSystem.getInstance().ifPresent(solarSystem -> solarSystem.playerDimChanged(event.getEntity(), event.getTo()));
    }
}
