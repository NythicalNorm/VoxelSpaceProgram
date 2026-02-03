package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.Item.ModItems;
import com.nythicalnorm.voxelspaceprogram.Item.armor.jetpack.Jetpack;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.gui.screen.PlayerSpacecraftScreen;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.gui.screen.MapSolarSystemScreen;
import com.nythicalnorm.voxelspaceprogram.util.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    @SubscribeEvent
    public static void OnKeyInput (InputEvent.Key event) {
        if (KeyBindings.INC_TIME_WARP_KEY.consumeClick()) {
            CelestialStateSupplier.getInstance().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(true)));
        } else if (KeyBindings.DEC_TIME_WARP_KEY.consumeClick()) {
            CelestialStateSupplier.getInstance().ifPresent((celestialStateSupplier ->
                    celestialStateSupplier.TryChangeTimeWarp(false)));
        } else if (KeyBindings.OPEN_SOLAR_SYSTEM_MAP_KEY.consumeClick()) {
            CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier -> {
                if (celestialStateSupplier.doRender()) {
                    Minecraft.getInstance().setScreen(new MapSolarSystemScreen(false));
                }
            });
        }
        else if (KeyBindings.USE_PLAYER_JETPACK_KEY.consumeClick()) {
            LocalPlayer player = Minecraft.getInstance().player;
            ItemStack chestplateItem = player.getSlot(102).get();

            if (chestplateItem.getItem() instanceof Jetpack) {
                CelestialStateSupplier.getInstance().ifPresent(celestialStateSupplier -> {
                    if (celestialStateSupplier.doRender()) {
                        Minecraft.getInstance().setScreen(new PlayerSpacecraftScreen(chestplateItem, player, celestialStateSupplier));
                        celestialStateSupplier.setControllingBody(celestialStateSupplier.getPlayerOrbit());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void postPlayerRender(RenderPlayerEvent.Pre event) {
        PlayerModel<AbstractClientPlayer> playerModel = event.getRenderer().getModel();

        if (event.getEntity().getSlot(102).get().is(ModItems.CREATIVE_SPACESUIT_CHESTPLATE.get())) {
            playerModel.leftArm.visible = false;
            playerModel.rightArm.visible = false;
            playerModel.leftSleeve.visible = false;
            playerModel.rightSleeve.visible = false;
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide() && event.getLevel() instanceof ClientLevel clientLevel) {
            CelestialStateSupplier.getInstance().ifPresent(css -> css.onClientLevelLoad(clientLevel));
        }
    }

    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CelestialStateSupplier.getInstance().ifPresent(CelestialStateSupplier::tick);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(ClientPlayerNetworkEvent.LoggingOut event) {
        CelestialStateSupplier.close();
    }

    @SubscribeEvent
    public static void onPlayerCloned(ClientPlayerNetworkEvent.Clone event) {
        CelestialStateSupplier.getInstance().ifPresent(css -> {
            if (css.getPlayerOrbit() != null) {
                css.getPlayerOrbit().setPlayer(event.getNewPlayer());

                if (event.getNewPlayer().level().dimension() != SpaceDimension.SPACE_LEVEL_KEY) {
                    css.orbitRemove(css.getPlayerOrbit().getOrbitId());
                }
            }
        });
    }
}
