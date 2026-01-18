package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import net.minecraft.network.FriendlyByteBuf;

public class EntityBodySpacecraftCodec extends OrbitCodec<EntitySpacecraftBody> {
    @Override
    public void encodeBuffer(EntitySpacecraftBody orbit, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(orbit, byteBuf);
        byteBuf.writeVector3f(orbit.angularVelocity);
    }

    @Override
    public EntitySpacecraftBody decodeBuffer(EntitySpacecraftBody orbit, FriendlyByteBuf byteBuf) {
        EntitySpacecraftBody entitySpacecraftBody = super.decodeBuffer(orbit, byteBuf);
        entitySpacecraftBody.angularVelocity = byteBuf.readVector3f();
        return entitySpacecraftBody;
    }
}
