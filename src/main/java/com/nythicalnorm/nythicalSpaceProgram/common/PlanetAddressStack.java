package com.nythicalnorm.nythicalSpaceProgram.common;

import com.nythicalnorm.nythicalSpaceProgram.planet.Star;

import java.util.Stack;

public class PlanetAddressStack {
    Stack<String> addressStack;

    public PlanetAddressStack() {
        this.addressStack = new Stack<>();
    }
    public PlanetAddressStack(Stack<String> addressStack) {
        this.addressStack = addressStack;
    }

    public void getOrbitingObj(Star starSystem) {

    }
}
