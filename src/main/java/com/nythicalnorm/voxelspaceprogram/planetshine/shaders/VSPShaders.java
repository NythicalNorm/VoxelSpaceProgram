package com.nythicalnorm.voxelspaceprogram.planetshine.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class VSPShaders {
    private static ShaderInstance PLANET_SHADER;
    private static ShaderInstance SKYBOX_SHADER;

    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), VoxelSpaceProgram.rl("voxelspaceprogram_planet"),
                DefaultVertexFormat.POSITION_TEX), shaderInstance -> PLANET_SHADER = shaderInstance);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), VoxelSpaceProgram.rl("voxelspaceprogram_skybox"),
                DefaultVertexFormat.POSITION_TEX), shaderInstance -> SKYBOX_SHADER = shaderInstance);
    }

    public static ShaderInstance getPlanetShader(){
        return PLANET_SHADER;
    }

    public static ShaderInstance getSkyboxShader(){
        return SKYBOX_SHADER;
    }
}
