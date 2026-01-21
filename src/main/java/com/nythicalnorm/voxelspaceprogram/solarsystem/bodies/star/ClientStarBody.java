package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.star;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ClientCelestialBody;
import net.minecraft.resources.ResourceLocation;

public class ClientStarBody extends StarBody implements ClientCelestialBody {
    private ResourceLocation mainTexture;

    public ClientStarBody(StarBuilder starBuilder) {
        super(starBuilder);
    }

    @Override
    public ResourceLocation getMainTexture() {
        return mainTexture;
    }

    @Override
    public void setMainTexture(ResourceLocation texResourceLocation) {
        mainTexture = texResourceLocation;
    }
}
