package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import com.nythicalnorm.voxelspaceprogram.solarsystem.CelestialBodyTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OrbitalBodyType<T extends Orbit> {
    private final String typeName;
    private final OrbitCodec<T> codec;
    private final Supplier<T> supplier;

    public OrbitalBodyType(String typeName, OrbitCodec<T> codec, Supplier<T> pSupplier) {
        this.codec = codec;
        this.typeName = typeName;
        this.supplier = pSupplier;
    }

    public String getTypeName() {
        return typeName;
    }

    public <M> void encodeToBuffer(M orbit, FriendlyByteBuf friendlyByteBuf) {
        codec.encodeBuffer((T) orbit, friendlyByteBuf);
    }

    public T decodeFromBuffer(FriendlyByteBuf friendlyByteBuf) {
       return codec.decodeBuffer(supplier.getInstance(), friendlyByteBuf);
    }

    @OnlyIn(Dist.CLIENT)
    public Orbit decodeFromBufferToClient(FriendlyByteBuf friendlyByteBuf) {
        T orbit = (T) CelestialBodyTypes.BodyTypeClientExt.celestialBodyClientSuppliers.get(typeName).getInstance();
        return codec.decodeBuffer(orbit, friendlyByteBuf);
    }

    // Server Side Instance (or common instance if both sides use the same class)
    public Orbit getInstance() {
        return supplier.getInstance();
    }

    @FunctionalInterface
    public interface Supplier<T extends Orbit> {
        T getInstance();
    }
}
