package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.AbstractPlayerOrbitBody;
import com.nythicalnorm.voxelspaceprogram.storage.NBTEncoders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PlayerOrbitCodec extends OrbitCodec<AbstractPlayerOrbitBody, AbstractPlayerOrbitBody.PlayerOrbitBuilder> {

    @Override
    public void encodeBuffer(AbstractPlayerOrbitBody orbit, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(orbit, byteBuf);
        byteBuf.writeVector3f(orbit.angularVelocity);
    }

    @Override
    public AbstractPlayerOrbitBody.PlayerOrbitBuilder decodeBuffer(AbstractPlayerOrbitBody.PlayerOrbitBuilder playerSpacecraft, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(playerSpacecraft, byteBuf);
        playerSpacecraft.setAngularVelocity(byteBuf.readVector3f());
        return playerSpacecraft;
    }

    @Override
    public CompoundTag encodeNBT(AbstractPlayerOrbitBody orbit) {
        CompoundTag tag = super.encodeNBT(orbit);
        tag.put("angular_velocity", NBTEncoders.putVector3f(orbit.angularVelocity));

        return tag;
    }

    @Override
    public AbstractPlayerOrbitBody.PlayerOrbitBuilder decodeNBT(AbstractPlayerOrbitBody.PlayerOrbitBuilder orbit, CompoundTag tag) {
        super.decodeNBT(orbit, tag);
        orbit.setAngularVelocity(NBTEncoders.getVector3f(tag.getCompound("angular_velocity")));

        return orbit;
    }
}
