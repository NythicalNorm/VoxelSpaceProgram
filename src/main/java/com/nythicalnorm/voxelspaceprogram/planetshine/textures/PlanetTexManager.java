package com.nythicalnorm.voxelspaceprogram.planetshine.textures;

import com.mojang.blaze3d.platform.NativeImage;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ClientCelestialBody;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PlanetTexManager {
    public void incomingPlanetTexture(ClientCelestialBody planet, byte[] tex) {
        VoxelSpaceProgram.log(planet.getName() +" texture received, Size: " + tex.length);
        ByteBuffer texBytebuffer = ByteBuffer.allocateDirect(tex.length);
        texBytebuffer.put(tex);
        texBytebuffer.rewind();

        try {
            NativeImage planetImage = NativeImage.read(texBytebuffer);
            DynamicTexture texture = new DynamicTexture(planetImage);
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            ResourceLocation texResourceLocation = texturemanager.register("voxelspaceprogram/planets/" + planet.getName(), texture);
            planet.setMainTexture(texResourceLocation);
        } catch (IOException e) {
            VoxelSpaceProgram.logError(e.toString());
            VoxelSpaceProgram.logError("png texture can't be parsed");
        }
    }

    public void incomingBiomeTexture(ResourceKey<Level> dimensionID, int textureID, short textureSize, byte[] biomeTexture) {
        if (dimensionID != Minecraft.getInstance().level.dimension()) {
            return;
        }
    }
}
