package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import net.minecraft.network.FriendlyByteBuf;

public class StarBodyCodec extends OrbitCodec<StarBody> {
    @Override
    public void encodeBuffer(StarBody sunBody, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(sunBody, byteBuf);
        byteBuf.writeDouble(sunBody.getRadius());
        byteBuf.writeDouble(sunBody.getMass());
        writePlanetAtmosphere(byteBuf, sunBody.getAtmosphere());
    }

    @Override
    public StarBody decodeBuffer(StarBody sunBody, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(sunBody, byteBuf);
        sunBody.radius = byteBuf.readDouble();
        sunBody.mass = byteBuf.readDouble();
        sunBody.atmosphericEffects = readPlanetAtmosphere(byteBuf);
        return sunBody;
    }

    private void writePlanetAtmosphere(FriendlyByteBuf byteBuf, PlanetAtmosphere atmosphere) {
        byteBuf.writeBoolean(atmosphere.hasAtmosphere);
        byteBuf.writeInt(atmosphere.surfaceColor);
        byteBuf.writeInt(atmosphere.atmoColor);
        byteBuf.writeDouble(atmosphere.atmosphereHeight);
        byteBuf.writeFloat(atmosphere.atmosphereAlpha);
        byteBuf.writeFloat(atmosphere.alphaNight);
        byteBuf.writeFloat(atmosphere.alphaDay);
    }

    private PlanetAtmosphere readPlanetAtmosphere(FriendlyByteBuf byteBuf) {
        return new PlanetAtmosphere(
                byteBuf.readBoolean(),
                byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readDouble(),
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat()
        );
    }
}
