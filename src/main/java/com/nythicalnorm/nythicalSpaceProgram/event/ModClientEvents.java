package com.nythicalnorm.nythicalSpaceProgram.event;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.entity.ModBlockEntities;
import com.nythicalnorm.nythicalSpaceProgram.block.entity.models.MagnetizerModels;
import com.nythicalnorm.nythicalSpaceProgram.block.entity.renderer.MagnetizerBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void RegisterBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MAGNETIZER_BE.get(), MagnetizerBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MagnetizerModels.LAYER_LOCATION, MagnetizerModels::createBodyLayer);
    }
}
