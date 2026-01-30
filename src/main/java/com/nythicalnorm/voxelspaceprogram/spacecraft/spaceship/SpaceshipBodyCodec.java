package com.nythicalnorm.voxelspaceprogram.spacecraft.spaceship;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SpaceshipBodyCodec extends OrbitCodec<AbstractSpaceshipBody, AbstractSpaceshipBody.ShipOrbitBuilder> {
    @Override
    public void encodeBuffer(AbstractSpaceshipBody orbit, FriendlyByteBuf byteBuf) {
        super.encodeBuffer(orbit, byteBuf);
    }

    @Override
    public AbstractSpaceshipBody.ShipOrbitBuilder decodeBuffer(AbstractSpaceshipBody.ShipOrbitBuilder playerSpacecraft, FriendlyByteBuf byteBuf) {
        super.decodeBuffer(playerSpacecraft, byteBuf);
        return playerSpacecraft;
    }

    @Override
    public CompoundTag encodeNBT(AbstractSpaceshipBody orbit) {
        CompoundTag tag = super.encodeNBT(orbit);
        //tag.put("angular_velocity", NBTEncoders.putVector3f(orbit.angularVelocity));
        return tag;
    }

    @Override
    public AbstractSpaceshipBody.ShipOrbitBuilder decodeNBT(AbstractSpaceshipBody.ShipOrbitBuilder orbit, CompoundTag tag) {
        super.decodeNBT(orbit, tag);
        // orbit.setAngularVelocity(NBTEncoders.getVector3f(tag.getCompound("angular_velocity")));
        return orbit;
    }
}