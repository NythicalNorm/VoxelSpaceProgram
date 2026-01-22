package com.nythicalnorm.voxelspaceprogram.solarsystem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class OrbitId {
    private final long mostSignificantBits;
    private final long leastSignificantBits;

    public OrbitId(UUID uuid) {
        this.mostSignificantBits = uuid.getMostSignificantBits();
        this.leastSignificantBits = uuid.getLeastSignificantBits();
    }

    public OrbitId(Entity entity) {
        this.mostSignificantBits = entity.getUUID().getMostSignificantBits();
        this.leastSignificantBits = entity.getUUID().getLeastSignificantBits();
    }

    public OrbitId(Long shipID) {
        this.leastSignificantBits =  shipID;
        this.mostSignificantBits = Long.MIN_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrbitId orbitId = (OrbitId) o;
        return mostSignificantBits == orbitId.mostSignificantBits && leastSignificantBits == orbitId.leastSignificantBits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostSignificantBits, leastSignificantBits);
    }

    public UUID getUUID () {
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    public Long getShipID() {
        return leastSignificantBits;
    }

    public static OrbitId getIdFromString(String name) {
        byte[] bytes = name.getBytes(StandardCharsets.US_ASCII);
        return new OrbitId(UUID.nameUUIDFromBytes(bytes));
    }

    public OrbitId(FriendlyByteBuf byteBuf) {
        this.mostSignificantBits = byteBuf.readLong();
        this.leastSignificantBits = byteBuf.readLong();
    }

    public void encodeToBuffer(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeLong(this.mostSignificantBits);
        friendlyByteBuf.writeLong(this.leastSignificantBits);
    }

    public OrbitId(CompoundTag nbt) {
        CompoundTag idBits = nbt.getCompound("orbit_id");
        this.mostSignificantBits = idBits.getLong("most");
        this.leastSignificantBits = idBits.getLong("least");
    }

    public void encodeToNBT(CompoundTag tag) {
        CompoundTag idBits = new CompoundTag();
        idBits.putLong("most", mostSignificantBits);
        idBits.putLong("least", leastSignificantBits);

        tag.put("orbit_id", idBits);
    }
}
