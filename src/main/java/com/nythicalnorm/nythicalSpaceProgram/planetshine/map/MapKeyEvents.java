package com.nythicalnorm.nythicalSpaceProgram.planetshine.map;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MapKeyEvents {
    @SubscribeEvent
    public static void OnKeyInput (ScreenEvent event) {

    }
}