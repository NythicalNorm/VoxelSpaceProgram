package com.nythicalnorm.nythicalSpaceProgram.event;

import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.map.MapSolarSystem;
import com.nythicalnorm.nythicalSpaceProgram.util.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
                if (celestialStateSupplier.doRender()){
                    Minecraft.getInstance().setScreen(new MapSolarSystem(Component.empty()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void postPlayerRender(RenderPlayerEvent.Pre event) {
        PlayerModel<AbstractClientPlayer> playerModel = event.getRenderer().getModel();

        if (event.getEntity().getSlot(102).get().is(ModItems.CREATIVE_JETPACK.get())) {
            playerModel.leftArm.visible = false;
            playerModel.rightArm.visible = false;
            playerModel.leftSleeve.visible = false;
            playerModel.rightSleeve.visible = false;
        }
    }
}
