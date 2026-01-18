package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import net.minecraft.network.FriendlyByteBuf;

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

    public Orbit getInstance() {
        return supplier.getInstance();
    }

    @FunctionalInterface
    public interface Supplier<T extends Orbit> {
        T getInstance();
    }
}
