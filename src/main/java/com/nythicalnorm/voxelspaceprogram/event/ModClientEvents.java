package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.VSPBlockEntities;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.models.MagnetizerModels;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.models.SheetMetalRollerModel;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.renderer.MagnetizerBlockEntityRenderer;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.renderer.SheetMetalRollerRenderer;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models.EngineModelData;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity_renderers.EngineEntityRenderer;
import com.nythicalnorm.voxelspaceprogram.util.VSPKeyBinds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void RegisterBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VSPBlockEntities.MAGNETIZER_BE.get(), MagnetizerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(VSPBlockEntities.SHEET_METAL_ROLLER_BE.get(), SheetMetalRollerRenderer::new);
        event.registerBlockEntityRenderer(VSPBlockEntities.ENGINE_BE.get(), EngineEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MagnetizerModels.LAYER_LOCATION, MagnetizerModels::createBodyLayer);
        event.registerLayerDefinition(SheetMetalRollerModel.LAYER_LOCATION, SheetMetalRollerModel::createBodyLayer);
        event.registerLayerDefinition(EngineModelData.ThreeKeroloxLayerLoc, EngineModelData::createThreeKeroloxModel);
        event.registerLayerDefinition(EngineModelData.TwoKeroloxLayerLoc, EngineModelData::createTwoKeroloxModel);
    }

    @SubscribeEvent
    public static void OnKeyRegister (RegisterKeyMappingsEvent event) {
        event.register(VSPKeyBinds.INCREASE_THROTTLE_KEY);
        event.register(VSPKeyBinds.DECREASE_THROTTLE_KEY);
        event.register(VSPKeyBinds.STAGING_KEY);
        event.register(VSPKeyBinds.RCS_TOGGLE_KEY);
        event.register(VSPKeyBinds.SAS_TOGGLE_KEY);
        event.register(VSPKeyBinds.DOCKING_MODE_TOGGLE_KEY);
        event.register(VSPKeyBinds.CLOCKWISE_SPIN_KEY);
        event.register(VSPKeyBinds.ANTI_CLOCKWISE_SPIN_KEY);
    }
}
