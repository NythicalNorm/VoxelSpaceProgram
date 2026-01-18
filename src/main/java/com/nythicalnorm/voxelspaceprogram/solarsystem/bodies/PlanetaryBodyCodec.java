package com.nythicalnorm.voxelspaceprogram.solarsystem.bodies;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
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

    @Override
    public PlanetaryBody decodeBuffer(PlanetaryBody planetBody, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(planetBody, byteBuf);
        planetBody.radius = byteBuf.readDouble();
        planetBody.mass = byteBuf.readDouble();

        planetBody.NorthPoleDir = new AxisAngle4f(
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat(),
                byteBuf.readFloat()
        );

        planetBody.RotationPeriod = byteBuf.readFloat();
        planetBody.atmosphericEffects = readPlanetAtmosphere(byteBuf);

        if (byteBuf.readBoolean()) {
            planetBody.dimension = byteBuf.readResourceKey(Registries.DIMENSION);
        }

        return planetBody;
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
