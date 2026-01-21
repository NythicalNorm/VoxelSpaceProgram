package com.nythicalnorm.voxelspaceprogram.spacecraft;

import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitCodec;
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
}
