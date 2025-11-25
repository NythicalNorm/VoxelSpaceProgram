package com.nythicalnorm.nythicalSpaceProgram.event;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.commands.NSPTeleportCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

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
}
