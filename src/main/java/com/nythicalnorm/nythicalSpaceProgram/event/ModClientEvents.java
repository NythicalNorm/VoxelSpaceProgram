package com.nythicalnorm.nythicalSpaceProgram.event;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.generators.QuadSphereModelGenerator;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.PlanetShine;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders.ModShaders;
import com.nythicalnorm.nythicalSpaceProgram.util.KeyBindings;
import net.minecraft.Util;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void OnLevelRenderedStartEvent(RenderLevelStageEvent.RegisterStageEvent event) {
        NythicalSpaceProgram.log("Baking Planet Models: ");
        long  beforeTimes = Util.getMillis();
        QuadSphereModelGenerator.setupModels();
        PlanetShine.setupBuffers();
        NythicalSpaceProgram.log("Setup Complete Took : " + (Util.getMillis()-beforeTimes) + " milliseconds");
    }

    @SubscribeEvent
    public static void OnKeyRegister (RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.INC_TIME_WARP_KEY);
        event.register(KeyBindings.DEC_TIME_WARP_KEY);
    }

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException
    {
        // Adds a shader to the list, the callback runs when loading is complete.
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID,
                "nythicalspaceprogram_planet"), DefaultVertexFormat.POSITION_TEX), ModShaders::setPlanetShaderInstance);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID,
                "nythicalspaceprogram_skybox"), DefaultVertexFormat.POSITION_COLOR), ModShaders::setSkyboxShaderInstance);
    }
}
