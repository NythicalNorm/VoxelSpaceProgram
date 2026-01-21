package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.planet;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.ClientCelestialBody;
import net.minecraft.resources.ResourceLocation;

public class ClientPlanetaryBody extends PlanetaryBody implements ClientCelestialBody {
    private ResourceLocation mainTexture;

    public ClientPlanetaryBody(PlanetBuilder planetBuilder) {
        super(planetBuilder);
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
