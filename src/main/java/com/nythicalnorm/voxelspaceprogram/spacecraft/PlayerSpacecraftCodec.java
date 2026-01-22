package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import com.nythicalnorm.voxelspaceprogram.storage.NBTEncoders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PlayerSpacecraftCodec extends OrbitCodec<AbstractPlayerSpacecraftBody, AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder> {

    @Override
    public void encodeBuffer(AbstractPlayerSpacecraftBody orbit, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(orbit, byteBuf);
        byteBuf.writeVector3f(orbit.angularVelocity);
    }

    @Override
    public AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder decodeBuffer(AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder playerSpacecraft, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(playerSpacecraft, byteBuf);
        playerSpacecraft.setAngularVelocity(byteBuf.readVector3f());
        return playerSpacecraft;
    }

    @Override
    public CompoundTag encodeNBT(AbstractPlayerSpacecraftBody orbit) {
        CompoundTag tag = super.encodeNBT(orbit);
        tag.put("angular_velocity", NBTEncoders.putVector3f(orbit.angularVelocity));

        return tag;
    }

    @Override
    public AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder decodeNBT(AbstractPlayerSpacecraftBody.PlayerSpacecraftBuilder orbit, CompoundTag tag) {
        super.decodeNBT(orbit, tag);
        orbit.setAngularVelocity(NBTEncoders.getVector3f(tag.getCompound("angular_velocity")));

        return orbit;
    }
}
