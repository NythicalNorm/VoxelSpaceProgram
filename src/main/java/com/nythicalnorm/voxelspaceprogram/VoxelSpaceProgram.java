package com.nythicalnorm.voxelspaceprogram;

import com.mojang.logging.LogUtils;
import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.dimensions.VSPFeatureTypes;
import com.nythicalnorm.voxelspaceprogram.sound.VSPSounds;
import com.nythicalnorm.voxelspaceprogram.util.VSPCreativeModeTab;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.fluid.VSPFluids;
import com.nythicalnorm.voxelspaceprogram.recipe.VSPRecipes;
import com.nythicalnorm.voxelspaceprogram.gui.VSPMenuTypes;
import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.Item.VSPItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VoxelSpaceProgram.MODID)
public class VoxelSpaceProgram
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "voxelspaceprogram";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public VoxelSpaceProgram(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        VSPItems.register(modEventBus);
        VSPBlocks.register(modEventBus);

        VSPSounds.register(modEventBus);
        VSPBlockEntities.register(modEventBus);

        VSPFeatureTypes.register(modEventBus);

        VSPFluids.FLUID_TYPES.register(modEventBus);
        VSPFluids.FLUIDS.register(modEventBus);

        VSPMenuTypes.register(modEventBus);
        VSPRecipes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);
        VSPCreativeModeTab.register(modEventBus);
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        event.enqueueWork(PacketHandler::register);
    }

    public static ResourceLocation rl(String location) {
        return ResourceLocation.fromNamespaceAndPath(MODID, location);
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

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            VSPItemProperties.addCustomItemProperties();
            VSPMenuTypes.registerMenus();
        }
    }
}