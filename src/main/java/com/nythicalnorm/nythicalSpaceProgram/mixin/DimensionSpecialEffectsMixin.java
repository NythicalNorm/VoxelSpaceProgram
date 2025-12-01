package com.nythicalnorm.nythicalSpaceProgram.mixin;


import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin (DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin {
    @Inject(method ="getSunriseColor", at = @At("HEAD"), cancellable = true)
    public void NSPgetSunriseColor(float pTimeOfDay, float pPartialTicks, CallbackInfoReturnable<float[]> cir) {
        NythicalSpaceProgram.getCelestialStateSupplier().ifPresent(celestialStateSupplier -> {
            if (celestialStateSupplier.doRender()) {
                float[] sunriseCol = new float[4];
                sunriseCol[0] = 0f;
                sunriseCol[1] = 0f;
                sunriseCol[2] = 0f;
                sunriseCol[3] = 0f;

                cir.setReturnValue(sunriseCol);
            }
        });
    }
}
