package com.nythicalnorm.nythicalSpaceProgram.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.CelestialStateSupplier;
import com.nythicalnorm.nythicalSpaceProgram.planetshine.PlanetShine;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private VertexBuffer skyBuffer;

    boolean doesMobEffectBlockSky(Camera pCamera) {
        return false;
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void NSPrenderSky(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo ci) {
        LevelRenderer levelRenderer = (LevelRenderer) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        //long beforeTimes = Util.getNanos();
        Optional<CelestialStateSupplier> css = NythicalSpaceProgram.getCelestialStateSupplier();

        if (mc.level == null || css.isEmpty()) {
            return;
        }
        if (css.get().doRender()) {
            pSkyFogSetup.run();
            if (!pIsFoggy) {
                FogType fogtype = pCamera.getFluidInCamera();
                if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA && !this.doesMobEffectBlockSky(pCamera)) {
                    PlanetShine.renderSkybox(mc, levelRenderer, pPoseStack, pProjectionMatrix, pPartialTick, pCamera, skyBuffer, css.get());
                }
            }
            ci.cancel();
        }
        //long diff = Util.getNanos() - beforeTimes;
        //NythicalSpaceProgram.log("PlanetShine Time: " + diff);
    }
}
