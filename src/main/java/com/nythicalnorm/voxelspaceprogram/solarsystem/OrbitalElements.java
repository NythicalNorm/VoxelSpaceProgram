package com.nythicalnorm.voxelspaceprogram.solarsystem;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.util.Mth;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class OrbitalElements {
    public static final double UniversalGravitationalConstant = 6.6743E-11d;
    public double SemiMajorAxis;
    public double Eccentricity;
    public long periapsisTime;

    public double Inclination;
    public double ArgumentOfPeriapsis;
    public double LongitudeOfAscendingNode;

    public double MeanAngularMotion;

    public Quaterniond orbitRotation;

    private double Mu;
    private static final double twoPI = 2 * Math.PI;

    public OrbitalElements(double semimajoraxis, double inclination, double eccentricity,
                           double argumentOfperiapsis, double longitudeOfAscendingNode, long startinganamoly) {
        this.SemiMajorAxis = semimajoraxis;
        this.Eccentricity = eccentricity;
        this.periapsisTime = startinganamoly;

        this.Inclination = inclination;
        this.ArgumentOfPeriapsis = argumentOfperiapsis;
        this.LongitudeOfAscendingNode = longitudeOfAscendingNode;

        setOrbitRotationFromElements(argumentOfperiapsis, inclination, longitudeOfAscendingNode);

        this.MeanAngularMotion = 0; //2*(2*Math.PI)/orbitalperiod; //temp fix *2 because orbits are faster than expected
    }

    public OrbitalElements(Vector3d pos, Vector3d vel, long TimeElapsed, double parentMass) {
        Mu = UniversalGravitationalConstant * parentMass;
        fromCartesian(pos, vel, TimeElapsed);
    }

    // Reference: https://space.stackexchange.com/questions/8911/determining-orbital-position-at-a-future-point-in-time
    public Vector3d[] ToCartesian(long timeElapsed) {
        Vector3d[] stateVectors = new Vector3d[2];

        double a = this.SemiMajorAxis;
        double e = this.Eccentricity;
        boolean isElliptical = e < 1;

        //temp halting check
//        if (e == 1) {
//            e = 0.999999999999999;
//        }

        // Calculating Mean Anamoly, M = n(t - t0)
        long diff = timeElapsed - this.periapsisTime;

        double M = this.MeanAngularMotion*(Calcs.timeLongToDouble(diff));

        //Eccentric anomaly also this works for circular orbits I think
        double Anomaly = M;

        if (isElliptical && e > 0) { // Eccentric Anomoly
            //newton-ralphson method Ei+1 = -E(i)/ E'(i)
            for (int iter = 0; iter < 100; iter++) {
                double Eiplus1 = (Anomaly - e * Math.sin(Anomaly) - M) /  // E(i+1) = (E - e*sin(E))/(1-e*cos(E))
                        (1 - e * Math.cos(Anomaly));
                Anomaly -= Eiplus1;

                if (Math.abs(Eiplus1) < 1e-15)
                    break;
            }
        } else if (e > 1) {
            // better initial guess: https://arxiv.org/html/2411.15374v1#S4.F2
            if (M != 0) {
                Anomaly = Math.log((2*M) / (e+1.8));
            }

            // Hyperbolic Anomoly
            for (int iter = 0; iter < 512; iter++) {
                double Hiplus1 = ((e * Math.sinh(Anomaly)) - Anomaly - M) / ((e * Math.cosh(Anomaly)) - 1);
                Anomaly -= Hiplus1;

                if (Math.abs(Hiplus1) < 1e-15 || Double.isNaN(Anomaly)) {
                    break;
                }
            }
        } else if (e != 0) {
            VoxelSpaceProgram.logError("we have an unhandled orbit on our hands");
        }

        double semiMinorAxis = (isElliptical) ? a * Math.sqrt(1 - (e*e)) : -a * Math.sqrt((e*e) - 1);

        double sinAnomaly =  (isElliptical) ? Math.sin(Anomaly) : Math.sinh(Anomaly);
        double cosAnomaly =  (isElliptical) ?  org.joml.Math.cosFromSin(sinAnomaly, Anomaly) : Math.cosh(Anomaly);

        double P = a * (cosAnomaly - e);
        double Q = semiMinorAxis * sinAnomaly;
        stateVectors[0] = perifocalToEquatorial(P, Q); //, this.ArgumentOfPeriapsis, this.Inclination, this.LongitudeOfAscendingNode);

        // Velocity Calculation:
        // Determine the square root of the standard gravitational parameter divided by the semi-latus rectum.
        double sqrtSgpOverSlr = Math.sqrt(Mu / (a*(1-e*e)) );

        // atan2 divides q/p to get the true anomaly but we are using a identity sin of arctan to get our results
        // double prearctanDiv = (Q/P);
        double prearctanDivSquareRoot = Math.sqrt(P*P+Q*Q);

        // reference https://space.stackexchange.com/questions/54596/how-to-calculate-velocity-vector-in-perifocal-coordinates
        // sin of atan2 = y/(y^2+x^2)
        double vP = -sqrtSgpOverSlr*(Q/prearctanDivSquareRoot);
        // cos of atan2 = x/(y^2+x^2)
        double vQ = sqrtSgpOverSlr*(e+P/prearctanDivSquareRoot);

        stateVectors[1] = perifocalToEquatorial(vP, vQ); //, this.ArgumentOfPeriapsis, this.Inclination, this.LongitudeOfAscendingNode);

        return stateVectors;
    }

//    private Vector3d perifocalToEquatorial(double P, double Q, double w, double i, double W) {
//        // rotate by argument of periapsis
//        double sinw = Math.sin(w);
//        double cosw = org.joml.Math.cosFromSin(sinw, w);
//        double x = cosw * P - sinw * Q;
//        double z = sinw * P + cosw * Q;
//
//        // rotate by inclination
//        double sinI = Math.sin(i);
//        double y = sinI * z;
//
//        z = org.joml.Math.cosFromSin(sinI, i) * z;
//
//        // rotate by longitude of ascending node
//        double xtemp = x;
//
//        double sinW = Math.sin(W);
//        double cosW = org.joml.Math.cosFromSin(sinW, W);
//
//        x = cosW * xtemp - sinW * z;
//        z = sinW * xtemp + cosW * z;
//
//        return new Vector3d(x, y, z);
//    }

    public Quaterniond getOrbitRotation() {
        return orbitRotation;
    }

    private void setOrbitRotationFromElements (double argumentOfPeriapsis, double inclination, double longitudeOfAscendingNode) {
        this.orbitRotation = new Quaterniond().rotateY(-longitudeOfAscendingNode).rotateX(-inclination).rotateY(-argumentOfPeriapsis);
        this.orbitRotation.normalize();
    }

    private Vector3d perifocalToEquatorial(double P, double Q) {
        Vector3d stateVec = new Vector3d(P, 0d, Q);
        return orbitRotation.transform(stateVec);
    }

    // reference: https://downloads.rene-schwarz.com/download/M002-Cartesian_State_Vectors_to_Keplerian_Orbit_Elements.pdf
    // https://space.stackexchange.com/questions/65465/orbit-determination-from-position-and-velocity
    public void fromCartesian(Vector3d position, Vector3d velocity, long TimeElapsed) {
        double VelMagnitude = velocity.length();
        double PosMagnitude = position.length();

        Vector3d momentumVectorH = new Vector3d(position).cross(velocity);
        Vector3d eccentricityVector = (new Vector3d(velocity).cross(momentumVectorH)).mul(1/Mu);
        eccentricityVector.sub(new Vector3d(position).normalize());

        Vector3d pointingAscendingNode = new Vector3d(momentumVectorH.z, 0, -momentumVectorH.x);

        double trueAnomalyAcosVar = eccentricityVector.dot(position)/(eccentricityVector.length()*PosMagnitude);

        double trueAnomoly = Math.acos(Mth.clamp(trueAnomalyAcosVar, -1, 1));
        trueAnomoly = position.dot(velocity) < 0 ? twoPI - trueAnomoly : trueAnomoly;

        this.Inclination = Math.acos(Mth.clamp(-momentumVectorH.y/momentumVectorH.length(), -1, 1));

        this.Eccentricity = eccentricityVector.length();

        this.LongitudeOfAscendingNode = Math.acos(Mth.clamp(pointingAscendingNode.x/pointingAscendingNode.length(), -1, 1));
        this.LongitudeOfAscendingNode = pointingAscendingNode.z < 0 ? twoPI - LongitudeOfAscendingNode : LongitudeOfAscendingNode;

        this.ArgumentOfPeriapsis = Math.acos(Mth.clamp(pointingAscendingNode.dot(eccentricityVector)/
                (pointingAscendingNode.length()*eccentricityVector.length()), -1, 1));

        //for equatorial orbits
        if (Double.isNaN(this.LongitudeOfAscendingNode) || Double.isNaN(this.ArgumentOfPeriapsis)) {
            this.LongitudeOfAscendingNode = 0;
            this.ArgumentOfPeriapsis = Math.atan2(eccentricityVector.z, eccentricityVector.x);
        }

        this.ArgumentOfPeriapsis = eccentricityVector.y < 0 ? twoPI - ArgumentOfPeriapsis : ArgumentOfPeriapsis;

        setOrbitRotationFromElements(this.ArgumentOfPeriapsis, this.Inclination, this.LongitudeOfAscendingNode);

        // vis viva equation
        this.SemiMajorAxis = 1 / ((2 / PosMagnitude) - (VelMagnitude * VelMagnitude) / Mu);

        if (Eccentricity < 1) {
            double E = 2 * Math.atan2(Math.tan(trueAnomoly * 0.5d), Math.sqrt((1 + Eccentricity) / (1 - Eccentricity)));

            this.MeanAngularMotion = Math.sqrt(Mu / (SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
            double timeDiffTerm = (E - Eccentricity * Math.sin(E)) / this.MeanAngularMotion;
            this.periapsisTime = TimeElapsed - Calcs.timeDoubleToLong(timeDiffTerm);
        } else {
            double cosTrueAnomoly = Math.cos(trueAnomoly);
            double H = invCosh((Eccentricity + cosTrueAnomoly) / (1 + Eccentricity * cosTrueAnomoly));

            this.MeanAngularMotion = Math.sqrt(Mu / -(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
            double timeDiffTerm = (Eccentricity * Math.sinh(H) - H) / this.MeanAngularMotion;
            this.periapsisTime = TimeElapsed - Calcs.timeDoubleToLong(timeDiffTerm);
        }
    }

    private double invCosh(double x) {
        if (x < 1.0) {
            return Double.NaN;
        }
        return Math.log(x + Math.sqrt(Math.pow(x, 2) - 1));
    }

    public void setOrbitalPeriod(double parentMass) {
        if (SemiMajorAxis >= 0) {
            Mu = UniversalGravitationalConstant * parentMass;
            //double orbitalPeriod = 2 * Math.PI * Math.sqrt(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis / Mu);
            this.MeanAngularMotion = Math.sqrt(Mu/(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
        } else {
            Mu = UniversalGravitationalConstant * parentMass;
            this.MeanAngularMotion = Math.sqrt(Mu/-(SemiMajorAxis * SemiMajorAxis * SemiMajorAxis));
        }
    }
}
