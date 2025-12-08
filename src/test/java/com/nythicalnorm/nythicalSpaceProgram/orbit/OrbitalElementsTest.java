package com.nythicalnorm.nythicalSpaceProgram.orbit;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;


class OrbitalElementsTest {
    OrbitalElements elements = new OrbitalElements(
            149653496273.0d,4.657951002584728917e-6,1.704239718110438E-02,
            5.1970176873649567284,2.8619013937171278172,6.2504793475201942954);


    OrbitalElements elementsNila = new OrbitalElements(
            382599226,0.091470106618193394721,6.476694128611285E-02,
            5.4073390958703955178,2.162973108375887854,2.7140591915324141503);
    double sunMass = 1.989E30;


   @Test
   void conversionTest1() {
       //elements.setOrbitalPeriod(sunMass);
       elementsNila.setOrbitalPeriod(5.97219E24);
       long currentnanotime = System.nanoTime();

       for (int i = 0; i < 100; i++) {
           double timeElapsed = i*1000000;
           Vector3d[] stateVectors = elementsNila.ToCartesian(timeElapsed);
           Vector3d pos = stateVectors[0];
           Vector3d vel = stateVectors[1];

           OrbitalElements orbitFromState = new OrbitalElements(pos, vel, timeElapsed, 5.97219E24);

           System.out.println("---------------" + timeElapsed + "---------------");

           double semiMajor = Math.abs(elementsNila.SemiMajorAxis - orbitFromState.SemiMajorAxis);
           System.out.println("Semi Major Axis Diff: " + semiMajor);

           double eccentricity = Math.abs(elementsNila.Eccentricity - orbitFromState.Eccentricity);
           System.out.println("Eccentricity Diff: " + eccentricity);

           double inclination = Math.abs(elementsNila.Inclination - orbitFromState.Inclination);
           System.out.println("Inclination Diff: " + inclination);

           double argumentofperi = Math.abs(elementsNila.ArgumentOfPeriapsis - orbitFromState.ArgumentOfPeriapsis);
           System.out.println("Argument Of Periapsis Diff: " + argumentofperi);

           double longitudeoftheascendingnode = Math.abs(elementsNila.LongitudeOfAscendingNode - orbitFromState.LongitudeOfAscendingNode);
           System.out.println("Longitude Of Ascending Node Diff: " + longitudeoftheascendingnode);

           double meanangularmotion = Math.abs(elementsNila.MeanAngularMotion - orbitFromState.MeanAngularMotion);
           System.out.println("Mean Angular Motion Diff: " + meanangularmotion);

           double periapsistime = Math.abs(elementsNila.periapsisTime - orbitFromState.periapsisTime);
           System.out.println("periapsis Time Diff: " + periapsistime);
       }

       long timepassed = System.nanoTime() - currentnanotime;
       System.out.println("took: " + (double)timepassed/1000000);
   }

    @Test
    void conversionTest2() {
        long currentnanotime = System.nanoTime();

        Vector3d posInit = new Vector3d(-2.9936153959992264E10, -606297.1566449205, 1.4402476049850812E11);
        Vector3d velInit = new Vector3d(-29659.438335282175, 0.06573780798998548, -6164.840027386336);

        OrbitalElements orbitFromState = new OrbitalElements(posInit, velInit, 0, sunMass);

        double timeElapsed = 0;
        Vector3d[] stateVectors = orbitFromState.ToCartesian(timeElapsed);
        Vector3d pos = stateVectors[0];
        Vector3d vel = stateVectors[1];

        long timepassed = System.nanoTime() - currentnanotime;
        System.out.println("took: " + (double)timepassed/1000000);
    }

    @Test
    void HyperbolicOrbitTest() {
        long currentnanotime = System.nanoTime();
        Vector3d position = new Vector3d(6871000,50,5);
        Vector3d velocity = new Vector3d(5, 5, 12000);

        OrbitalElements orbitFromState = new OrbitalElements(position, velocity, 0, 5.972E24);
        System.out.println("--------------- Original ---------------");
        System.out.println("posX: " + position.x + " posY: " + position.y + " posZ: " + position.z);
        System.out.println("velX: " + velocity.x + " velY: " + velocity.y + " velZ: " + velocity.z);
        System.out.println("Eccentricity: " + orbitFromState.Eccentricity);

        for (int i = 0; i < 100; i++) {
            double time = i*1000;

            Vector3d[] stateVectors = orbitFromState.ToCartesian(time);
            Vector3d pos = stateVectors[0];
            Vector3d vel = stateVectors[1];

            System.out.println("---------------" + time + "---------------");
            System.out.println("posX: " + pos.x + " posY: " + pos.y + " posZ: " + pos.z);
            System.out.println("distance: " + pos.length());
            System.out.println("velX: " + vel.x + " velY: " + vel.y + " velZ: " + vel.z);
            System.out.println("Speed: " + vel.length());
        }
    }

   @Test
   void customConversionTest() {
       Vector3d position = new Vector3d(6871000,50,0);
       Vector3d velocity = new Vector3d(0, 0, 7000);
       double timeElapsed = 0;

       OrbitalElements elem = new OrbitalElements(position, velocity, timeElapsed, 5.972E24);

       Vector3d[] stateVectors = elem.ToCartesian(timeElapsed);
       Vector3d pos = stateVectors[0];
       Vector3d vel = stateVectors[1];

       System.out.println("posX: " + pos.x + " posY: " + pos.y + " posZ: " + pos.z);
       System.out.println("velX: " + vel.x + " velY: " + vel.y + " velZ: " + vel.z);
   }

   @Test
   void OrbitalElementsToStateVector() {
       elements.setOrbitalPeriod(sunMass);
       long currentnanotime = System.nanoTime();

       for (int i = 0; i < 1000000; i++) {
           double timeElapsed = i*100000;
           Vector3d[] stateVectors = elements.ToCartesian(timeElapsed);
           Vector3d pos = stateVectors[0];
           Vector3d vel = stateVectors[1];
       }

       long timepassed = System.nanoTime() - currentnanotime;
       System.out.println("Time Taken for 1 Million Elements->State Vector Transformation: " + (double)timepassed/1000000d + " ms");
   }

    @Test
    void StateVectorToOrbitalElements() {
        elements.setOrbitalPeriod(sunMass);
        long currentnanotime = System.nanoTime();
        Vector3d posInit = new Vector3d(-2.9936153959992264E10, -606297.1566449205, 1.4402476049850812E11);
        Vector3d velInit = new Vector3d(-29659.438335282175, 0.06573780798998548, -6164.840027386336);

        OrbitalElements orbitFromState = new OrbitalElements(posInit, velInit, 0, sunMass);

        for (int i = 0; i < 1000000; i++) {
            double timeElapsed = i*100000;
            Vector3d pos = new Vector3d(-2.9936153959992264E10, -606297.1566449205, 1.4402476049850812E11);
            Vector3d vel = new Vector3d(-29659.438335282175, 0.06573780798998548, -6164.840027386336);
            orbitFromState.fromCartesian(pos, vel, timeElapsed);
        }

        long timepassed = System.nanoTime() - currentnanotime;
        System.out.println("Time Taken for 1 Million State Vector->Elements Transformation: " + (double)timepassed/1000000d + " ms");
    }
}