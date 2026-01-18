package com.nythicalnorm.voxelspaceprogram;

import com.mojang.logging.LogUtils;
import com.nythicalnorm.voxelspaceprogram.Item.ModCreativeModeTab;
import com.nythicalnorm.voxelspaceprogram.Item.ModItems;
import com.nythicalnorm.voxelspaceprogram.block.ModBlocks;
import com.nythicalnorm.voxelspaceprogram.commands.ModArguments;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.sound.ModSounds;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.storage.VSPDataManager;
import com.nythicalnorm.voxelspaceprogram.util.ModItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VoxelSpaceProgram.MODID)
public class VoxelSpaceProgram
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "voxelspaceprogram";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    //only use this in the Logical Server side
    private static SolarSystem solarSystem;
    private static CelestialStateSupplier celestialStateSupplier;

    public VoxelSpaceProgram(FMLJavaModLoadingContext context)
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

    public static Logger getLogger() {
        return LOGGER;
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
        PlanetsProvider planets = VSPDataManager.loadServerDataAndStartSolarSystem(event.getServer());
        solarSystem = new SolarSystem(event.getServer(), planets);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        if (VoxelSpaceProgram.getSolarSystem().isPresent()) {
            VoxelSpaceProgram.getSolarSystem().get().serverStarted();
        }
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

    public static void startClient(EntitySpacecraftBody playerData) {
        //PlanetsProvider planets = new PlanetsProvider(true);
        //celestialStateSupplier = new CelestialStateSupplier(playerData, planets);
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
