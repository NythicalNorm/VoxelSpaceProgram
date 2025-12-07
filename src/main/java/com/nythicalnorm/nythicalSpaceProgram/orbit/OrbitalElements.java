package com.nythicalnorm.nythicalSpaceProgram.orbit;

import org.joml.Vector3d;

public class OrbitalElements {
    public static final double UniversalGravitationalConstant = 6.6743E-11d;
    public double SemiMajorAxis;
    public double Inclination;
    public double Eccentricity;

    public double ArgumentOfPeriapsis;
    public double LongitudeOfAscendingNode;
    public double periapsisTime;
    public double MeanAngularMotion;

    private double Mu;

    public OrbitalElements(double semimajoraxis, double inclination, double eccentricity,
                           double argumentOfperiapsis, double longitudeOfAscendingNode, double startinganamoly) {
        this.SemiMajorAxis = semimajoraxis;
        this.Inclination = inclination;
        this.Eccentricity = eccentricity;
        //this.MeanLongitude = meanlongitude;
        this.ArgumentOfPeriapsis = argumentOfperiapsis;
        this.LongitudeOfAscendingNode = longitudeOfAscendingNode;
        this.periapsisTime = startinganamoly;
        this.MeanAngularMotion = 0; //2*(2*Math.PI)/orbitalperiod; //temp fix *2 because orbits are faster than expected
    }

    // Reference: https://space.stackexchange.com/questions/8911/determining-orbital-position-at-a-future-point-in-time
    public Vector3d[] ToCartesian(double timeElapsed) {
        Vector3d[] stateVectors = new Vector3d[2];

        double a = this.SemiMajorAxis;
        double e = this.Eccentricity;

        //temp halting check
        if (e == 1) {
            e = 0.999999999999999;
        }

        // Calculating Mean Anamoly, M = n(t - t0)
        double M = this.MeanAngularMotion*(timeElapsed - this.periapsisTime);

        //Eccentric anomaly
        double E = M;

        //newton-ralphson method Ei+1 = -E(i)/ E'(i)
        for (int iter = 0; iter < 4; iter++)
        {
            double Eiplus1 = (E - e * Math.sin(E) - M) /  // E(i+1) = (E - e*sin(E))/(1-e*cos(E))
                             (1 - e * Math.cos(E));
            E -= Eiplus1;

            if (Math.abs(Eiplus1) < 1e-6)
                break;
        }

        // Position Calculation
        double P = a * (Math.cos(E) - e);
        double semiLatusRectum = a * Math.sqrt(1 - (e*e));

        double Q = semiLatusRectum *  Math.sin(E);

        stateVectors[0] = perifocalToEquatorial(P, Q, this.ArgumentOfPeriapsis, this.Inclination, this.LongitudeOfAscendingNode);

        // Velocity Calculation:
        // Determine the square root of the standard gravitational parameter divided by the semi-latus rectum.
        double sqrtSgpOverSlr = Math.sqrt(Mu / semiLatusRectum);

        //atan2 divides q/p to get the true anomoly but we are using a identity sin of arctan to get our results
        //double prearctanDiv = (Q/P);
        double prearctanDivSquareRoot = Math.sqrt(P*P+Q*Q);

        //reference https://space.stackexchange.com/questions/54596/how-to-calculate-velocity-vector-in-perifocal-coordinates
        // sin of atan2 = y/(y^2+x^2)
        double vP = -sqrtSgpOverSlr*(Q/prearctanDivSquareRoot);
        //cos of atan2 = x/(y^2+x^2)
        double vQ = sqrtSgpOverSlr*(e+P/prearctanDivSquareRoot);

        stateVectors[1] = perifocalToEquatorial(vP, vQ, this.ArgumentOfPeriapsis, this.Inclination, this.LongitudeOfAscendingNode);

        return stateVectors;
    }

    private Vector3d perifocalToEquatorial(double P, double Q, double w, double i, double W) {
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

    //reference: https://downloads.rene-schwarz.com/download/M002-Cartesian_State_Vectors_to_Keplerian_Orbit_Elements.pdf
    public void fromOrbitalElements(Vector3d position, Vector3d velocity) {
        Vector3d momentumVectorH = new Vector3d(position).cross(velocity);
        Vector3d eccentricityVector = (new Vector3d(velocity).cross(momentumVectorH)).mul(1/Mu);
        eccentricityVector.sub(new Vector3d(position).normalize());

        Vector3d pointingAscendingNode = new Vector3d(-momentumVectorH.y, momentumVectorH.x, 0);

        double trueAnomoly = Math.acos(eccentricityVector.dot(position)/(eccentricityVector.length()*position.length()));
        if (new Vector3d(position).dot(velocity) < 0) {
            trueAnomoly = Math.PI*2 - trueAnomoly;
        }

        double inclination = Math.acos(momentumVectorH.z/momentumVectorH.length());

        double e = eccentricityVector.length();

        double E = 2*Math.atan2(Math.tan(trueAnomoly*0.5d), Math.sqrt((1+e)/(1-e)));

        //double longitudeOfAscendingNode = Math.acos();
    }

    public void setOrbitalPeriod(double parentMass) {
        if (SemiMajorAxis >= 0) {
            Mu = UniversalGravitationalConstant * parentMass;
            //double orbitalPeriod = 2 * Math.PI * Math.sqrt(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis / Mu);
            this.MeanAngularMotion = Math.sqrt(Mu/(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
        }
    }
}
