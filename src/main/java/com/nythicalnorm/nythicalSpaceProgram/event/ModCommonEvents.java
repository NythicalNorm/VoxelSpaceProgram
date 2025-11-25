package com.nythicalnorm.nythicalSpaceProgram.event;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.commands.NSPTeleportCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
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
        if (NythicalSpaceProgram.solarSystem != null) {
            NythicalSpaceProgram.solarSystem.OnTick();
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
}
