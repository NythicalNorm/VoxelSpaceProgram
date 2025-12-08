package com.nythicalnorm.nythicalSpaceProgram.orbit;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.util.Mth;
import org.joml.Vector3d;

public class OrbitalElements {
    public static final double UniversalGravitationalConstant = 6.6743E-11d;
    public double SemiMajorAxis;
    public double Eccentricity;
    public double Inclination;

    public double ArgumentOfPeriapsis;
    public double LongitudeOfAscendingNode;
    public double periapsisTime;
    public double MeanAngularMotion;

    private double Mu;
    private static final double twoPI = 2 * Math.PI;

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

    public OrbitalElements(Vector3d posOG, Vector3d velOG, double TimeElapsed, double parentMass) {
        Mu = UniversalGravitationalConstant * parentMass;
        fromCartesian(posOG, velOG, TimeElapsed);
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

        //Eccentric anomaly also this works for circular orbits I think
        double Anomaly = M;

        if (e < 1 && e > 0) { // Eccentric Anomoly
            //newton-ralphson method Ei+1 = -E(i)/ E'(i)
            for (int iter = 0; iter < 20; iter++) {
                double Eiplus1 = (Anomaly - e * Math.sin(Anomaly) - M) /  // E(i+1) = (E - e*sin(E))/(1-e*cos(E))
                        (1 - e * Math.cos(Anomaly));
                Anomaly -= Eiplus1;

                if (Math.abs(Eiplus1) < 1e-15)
                    break;
            }
        } else if (e > 1) { // Hyperbolic Anomoly
            for (int iter = 0; iter < 36; iter++) {
                double Hiplus1 = (M - (e * Math.sinh(Anomaly)) + Anomaly) /  // E(i+1) = (E - e*sin(E))/(1-e*cos(E))
                        (e * Math.cosh(Anomaly) - 1);
                Anomaly += Hiplus1;

                if (Math.abs(Hiplus1) < 1e-15)
                    break;
            }
        } else {
            NythicalSpaceProgram.logError("we have an unhandled orbit on our hands");
        }

        double semiMinorAxis = (e < 1) ? a * Math.sqrt(1 - (e*e)) : -a * Math.sqrt((e*e) - 1);

        double sinAnomaly =  (e < 1) ? Math.sin(Anomaly) : Math.sinh(Anomaly);
        double cosAnomaly =  (e < 1) ?  org.joml.Math.cosFromSin(sinAnomaly, Anomaly) : Math.cosh(Anomaly);

        double P = a * (cosAnomaly - e);
        double Q = semiMinorAxis * sinAnomaly;
        stateVectors[0] = perifocalToEquatorial(P, Q, this.ArgumentOfPeriapsis, this.Inclination, this.LongitudeOfAscendingNode);

        // Velocity Calculation:
        // Determine the square root of the standard gravitational parameter divided by the semi-latus rectum.
        double sqrtSgpOverSlr = Math.sqrt(Mu / (a*(1-e*e)) );

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

//    private Vector3d perifocalToEquatorialOld(double P, double Q, double w, double i, double W) {
//        // rotate by argument of periapsis
//        double x = Math.cos(w) * P - Math.sin(w) * Q;
//        double y = Math.sin(w) * P + Math.cos(w) * Q;
//
//        // rotate by inclination
//        double z = Math.sin(i) * y;
//        y = Math.cos(i) * y;
//        // rotate by longitude of ascending node
//        double xtemp = x;
//        x = Math.cos(W) * xtemp - Math.sin(W) * y;
//        y = Math.sin(W) * xtemp + Math.cos(W) * y;
//
//        return new Vector3d(x,z,y);
//    }

    private Vector3d perifocalToEquatorial(double P, double Q, double w, double i, double W) {
        // rotate by argument of periapsis
        double sinw = Math.sin(w);
        double cosw = org.joml.Math.cosFromSin(sinw, w);
        double x = cosw * P - sinw * Q;
        double y = sinw * P + cosw * Q;

        // rotate by inclination
        double sinI = Math.sin(i);
        double z = sinI * y;

        y = org.joml.Math.cosFromSin(sinI, i) * y;

        // rotate by longitude of ascending node
        double xtemp = x;

        double sinW = Math.sin(W);
        double cosW = org.joml.Math.cosFromSin(sinW, W);

        x = cosW * xtemp - sinW * y;
        y = sinW * xtemp + cosW * y;

        return new Vector3d(x,z,y);
    }

    // reference: https://downloads.rene-schwarz.com/download/M002-Cartesian_State_Vectors_to_Keplerian_Orbit_Elements.pdf
    // https://space.stackexchange.com/questions/65465/orbit-determination-from-position-and-velocity
    public void fromCartesian(Vector3d posOG, Vector3d velOG, double TimeElapsed) {
        Vector3d position = new Vector3d(posOG.x, posOG.z, posOG.y);
        Vector3d velocity = new Vector3d(velOG.x, velOG.z, velOG.y);
        double VelMagnitude = velocity.length();
        double PosMagnitude = position.length();

        Vector3d momentumVectorH = new Vector3d(position).cross(velocity);
        Vector3d eccentricityVector = (new Vector3d(velocity).cross(momentumVectorH)).mul(1/Mu);
        eccentricityVector.sub(new Vector3d(position).normalize());

        Vector3d pointingAscendingNode = new Vector3d(-momentumVectorH.y, momentumVectorH.x, 0); //n

        double trueAnomolyacosVar = eccentricityVector.dot(position)/(eccentricityVector.length()*PosMagnitude);

        double trueAnomoly = Math.acos(Mth.clamp(trueAnomolyacosVar, -1, 1));

        if (position.dot(velocity) < 0) {
            trueAnomoly = twoPI - trueAnomoly;
        }

        this.Inclination = Math.acos(momentumVectorH.z/momentumVectorH.length());

        this.Eccentricity = eccentricityVector.length();

        this.LongitudeOfAscendingNode = Math.acos(pointingAscendingNode.x/pointingAscendingNode.length());
        if (pointingAscendingNode.y < 0) {
            this.LongitudeOfAscendingNode = twoPI - this.LongitudeOfAscendingNode;
        }

        this.ArgumentOfPeriapsis = Math.acos(pointingAscendingNode.dot(eccentricityVector)/
                (pointingAscendingNode.length()*eccentricityVector.length()));

        //for equatorial orbits
        if (Double.isNaN(this.LongitudeOfAscendingNode)) {
            this.LongitudeOfAscendingNode = 0;
            this.ArgumentOfPeriapsis = Math.atan2(eccentricityVector.y, eccentricityVector.x);
        }

        if (eccentricityVector.z < 0) {
            this.ArgumentOfPeriapsis = twoPI - this.ArgumentOfPeriapsis;
        }

        // vis viva equation
        this.SemiMajorAxis = 1/ ( (2/PosMagnitude) - (VelMagnitude*VelMagnitude)/Mu );

        if (Eccentricity < 1) {
            double E = 2*Math.atan2( Math.tan(trueAnomoly*0.5d), Math.sqrt((1+Eccentricity)/(1-Eccentricity)) );

            this.MeanAngularMotion = Math.sqrt(Mu/(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
            this.periapsisTime = TimeElapsed - (E - Eccentricity*Math.sin(E))/this.MeanAngularMotion;
        } else {
            double H = 2*Math.atan2( Math.tan(trueAnomoly*0.5d), Math.sqrt((Eccentricity+1)/(Eccentricity-1) ));

            this.MeanAngularMotion = Math.sqrt(Mu/-(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
            this.periapsisTime = TimeElapsed - (Eccentricity*Math.sinh(H) - H)/this.MeanAngularMotion;
        }
    }

    public void setOrbitalPeriod(double parentMass) {
        if (SemiMajorAxis >= 0) {
            Mu = UniversalGravitationalConstant * parentMass;
            //double orbitalPeriod = 2 * Math.PI * Math.sqrt(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis / Mu);
            this.MeanAngularMotion = Math.sqrt(Mu/(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
        }
    }
}
