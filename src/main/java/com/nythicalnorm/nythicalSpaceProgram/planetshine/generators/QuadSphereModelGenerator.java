package com.nythicalnorm.nythicalSpaceProgram.planetshine.generators;

import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.nythicalSpaceProgram.util.Calcs;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class QuadSphereModelGenerator {
    private static final Vector2f[] textureboundingboxStart = new Vector2f[]{
            new Vector2f(0f,1f/3f),
            new Vector2f(1f/3f,1f/3f),
            new Vector2f(2f/3f,1f/3f),
            new Vector2f(0f,2f/3f),
            new Vector2f(1f,1f/3f),
            new Vector2f(2f/3f,1f/3f),

    };

    private static final Vector2f[] textureboundingboxEnd = new Vector2f[]{
            new Vector2f(1f/3f,0f/3f),
            new Vector2f(2f/3f,0f/3f),
            new Vector2f(1f,0f/3f),
            new Vector2f(1f/3f,1f/3f),
            new Vector2f(2f/3f,2f/3f),
            new Vector2f(1f/3f,2f/3f),
    };

    private static final float radius = 0.5f;
    private static final Vector3d modelOffset = new Vector3d(0.0,0.0,0.0);
    private static final VertexBuffer sphereLod0Buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

    public static void setupModels() {
        PoseStack spherePose = new PoseStack();
        spherePose.setIdentity();
        List<BakedQuad> planetquads = getsphereQuads();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for(BakedQuad bakedquad : planetquads) {
            bufferbuilder.putBulkData(spherePose.last(), bakedquad, 1f, 1f, 1f, 10, 10);
        }
        
        sphereLod0Buffer.bind();
        sphereLod0Buffer.upload(bufferbuilder.end());
        VertexBuffer.unbind();
    }

    public static VertexBuffer getSphereBuffer() {
        return sphereLod0Buffer;
    }

    public static List<BakedQuad> getsphereQuads() {
        List<BakedQuad> quads = new ArrayList<>();
        int QuadsPerSide = 32;

        int MaxPerSide = QuadsPerSide / 2;

        for (int squareSide = 0; squareSide < 6; squareSide++) {
            for (int sidesUpIter = -MaxPerSide; sidesUpIter < MaxPerSide; sidesUpIter++) {
                for (int sidesRightIter = -MaxPerSide; sidesRightIter < MaxPerSide; sidesRightIter++) {
                    BakedQuad quad0 = quad(Calcs.getQuadSquarePos(sidesUpIter, sidesRightIter, MaxPerSide, squareSide, radius),
                            Calcs.getQuadSquarePos(sidesUpIter, sidesRightIter + 1, MaxPerSide, squareSide, radius),
                            Calcs.getQuadSquarePos(sidesUpIter + 1, sidesRightIter + 1, MaxPerSide, squareSide, radius),
                            Calcs.getQuadSquarePos(sidesUpIter + 1, sidesRightIter, MaxPerSide, squareSide, radius),
                            sidesUpIter, sidesRightIter, QuadsPerSide, squareSide);
                    quads.add(quad0);
                }
            }
        }
        return quads;
    }



    public static BakedQuad quad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4,
                                 float sidesUpIter, float sidesRightIter, float QuadsPerSide, int squareSide) {
        BakedQuad[] quad = new BakedQuad[1];
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(q -> quad[0] = q);

        float usize = textureboundingboxEnd[squareSide].x - textureboundingboxStart[squareSide].x;
        float vsize = textureboundingboxEnd[squareSide].y - textureboundingboxStart[squareSide].y;
        sidesUpIter =  sidesUpIter + QuadsPerSide/2;
        sidesRightIter = sidesRightIter + QuadsPerSide/2;

        putVertex(builder, v1.x, v1.y, v1.z, (sidesRightIter/QuadsPerSide), (sidesUpIter/QuadsPerSide), usize, vsize, squareSide);
        putVertex(builder, v2.x, v2.y, v2.z, ((sidesRightIter + 1)/QuadsPerSide), (sidesUpIter/QuadsPerSide), usize, vsize, squareSide);
        putVertex(builder, v3.x, v3.y, v3.z, ((sidesRightIter + 1)/QuadsPerSide), ((sidesUpIter + 1)/QuadsPerSide), usize, vsize, squareSide);
        putVertex(builder, v4.x, v4.y, v4.z, (sidesRightIter/QuadsPerSide), ((sidesUpIter + 1)/QuadsPerSide), usize, vsize, squareSide);
        return quad[0];
    }

    private static void putVertex(VertexConsumer builder,
                                  double x, double y, double z, float u1, float v1, //,float u2, float v2,
                                  float usize, float vsize, int squareSide) {
        float iu = textureboundingboxStart[squareSide].x + (usize*u1);
        float iv = textureboundingboxStart[squareSide].y + (vsize*v1);
        builder.vertex(x + modelOffset.x, y + modelOffset.y, z + modelOffset.z)
                .uv(iu, iv)
                .endVertex();
    }
}
