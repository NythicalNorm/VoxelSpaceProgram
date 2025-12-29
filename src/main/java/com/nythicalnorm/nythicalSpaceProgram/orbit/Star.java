package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetAtmosphere;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.*;

public class Star extends PlanetaryBody {

    public Star(PlanetAtmosphere effects, @Nullable HashMap<String, Orbit> childBody, double radius, double mass) {
        super(null, effects, childBody, radius, mass, 0f, 0, 0);
    }

    public void simulatePlanets(double currentTime) {
        this.simulatePropagate(currentTime, new Vector3d(0d, 0d, 0d), this.getMass());
    }

    public void initCalcs() {
        this.setSphereOfInfluence(Double.POSITIVE_INFINITY);
        this.calculateOrbitalPeriod();
        super.UpdateSOIs();
        this.parent = null;
        this.setChildrenParents();
    }

    public void setChildAddresses(HashMap<String, Stack<String>> allPlanetsAddresses) {
        this.name = "suriyan";
        allPlanetsAddresses.put(name, new Stack<>());

        for (Map.Entry<String, Orbit> orbitBody : childElements.entrySet()) {
            if (orbitBody.getValue() instanceof PlanetaryBody body) {
                Stack<String> currentAddress = new Stack<>();
                body.setChildAddresses(allPlanetsAddresses, currentAddress, orbitBody.getKey());
            }
        }

        //reversing the stack for future use here cause I can't be bothered to do change the recursion code. still this only runs once anyway so should be fine.
        for (Map.Entry<String, Stack<String>> entry : allPlanetsAddresses.entrySet()) {
            Stack<String> stack = entry.getValue();
            stack.sort(Collections.reverseOrder());
            entry.setValue(stack);
        }
    }
}
