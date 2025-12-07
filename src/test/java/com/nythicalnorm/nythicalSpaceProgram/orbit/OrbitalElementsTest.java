package com.nythicalnorm.nythicalSpaceProgram.orbit;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrbitalElementsTest {
   @Test
   void toCartesianTest() {
       OrbitalElements elements = new OrbitalElements(
               149653496273.0d,4.657951002584728917e-6,1.704239718110438E-02,
               5.1970176873649567284,2.8619013937171278172,6.2504793475201942954);


       OrbitalElements elementsNila = new OrbitalElements(
               382599226,0.091470106618193394721,6.476694128611285E-02,
               5.4073390958703955178,2.162973108375887854,2.7140591915324141503);

       elements.setOrbitalPeriod(1.989E30);
       elementsNila.setOrbitalPeriod(5.972E24);

       long currentnanotime = System.nanoTime();
       for (int i = 0; i < 5; i++) {
           double timeElapsed = i*590147.8776f;
           Vector3d[] stateVectors = elementsNila.ToCartesian(timeElapsed);
           Vector3d pos = stateVectors[0];
           Vector3d vel = stateVectors[1];

           System.out.println("posX: " + pos.x + ", posY: " + pos.y + ", posZ: " + pos.z);
           System.out.println("velX: " + vel.x + ", velY: " + vel.y + ", velZ: " + vel.z);
           System.out.println("speed: " + vel.length());
       }
       long timepassed = System.nanoTime() - currentnanotime;
       System.out.println("took: " + (double)timepassed/1000000);
   }
}