package com.nythicalnorm.nythicalSpaceProgram.event;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.map.MapSolarSystem;
import com.nythicalnorm.nythicalSpaceProgram.util.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    @SubscribeEvent
    public static void OnKeyInput (InputEvent.Key event) {
        if (KeyBindings.INC_TIME_WARP_KEY.consumeClick()) {
            NythicalSpaceProgram.getCelestialStateSupplier().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(true)));
        } else if (KeyBindings.DEC_TIME_WARP_KEY.consumeClick()) {
            NythicalSpaceProgram.getCelestialStateSupplier().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(false)));
        } else if (KeyBindings.OPEN_SOLAR_SYSTEM_MAP_KEY.consumeClick()) {
            NythicalSpaceProgram.getCelestialStateSupplier().ifPresent(celestialStateSupplier -> {
                Minecraft.getInstance().setScreen(new MapSolarSystem(Component.translatable("key.nythicalspaceprogram.open_solar_system_map")));
            });
        }
    }
}
