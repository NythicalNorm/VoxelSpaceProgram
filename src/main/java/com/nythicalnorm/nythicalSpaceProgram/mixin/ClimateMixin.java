package com.nythicalnorm.nythicalSpaceProgram.mixin;

import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.biome.Climate.quantizeCoord;

//@Mixin(Climate.class)
public class ClimateMixin {
//    @Inject(method = "target", at = @At("TAIL"), cancellable = true)
//    private static void target(float pTemperature, float pHumidity, float pContinentalness, float pErosion, float pDepth, float pWeirdness, CallbackInfoReturnable<Climate.TargetPoint> cir) {
//        pContinentalness = -0.8f;
//        pWeirdness = -0.7f;
//        cir.setReturnValue(new Climate.TargetPoint(Climate.quantizeCoord(pTemperature), Climate.quantizeCoord(pHumidity), Climate.quantizeCoord(pContinentalness), Climate.quantizeCoord(pErosion), Climate.quantizeCoord(pDepth), Climate.quantizeCoord(pWeirdness)));
//    }
}
