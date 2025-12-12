package com.nythicalnorm.nythicalSpaceProgram.Item.armor;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModModelLayers {
    public static ModelLayerLocation JETPACK_LAYER = new ModelLayerLocation(ResourceLocation.withDefaultNamespace("player"), "nsp_spacesuit");

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(JETPACK_LAYER, JetpackModel::getSpacesuitLayer);
    }
}
