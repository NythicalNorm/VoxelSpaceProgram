package com.nythicalnorm.nythicalSpaceProgram.planet;

import com.nythicalnorm.nythicalSpaceProgram.common.EntityBody;
import com.nythicalnorm.nythicalSpaceProgram.common.Orbit;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.*;

public class Star extends PlanetaryBody {
    public Star(PlanetAtmosphere effects, @Nullable HashMap<String, Orbit> childBody, double radius, double mass, ResourceLocation texture) {
        super(null, effects, childBody, radius, mass, 0f, 0, 0, texture);
    }

    public void simulatePlanets(double currentTime) {
        this.simulatePropagate(currentTime, new Vector3d(0d, 0d, 0d), this.getMass());
    }

    public void initCalcs() {
        this.setSphereOfInfluence(Double.POSITIVE_INFINITY);
        this.calculateOrbitalPeriod();
        super.UpdateSOIs();
    }

    public void setChildAddresses(HashMap<String, Stack<String>> allPlanetsAddresses) {
        allPlanetsAddresses.put("suriyan", new Stack<>());

        for (Map.Entry<String, Orbit> orbitBody : childElements.entrySet()) {
            if (orbitBody.getValue() instanceof PlanetaryBody) {
                PlanetaryBody body = (PlanetaryBody) orbitBody.getValue();
                Stack<String> currentAddress = new Stack<>();
                body.setChildAddresses(allPlanetsAddresses, currentAddress, orbitBody.getKey());
            }
        }

        //reversing the stack for future use here cause I can't be bothered to do change the recursion code. still this only runs once anyway so should be fine.
        for (Map.Entry<String, Stack<String>> entry : allPlanetsAddresses.entrySet()) {
            Stack<String> stack = entry.getValue();
            stack.sort(Collections.reverseOrder());;
            entry.setValue(stack);
        }
    }
}
