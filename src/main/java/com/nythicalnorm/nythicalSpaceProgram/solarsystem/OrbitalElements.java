package com.nythicalnorm.nythicalSpaceProgram.solarsystem;

import org.joml.Vector3d;

public class OrbitalElements {
    public static final float universalGravitationalConstant = 6.6743E-11f;
    public double SemiMajorAxis;
    public double Inclination;
    public double Eccentricity;

    public double ArgumentOfPeriapsis;
    public double LongitudeOfAscendingNode;
    public double StartingAnamoly;
    public double MeanAngularMotion;

    public OrbitalElements(double semimajoraxis, double inclination, double eccentricity,
                           double argumentOfperiapsis, double longitudeOfAscendingNode, double startinganamoly,
                           double orbitalperiod) {
        this.SemiMajorAxis = semimajoraxis;
        this.Inclination = inclination;
        this.Eccentricity = eccentricity;
        //this.MeanLongitude = meanlongitude;
        this.ArgumentOfPeriapsis = argumentOfperiapsis;
        this.LongitudeOfAscendingNode = longitudeOfAscendingNode;
        this.StartingAnamoly = startinganamoly;
        this.MeanAngularMotion = (2*Math.PI)/orbitalperiod;
    }

    // Reference: https://space.stackexchange.com/questions/8911/determining-orbital-position-at-a-future-point-in-time
    public Vector3d ToCartesian(double timeElapsed) {
        double a = this.SemiMajorAxis;
        double w = this.ArgumentOfPeriapsis;
        double W = this.LongitudeOfAscendingNode;
        double i = this.Inclination;
        double e = this.Eccentricity;
        //double p = this.LongitudeOfPeriapsis;

        //double M = L - p; // Calculates Mean Anomally i think
        // Calculating Mean Anamoly
        double M = this.MeanAngularMotion*(timeElapsed - this.StartingAnamoly);
        //double w = p - W; //This calcultes Argument of periapsis

        double E = M;
        while (true)
        {
            double dE = (E - e * Math.sin(E) - M) / (1 - e * Math.cos(E));
            E -= dE;
            if (Math.abs(dE) < 1e-6)
                break;
        }

        double P = a * (Math.cos(E) - e);
        double Q = a * Math.sin(E) * Math.sqrt(1 - Math.pow(e, 2));

        // rotate by argument of periapsis
        double x = Math.cos(w) * P - Math.sin(w) * Q;
        double y = Math.sin(w) * P + Math.cos(w) * Q;
        // rotate by inclination
        double z = Math.sin(i) * y;
        y = Math.cos(i) * y;
        // rotate by longitude of ascending node
        double xtemp = x;
        x = Math.cos(W) * xtemp - Math.sin(W) * y;
        y = Math.sin(W) * xtemp + Math.cos(W) * y;

        return new Vector3d(x,z,y);
    }
}
