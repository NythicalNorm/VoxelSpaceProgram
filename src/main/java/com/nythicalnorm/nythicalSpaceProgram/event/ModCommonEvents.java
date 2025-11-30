package com.nythicalnorm.nythicalSpaceProgram.event;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.commands.NSPTeleportCommand;
import com.nythicalnorm.nythicalSpaceProgram.common.PlayerOrbitalData;
import com.nythicalnorm.nythicalSpaceProgram.common.PlayerOrbitalDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
public class ModCommonEvents {

    @SubscribeEvent
    public static void OnTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER && event.phase != TickEvent.Phase.END) {
            return;
        }
        if (NythicalSpaceProgram.getSolarSystem() != null) {
            NythicalSpaceProgram.getSolarSystem().OnTick();
        }
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
        if (NythicalSpaceProgram.getSolarSystem() != null) {
            NythicalSpaceProgram.getSolarSystem().playerJoined(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            if(!event.getObject().getCapability(PlayerOrbitalDataProvider.ORBITAL_DATA).isPresent()) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "properties"), new PlayerOrbitalDataProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerOrbitalDataProvider.ORBITAL_DATA).ifPresent(newStore -> {
                //need to change this to the orbital positon of the respawn point.
                newStore.copyFrom(new PlayerOrbitalData(event.getEntity()));
            });
        }
    }

//    @SubscribeEvent
//    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
//
//    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerOrbitalDataProvider.class);
    }

}
