package com.nythicalnorm.voxelspaceprogram.solarsystem.planet;

import com.nythicalnorm.voxelspaceprogram.solarsystem.OrbitCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.AxisAngle4f;

public class PlanetaryBodyCodec extends OrbitCodec<PlanetaryBody> {

    @Override
    public void encodeBuffer(PlanetaryBody planetBody, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(planetBody, byteBuf);
        byteBuf.writeDouble(planetBody.getRadius());
        byteBuf.writeDouble(planetBody.getMass());

        byteBuf.writeFloat(planetBody.getNorthPoleDir().x);
        byteBuf.writeFloat(planetBody.getNorthPoleDir().y);
        byteBuf.writeFloat(planetBody.getNorthPoleDir().z);
        byteBuf.writeFloat(planetBody.getNorthPoleDir().angle);

        byteBuf.writeFloat(planetBody.getRotationPeriod());
        writePlanetAtmosphere(byteBuf, planetBody.getAtmosphere());

        if (planetBody.getDimension() == null) {
            byteBuf.writeBoolean(false);
        } else {
            byteBuf.writeBoolean(true);
            byteBuf.writeResourceKey(planetBody.getDimension());
        }
    }

    private void writePlanetAtmosphere(FriendlyByteBuf byteBuf, PlanetAtmosphere atmosphere) {
        byteBuf.writeBoolean(atmosphere.hasAtmosphere);
        byteBuf.writeInt(atmosphere.overlayColor);
        byteBuf.writeInt(atmosphere.atmoColor);
        byteBuf.writeDouble(atmosphere.atmosphereHeight);
        byteBuf.writeFloat(atmosphere.atmosphereAlpha);
        byteBuf.writeFloat(atmosphere.exposureNight);
        byteBuf.writeFloat(atmosphere.exposureDay);
    }

    @Override
    public PlanetaryBody decodeBuffer(PlanetaryBody planetBody, FriendlyByteBuf byteBuf) {
        PlanetaryBody planetaryBody = super.decodeBuffer(planetBody, byteBuf);
        planetaryBody.radius = byteBuf.readDouble();
        planetaryBody.mass = byteBuf.readDouble();

        planetaryBody.NorthPoleDir = new AxisAngle4f(
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat()
        );

        planetaryBody.RotationPeriod = byteBuf.readFloat();
        planetaryBody.atmosphericEffects = readPlanetAtmosphere(byteBuf);

        if (byteBuf.readBoolean()) {
            planetBody.dimension = byteBuf.readResourceKey(Registries.DIMENSION);
        }

        return planetaryBody;
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
