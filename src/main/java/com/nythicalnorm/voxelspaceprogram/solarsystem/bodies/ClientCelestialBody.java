package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import net.minecraft.resources.ResourceLocation;

public interface ClientCelestialBody {
    public String getName();
    public ResourceLocation getMainTexture();

    void setMainTexture(ResourceLocation texResourceLocation);
}
