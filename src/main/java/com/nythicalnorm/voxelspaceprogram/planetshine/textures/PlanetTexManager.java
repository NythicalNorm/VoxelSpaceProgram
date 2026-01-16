package com.nythicalnorm.voxelspaceprogram.planetshine.textures;

import com.mojang.blaze3d.platform.NativeImage;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.OrbitId;
import com.nythicalnorm.voxelspaceprogram.solarsystem.planet.PlanetaryBody;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Optional;

public class PlanetTexManager {
    HashMap<OrbitId, ResourceLocation> planetResourceLocations;

    public PlanetTexManager() {
        this.planetResourceLocations = new HashMap<>();
    }

    public void incomingPlanetTexture(PlanetaryBody planet, byte[] tex) {
        String planetName = planet.getName();
        VoxelSpaceProgram.log(planetName + " texture received, Size: " + tex.length);
        ByteBuffer texBytebuffer = ByteBuffer.allocateDirect(tex.length);
        texBytebuffer.put(tex);
        texBytebuffer.rewind();

        try {
            NativeImage planetImage = NativeImage.read(texBytebuffer);
            DynamicTexture texture = new DynamicTexture(planetImage);
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            ResourceLocation texResourceLocation = texturemanager.register("voxelspaceprogram/planets/" + planetName, texture);
            planetResourceLocations.put(planet.getOrbitId(), texResourceLocation);
        } catch (IOException e) {
            VoxelSpaceProgram.logError(e.toString());
            VoxelSpaceProgram.logError("png texture can't be parsed");
        }
    }

    public Optional<ResourceLocation> getTextureForPlanet(OrbitId planetName) {
        ResourceLocation returnLoc = planetResourceLocations.get(planetName);
        if (returnLoc != null) {
            return Optional.of(returnLoc);
        } else {
            return Optional.empty();
        }
    }

    public void incomingBiomeTexture(ResourceKey<Level> dimensionID, int textureID, short textureSize, byte[] biomeTexture) {
        if (dimensionID != Minecraft.getInstance().level.dimension()) {
            return;
        }


    }
}
