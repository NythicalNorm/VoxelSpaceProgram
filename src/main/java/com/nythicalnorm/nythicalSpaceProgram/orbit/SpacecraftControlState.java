package com.nythicalnorm.nythicalSpaceProgram.orbit;

import org.joml.Vector3d;

public class SpacecraftControlState {
    public final float inputAD;
    public final float inputSW;
    public final float inputQE;
    public final float inputShiftCTRL;
    public final boolean SAS;
    public final boolean RCS;
    public final boolean inDockingMode;
    public final Vector3d position;
    public final Vector3d rotation;



    public SpacecraftControlState(float inputAD, float inputSW, float inputQE, float inputShiftCTRL,
                                  boolean SAS, boolean RCS, boolean inDockingMode, Vector3d position, Vector3d rotation) {
        this.inputAD = inputAD;
        this.inputSW = inputSW;
        this.inputQE = inputQE;
        this.inputShiftCTRL = inputShiftCTRL;
        this.SAS = SAS;
        this.RCS = RCS;
        this.inDockingMode = inDockingMode;
        this.position = position;
        this.rotation = rotation;
    }
}
