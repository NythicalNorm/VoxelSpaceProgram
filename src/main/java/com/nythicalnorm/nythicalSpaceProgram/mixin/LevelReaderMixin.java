package com.nythicalnorm.nythicalSpaceProgram.mixin;


import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetDimensions;
import com.nythicalnorm.nythicalSpaceProgram.util.DayNightCycleHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LevelReader.class)
public interface LevelReaderMixin extends BlockAndTintGetter, CollisionGetter, SignalGetter, BiomeManager.NoiseBiomeSource {
    @Shadow
    int getMaxLocalRawBrightness(BlockPos pPos, int brightness);

    @Shadow
    int getSkyDarken();

    /**
     * @author NythicalNorm
     * @reason injecting into default interface method doesn't seem to work so once again overwriting this method to give
     * the correct time for a timezone.
     */
    @Overwrite
    default int getMaxLocalRawBrightness(BlockPos pPos) {
        int DarkenAmount = this.getSkyDarken();
        Optional<Integer> darkLevelFromPlanet = Optional.empty();

        if (this instanceof Level || this instanceof WorldGenRegion) {
            Level level = null;

            if (this instanceof Level) {
                level = (Level) this;
            }
            else if (this instanceof WorldGenRegion) {
                WorldGenRegion worldGenLevel = (WorldGenRegion) this;
                // TO DO add a capabilit to the level storing its PlanetAddressStack
                level = PlanetDimensions.getDimensionLevel(worldGenLevel.dimensionType());
            }
            if (level != null) {
                // need to use pLevel.getNearestPlayer to get the sun angle of the nearest player, so this code would be faster in theory.
                if (!level.isClientSide()) {
                    darkLevelFromPlanet = DayNightCycleHandler.getDarknessLightLevel(pPos, level);
                } else if (NythicalSpaceProgram.getCelestialStateSupplier().isPresent()) {
                    darkLevelFromPlanet  = DayNightCycleHandler.getDarknessLightLevel(Optional.of(NythicalSpaceProgram.getCelestialStateSupplier().get().getPlayerData().getSunAngle()), level);
                }
            }
        }
        else {
            NythicalSpaceProgram.log("Where");
        }
        if (darkLevelFromPlanet.isPresent()) {
            DarkenAmount = darkLevelFromPlanet.get();
        }
        return getMaxLocalRawBrightness(pPos, DarkenAmount);
    }
}
