package com.nythicalnorm.voxelspaceprogram.planettexgen;

import java.awt.*;
import java.util.Map;

public class GradientSupplier {
    public static PlanetGradient STAR_GRADIENT = new PlanetGradient(new BiomeGroup[]{
            new BiomeGroup("orangeHot", -0.5f, 0.5f, new BiomeGradient[]{
                        new BiomeGradient(0,0.5f, 0, 1, 1f, Color.decode("#fbba39")),
                    new BiomeGradient(0.5f,0.6f, 0, 1, 1f, Color.decode("#fbd955")),
                    new BiomeGradient(0.6f,0.72f, 0, 1, 1f, Color.decode("#ffffa8")),
                    new BiomeGradient(0.70f,1f, 0, 1, 1f, Color.decode("#fffffe")),
            }),
    });

    public static PlanetGradient OVERWORLD_GRADIENT = new PlanetGradient(new BiomeGroup[]{
            new BiomeGroup("Ocean", -0.6f, 0.1f, new BiomeGradient[]{
                    new BiomeGradient(0,0.85f, 0, 1, 1f, Color.decode("#3938C9")),
                    new BiomeGradient(0.85f,1f, 0, 1, 1f, Color.decode("#3D57D6")),
            }),
            new BiomeGroup("Land", 0.1f, 0.5f, new BiomeGradient[]{
                    new BiomeGradient(0f,0.4f, 0, 1f, 1f, Color.decode("#71A74D")),
                    new BiomeGradient(0.4f,0.75f, 0, 1f, 1f, Color.decode("#737373")),
                    new BiomeGradient(0.75f,1f, 0.78f, 1, 0.5f, Color.decode("#ffffff")),
            }),
    });

    public static PlanetGradient NILA_GRADIENT = new PlanetGradient(new BiomeGroup[]{
            new BiomeGroup("Mare", -0.5f, -0.2f, new BiomeGradient[]{
                    new BiomeGradient(0f,0.5f, 0, 1, 1f, Color.decode("#5d5d5d")),
                    new BiomeGradient(0.5f,0.90f, 0, 1, 1f, Color.decode("#3c3c3c")),
                    new BiomeGradient(0.90f,1f, 0, 1, 1f, Color.decode("#595959")),
            }),

            new BiomeGroup("Land", -0.2f, 0.5f, new BiomeGradient[]{
                    new BiomeGradient(0f,0.15f, 0, 1f, 1f, Color.decode("#959293")),
                    new BiomeGradient(0.15f,0.3f, 0, 1f, 1f, Color.decode("#c3c1c2")),
                    new BiomeGradient(0.3f,1f, 0, 1f, 1f, Color.decode("#d0d0d0")),
            }),
    });

    public static final Map<String, PlanetGradient> textureForPlanets = Map.of("earth", OVERWORLD_GRADIENT,"luna", NILA_GRADIENT, "sun", STAR_GRADIENT);
}
