package com.nythicalnorm.voxelspaceprogram.solarsystem.orbits;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;

public class OrbitalBodyType<T extends OrbitalBody, M extends OrbitalBody.Builder<T>> {
    private final String typeName;
    private final OrbitCodec<T, M> codec;
    private final Supplier<M> builder;

    public OrbitalBodyType(String typeName, OrbitCodec<T, M> codec, Supplier<M> builder) {
        this.codec = codec;
        this.typeName = typeName;
        this.builder = builder;
    }

    public String getTypeName() {
        return typeName;
    }

    public void encodeToBuffer(OrbitalBody orbit, FriendlyByteBuf friendlyByteBuf) {
        codec.encodeBuffer((T) orbit, friendlyByteBuf);
    }

    public M decodeFromBuffer(FriendlyByteBuf friendlyByteBuf) {
       return codec.decodeBuffer(builder.getInstance(), friendlyByteBuf);
    }

    public CompoundTag encodeToNBT(OrbitalBody orbitalBody) {
        return codec.encodeNBT((T) orbitalBody);
    }

    public M decodeFromNBT(CompoundTag tag) {
        return codec.decodeNBT(builder.getInstance(), tag);
    }

    public M readCelestialBodyDataPack(String name, JsonObject jsonObj,  Map<String, String[]> tempChildPlanetsMap) {
        return codec.readCelestialBodyDatapack(getInstance(), name, jsonObj, tempChildPlanetsMap);
    }

    //    @OnlyIn(Dist.CLIENT)
//    public OrbitalBody decodeFromBufferToClient(FriendlyByteBuf friendlyByteBuf) {
//        T orbit = (T) OrbitalBodyTypesHolder.BodyTypeClientExt.celestialBodyClientSuppliers.get(typeName).getInstance();
//        return codec.decodeBuffer(orbit, friendlyByteBuf);
//    }

    public M getInstance() {
        return builder.getInstance();
    }

    @FunctionalInterface
    public interface Supplier<M extends OrbitalBody.Builder<?>> {
        M getInstance();
    }
}
