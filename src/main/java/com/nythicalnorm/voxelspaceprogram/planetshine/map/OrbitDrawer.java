package com.nythicalnorm.voxelspaceprogram.planetshine.map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.Orbit;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.spacecraft.EntitySpacecraftBody;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class OrbitDrawer {
    private static VertexBuffer circleBuffer;
    private static VertexBuffer hyperbolaBuffer;

    public static void generateCircle(int segments) {
        circleBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < segments; i++) {
            float angleAround = (float) i/(segments);
            float angleAroundNext = (i+1f)/(segments);

            angleAround = angleAround * Mth.TWO_PI;
            angleAroundNext = angleAroundNext * Mth.TWO_PI;

            bufferbuilder.vertex(Math.cos(angleAround), 0f, Math.sin(angleAround)).color(1.0f,1.0f,1.0f,1.0f).endVertex();
            bufferbuilder.vertex(Math.cos(angleAroundNext), 0f, Math.sin(angleAroundNext)).color(1.0f,1.0f,1.0f,1.0f).endVertex();
        }

        circleBuffer.bind();
        circleBuffer.upload(bufferbuilder.end());
        VertexBuffer.unbind();
    }

    public static void generateHyperbola(int segments) {
        hyperbolaBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        // start the buffer with negative z infinity line - causes visual issues with float max at 10^38
//        Vector3f startLineMax = new Vector3f((float) (1f/Math.cos( Mth.HALF_PI)), 0f,(float) Math.tan( Mth.HALF_PI));
//        Vector3f startLineInfinity = new Vector3f(Float.MAX_VALUE, 0f, Float.MAX_VALUE);
//        bufferbuilder.vertex(startLineInfinity.x, startLineInfinity.y, startLineInfinity.z).color(1.0f,1.0f,1.0f,1.0f).endVertex();
//        bufferbuilder.vertex(startLineMax.x, startLineMax.y, startLineMax.z).color(1.0f,1.0f,1.0f,1.0f).endVertex();

        for (int i = 0; i < segments; i++) {
            float angleAround = (float) i/(segments);
            float angleAroundNext = (i+1f)/(segments);

            angleAround = (angleAround * Mth.PI) + Mth.HALF_PI;
            angleAroundNext = (angleAroundNext * Mth.PI) + Mth.HALF_PI;
            Vector3f startLine = new Vector3f((float) (1f/Math.cos(angleAround)), 0f,(float) Math.tan(angleAround));
            Vector3f endLine = new Vector3f((float)  (1f/Math.cos(angleAroundNext)), 0f,(float) Math.tan(angleAroundNext));

            if (startLine.isFinite() && endLine.isFinite() && startLine.x < 0 && endLine.x < 0) {
                bufferbuilder.vertex(startLine.x, startLine.y, startLine.z).color(1.0f,1.0f,1.0f,1.0f).endVertex();
                bufferbuilder.vertex(endLine.x, endLine.y, endLine.z).color(1.0f,1.0f,1.0f,1.0f).endVertex();
            }
        }

        hyperbolaBuffer.bind();
        hyperbolaBuffer.upload(bufferbuilder.end());
        VertexBuffer.unbind();
    }

    public static void drawOrbit(Orbit orbitalBody, float scaleFactor, PoseStack poseStack, Matrix4f projectionMatrix) {
        OrbitalElements orbitalElements = orbitalBody.getOrbitalElements();
        boolean isElliptical = (orbitalElements.Eccentricity >= 0 && orbitalElements.Eccentricity < 1);

        VertexBuffer drawBuffer = isElliptical ? circleBuffer : hyperbolaBuffer;

        double a = isElliptical ? orbitalElements.SemiMajorAxis : -orbitalElements.SemiMajorAxis;
        double b = isElliptical ? a*Math.sqrt(1-(orbitalElements.Eccentricity*orbitalElements.Eccentricity))
                : a*Math.sqrt((orbitalElements.Eccentricity*orbitalElements.Eccentricity) - 1);

        a = a*scaleFactor;
        b = b*scaleFactor;
        float distanceFromCenterToFoci =  isElliptical ? (float) Math.sqrt(a*a - b*b) : (float) -Math.sqrt(a*a + b*b);

        poseStack.pushPose();
//        Quaternionf orbitRotations = new Quaternionf();
//        orbitRotations.rotateY((float) -orbitalElements.LongitudeOfAscendingNode);
//        orbitRotations.rotateX((float) -orbitalElements.Inclination);
//        orbitRotations.rotateY((float) -orbitalElements.ArgumentOfPeriapsis);
        poseStack.mulPose(Calcs.quaternionDtoF(orbitalElements.getOrbitRotation()));

        poseStack.translate(-distanceFromCenterToFoci, 0f, 0f);
        poseStack.scale((float) a,1f,(float) b);

        if (orbitalBody instanceof EntitySpacecraftBody) {
            RenderSystem.setShaderColor(0.0f,0.0f,1.0f,1.0f);
        } else {
            RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        }

        drawBuffer.bind();
        drawBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionColorShader());
        VertexBuffer.unbind();
        poseStack.popPose();
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
    }
}
