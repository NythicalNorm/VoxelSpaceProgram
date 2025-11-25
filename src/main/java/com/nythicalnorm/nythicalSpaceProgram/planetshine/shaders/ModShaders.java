package com.nythicalnorm.nythicalSpaceProgram.planetshine.shaders;

import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Supplier;

// function is from  @gigaherz on github
public class ModShaders {
    private static ShaderInstance PLANETSHADER;
    private static ShaderInstance SKYBOXSHADER;

    private static Supplier<ShaderInstance> SKYBOXSHADERSUPPLIER;
    private static Supplier<ShaderInstance> PLANETSHADERSUPPLIER;

    public static void setPlanetShaderInstance(ShaderInstance planet){
        PLANETSHADER = planet;
        PLANETSHADERSUPPLIER = () -> PLANETSHADER;
    }

    public static void setSkyboxShaderInstance(ShaderInstance shad){
        SKYBOXSHADER = shad;
        SKYBOXSHADERSUPPLIER = () -> SKYBOXSHADER;
    }

    public static Supplier<ShaderInstance> getPlanetShaderInstance(){
        return PLANETSHADERSUPPLIER;
    }

    public static Supplier<ShaderInstance> getSkyboxShaderInstance(){
        return SKYBOXSHADERSUPPLIER;
    }
}
