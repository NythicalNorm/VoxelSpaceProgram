package com.nythicalnorm.nythicalSpaceProgram.planetshine.generators;

import org.joml.Vector3f;

public class SkyboxCubeGen {
    private static final float size = 1.0f;

    // Define the 8 unique corner points of the cube (for a unit cube centered at origin)
    private static final Vector3f p0 = new Vector3f(-size / 2, -size / 2, -size / 2);
    private static final Vector3f p1 = new Vector3f(size / 2, -size / 2, -size / 2);
    private static final Vector3f p2 = new Vector3f(size / 2, size / 2, -size / 2);
    private static final Vector3f p3 = new Vector3f(-size / 2, size / 2, -size / 2);
    private static final Vector3f p4 = new Vector3f(-size / 2, -size / 2, size / 2);
    private static final Vector3f p5 = new Vector3f(size / 2, -size / 2, size / 2);
    private static final Vector3f p6 = new Vector3f(size / 2, size / 2, size / 2);
    private static final Vector3f p7 = new Vector3f(-size / 2, size / 2, size / 2);

    public static Vector3f[] getCubeVertexes() {
        Vector3f[] vertices = new Vector3f[24]; // 6 faces * 4 vertices per face
        // Front face (z = -size / 2)
        vertices[0] = p0; // Bottom-left
        vertices[1] = p1; // Bottom-right
        vertices[2] = p2; // Top-right
        vertices[3] = p3; // Top-left

        // Back face (z = size / 2)
        vertices[4] = p7; // Bottom-left
        vertices[5] = p6; // Bottom-right
        vertices[6] = p5; // Top-right
        vertices[7] = p4; // Top-left

        // Left face (x = -size / 2)
        vertices[8] = p0; // Bottom-left
        vertices[9] = p3; // Top-left
        vertices[10] = p7; // Top-right
        vertices[11] = p4; // Bottom-right

        // Right face (x = size / 2)
        vertices[12] = p1; // Bottom-left
        vertices[13] = p5; // Bottom-right
        vertices[14] = p6; // Top-right
        vertices[15] = p2; // Top-left

        // Bottom face (y = -size / 2)
        vertices[16] = p0; // Bottom-left
        vertices[17] = p4; // Bottom-right
        vertices[18] = p5; // Top-right
        vertices[19] = p1; // Top-left

        // Top face (y = size / 2)
        vertices[20] = p3; // Bottom-left
        vertices[21] = p2; // Bottom-right
        vertices[22] = p6; // Top-right
        vertices[23] = p7; // Top-left
        return vertices;
    }
}
