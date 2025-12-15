package com.nythicalnorm.nythicalSpaceProgram.planettexgen;

import org.joml.Vector3f;

public class NoiseHandler {
    private final OpenSimplexNoise[] noises;
    private final int octaveCount;
    private static final float[] blendVals = {1f, 0.3f, 0.25f, 0.10f, 0.05f};

    public NoiseHandler(int octavecount, long seed){
        this.octaveCount = octavecount;
        this.noises = new OpenSimplexNoise[octavecount];
        for (int i = 0; i < octavecount; i++){
            noises[i] = new OpenSimplexNoise(seed+i);
        }
    }

    public float getNoiseAt(Vector3f spherePos) {
        float totalAmplitude = 0;
        float noiseResult = 0;

        //Vector3f spherePos = getSquarePos(u,v,squareSides);

        for (int octave = 0; octave < octaveCount; octave++) {
            spherePos = spherePos.mul(2);
            noiseResult += noises[octave].eval(spherePos.x, spherePos.y, spherePos.z) * blendVals[octave];
            totalAmplitude += blendVals[octave];
        }

        noiseResult /= totalAmplitude;

        return noiseResult;
    }

    public static Vector3f getSquarePos(float sidesrightP, float sidesupP, int squareSide) {
        Vector3f squarePos = new Vector3f();
        sidesrightP = Math.fma(sidesrightP, 2f, -1f);
        sidesupP = Math.fma(sidesupP, 2f, -1f);

        squarePos = switch (squareSide) {
            case 0 -> new Vector3f(sidesrightP, sidesupP, 1f);
            case 1 -> new Vector3f(1f, sidesupP, -sidesrightP);
            case 2 -> new Vector3f(-sidesrightP, sidesupP, -1);
            case 3 -> new Vector3f(-1f, sidesupP, sidesrightP);
            case 4 -> new Vector3f(-sidesrightP, 1f, sidesupP);
            case 5 -> new Vector3f(sidesrightP, -1f, sidesupP);
            default -> squarePos;
        };
        squarePos.normalize();
        return squarePos;
    }
}
