package com.nythicalnorm.voxelspaceprogram;

import com.mojang.logging.LogUtils;
import com.nythicalnorm.voxelspaceprogram.Item.ModCreativeModeTab;
import com.nythicalnorm.voxelspaceprogram.Item.ModItems;
import com.nythicalnorm.voxelspaceprogram.block.ModBlocks;
import com.nythicalnorm.voxelspaceprogram.commands.ModArguments;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import com.nythicalnorm.voxelspaceprogram.sound.ModSounds;
import com.nythicalnorm.voxelspaceprogram.storage.VSPDataManager;
import com.nythicalnorm.voxelspaceprogram.util.ModItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(VoxelSpaceProgram.MODID)
public class VoxelSpaceProgram
{
    public static final String MODID = "voxelspaceprogram";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static PlanetsProvider planetsProvider;

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

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(PacketHandler::register);
    }

    public static void exitWorld() {
        planetsProvider = null;
    }

    // Returns the server planets provider when on singleplayer, and returns the client planets provider in multiplayer
    public static PlanetsProvider getAnyPlanetsProvider() {
        return planetsProvider;
    }

    public static void setPlanetsProvider(PlanetsProvider planetsProvider) {
        VoxelSpaceProgram.planetsProvider = planetsProvider;
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

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        VSPDataManager.loadServerDataAndStartSolarSystem(event.getServer());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        SolarSystem.getInstance().ifPresent(SolarSystem::serverStarted);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        SolarSystem.close();
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
}
