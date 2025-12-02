package com.nythicalnorm.nythicalSpaceProgram.event;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.commands.NSPTeleportCommand;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetLevelData;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetLevelDataProvider;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.SolarSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.Map;

@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeServerEvents {

    @SubscribeEvent
    public static void OnTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER && event.phase != TickEvent.Phase.END) {
            return;
        }
        NythicalSpaceProgram.getSolarSystem().ifPresent(solarSystem -> {
            solarSystem.OnTick();
        });
    }

    @SubscribeEvent
    public static void onCommandsRegiser(RegisterCommandsEvent event) {
        new NSPTeleportCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void OnResourceReload(AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener((new GsonBuilder()).create(), "planet") {

            @Override
            protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                pObject.forEach((resourceLocation, ruleJsonElement) -> {
                    //NythicalSpaceProgram.log("Whaaat");
                });
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        NythicalSpaceProgram.log("Hello");
        NythicalSpaceProgram.getSolarSystem().ifPresent(solarSystem -> {
            solarSystem.playerJoined(event.getEntity());
        });
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath() && event.getEntity() instanceof ServerPlayer) {
            //NythicalSpaceProgram.getSolarSystem().playerJoined(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesLevel(AttachCapabilitiesEvent<Level> event) {
        if(!event.getObject().getCapability(PlanetLevelDataProvider.PLANET_LEVEL_DATA).isPresent()) {
            if (NythicalSpaceProgram.getSolarSystem().isPresent()) {
                SolarSystem solarSystem = NythicalSpaceProgram.getSolarSystem().get();
                if (solarSystem.getPlanets().isDimensionPlanet(event.getObject().dimension())) {
                    String planetName = solarSystem.getPlanets().getDimensionPlanet(event.getObject().dimension());
                    PlanetLevelDataProvider planetDataprovider = new PlanetLevelDataProvider(new PlanetLevelData(planetName));
                    event.addCapability(ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "planetleveldata"), planetDataprovider);
                }
            }
        }
    }

//    @SubscribeEvent
//    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
//
//    }

}
