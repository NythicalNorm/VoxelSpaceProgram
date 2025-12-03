package com.nythicalnorm.nythicalSpaceProgram;

import com.mojang.logging.LogUtils;
import com.nythicalnorm.nythicalSpaceProgram.Item.ModCreativeModeTab;
import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.commands.ModArguments;
import com.nythicalnorm.nythicalSpaceProgram.orbit.EntityOrbitalBody;
import com.nythicalnorm.nythicalSpaceProgram.network.PacketHandler;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.Planets;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.SolarSystem;
import com.nythicalnorm.nythicalSpaceProgram.sound.ModSounds;
import com.nythicalnorm.nythicalSpaceProgram.util.ModItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NythicalSpaceProgram.MODID)
public class NythicalSpaceProgram
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "nythicalspaceprogram";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    //only use this in the Logical Server side
    private static SolarSystem solarSystem;
    private static CelestialStateSupplier celestialStateSupplier;

    public NythicalSpaceProgram(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSounds.register(modEventBus);
        ModArguments.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
        ModCreativeModeTab.register(modEventBus);

        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        event.enqueueWork(PacketHandler::register);
    }

    public static void log(String msg){
        LOGGER.debug(msg);
    }

    public static void logError(String msg){
        LOGGER.error(msg);
    }
    public static void logWarn(String msg){
        LOGGER.warn(msg);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        Planets planets = new Planets(false);
        solarSystem = new SolarSystem(event.getServer(), planets);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            ModItemProperties.addCustomItemProperties();
        }
    }

    public static void startClient(EntityOrbitalBody playerData) {
        Planets planets = new Planets(true);
        celestialStateSupplier = new CelestialStateSupplier(playerData, planets);
    }

    public static Optional<SolarSystem> getSolarSystem() {
        if (solarSystem != null) {
            return Optional.of(solarSystem);
        }
        return Optional.empty();
    }

    public static Optional<CelestialStateSupplier> getCelestialStateSupplier() {
        if (celestialStateSupplier != null) {
            return Optional.of(celestialStateSupplier);
        }
        else {
            return Optional.empty();
        }
    }
}
