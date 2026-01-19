package com.nythicalnorm.voxelspaceprogram.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.voxelspaceprogram.CelestialStateSupplier;
import com.nythicalnorm.voxelspaceprogram.planetshine.PlanetShine;
import com.nythicalnorm.voxelspaceprogram.util.Calcs;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private VertexBuffer skyBuffer;

    @Shadow
    private boolean doesMobEffectBlockSky(Camera pCamera) {
        return false;
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void NSPrenderSky(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo ci) {
        LevelRenderer levelRenderer = (LevelRenderer) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        //long beforeTimes = Util.getNanos();
        Optional<CelestialStateSupplier> css = CelestialStateSupplier.getInstance();

        if (mc.level == null || css.isEmpty()) {
            return;
        }
        if (css.get().doRender()) {
            pSkyFogSetup.run();
            if (!pIsFoggy) {
                FogType fogtype = pCamera.getFluidInCamera();
                if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(pCamera)) {
                    PlanetShine.renderSkybox(mc, levelRenderer, pPoseStack, pPartialTick, pCamera, skyBuffer, css.get());
                }
            }
            ci.cancel();
        }
        //long diff = Util.getNanos() - beforeTimes;
        //VoxelSpaceProgram.log("PlanetShine Time: " + diff);
    }

    @ModifyVariable(method = "renderClouds", at = @At("LOAD"), ordinal = 0)
    private double changeCloudSpeed(double value) {
        Optional<CelestialStateSupplier> css = CelestialStateSupplier.getInstance();
        if (!css.isEmpty()) {
            if (css.get().doRender()) {
                return Calcs.TimePerMilliTickToTick(css.get().getCurrentTime()) * 0.03F;
            }
        }

        return value;
    }
}
