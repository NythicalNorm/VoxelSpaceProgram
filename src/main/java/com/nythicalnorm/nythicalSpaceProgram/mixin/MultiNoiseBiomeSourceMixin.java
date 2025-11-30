package com.nythicalnorm.nythicalSpaceProgram.mixin;

import com.mojang.serialization.Codec;
import com.nythicalnorm.nythicalSpaceProgram.planetdimgen.OverworldBiomeGen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {

//    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("TAIL"), cancellable = true)
//    public void getNoiseBiome(int pX, int pY, int pZ, Climate.Sampler pSampler, CallbackInfoReturnable<Holder<Biome>> cir) {
//        Optional<Holder<Biome>> bim = OverworldBiomeGen.getBiome();
//        if (bim.isPresent()) {
//            //HolderSet<Biome> ogVal = possibleBiomes();
//            cir.setReturnValue(bim.get());
//            // Continentalness nneds to change i think
//        }
//    }
}
