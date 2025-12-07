package com.nythicalnorm.nythicalSpaceProgram.network;

import com.nythicalnorm.nythicalSpaceProgram.orbit.OrbitalElements;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class NetworkEncoders {
    public static void writeStack(FriendlyByteBuf friendlyByteBuf, Stack<String> planetAddressStack) {
        friendlyByteBuf.writeVarInt(planetAddressStack.size());
        for(String addressName : planetAddressStack){
            friendlyByteBuf.writeVarInt(addressName.length());
            friendlyByteBuf.writeCharSequence(addressName, StandardCharsets.US_ASCII);
        }
    }

    public static Stack<String> readStack(FriendlyByteBuf friendlyByteBuf) {
        Stack<String> planetAddressStack = new Stack<>();
        int arraySize = friendlyByteBuf.readVarInt();

        for(int i = 0; i < arraySize && arraySize < 16; i++){
            int stringSize = friendlyByteBuf.readVarInt();
            if(stringSize > 0 && stringSize < 128) {
                planetAddressStack.push(friendlyByteBuf.readCharSequence(stringSize, StandardCharsets.US_ASCII).toString());
            }
        }
        return planetAddressStack;
    }

    public static void writeOrbitalElements(FriendlyByteBuf friendlyByteBuf,OrbitalElements orbitalElements) {
        friendlyByteBuf.writeDouble(orbitalElements.SemiMajorAxis);
        friendlyByteBuf.writeDouble(orbitalElements.Inclination);
        friendlyByteBuf.writeDouble(orbitalElements.Eccentricity);

        friendlyByteBuf.writeDouble(orbitalElements.ArgumentOfPeriapsis);
        friendlyByteBuf.writeDouble(orbitalElements.LongitudeOfAscendingNode);
        friendlyByteBuf.writeDouble(orbitalElements.periapsisTime);
    }

    public static OrbitalElements readOrbitalElements(FriendlyByteBuf friendlyByteBuf) {
        OrbitalElements orbitElements = new OrbitalElements(
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble(),
                friendlyByteBuf.readDouble()
        );

        return orbitElements;
    }
}
