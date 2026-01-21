package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle;

import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import com.nythicalnorm.voxelspaceprogram.util.SidedCallsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
    default int getMaxLocalRawBrightness(BlockPos pPos) throws Exception {
        Integer darkLevelFromPlanet = null;

        if (this instanceof Level level) {
            if (level.isClientSide) {
                Float result = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> SidedCallsUtil::getPlayerSunAngle).call();
                if (result != null) {
                    darkLevelFromPlanet = DayNightCycleHandler.getDarknessLightLevel(result, level);
                }
            }
            else {
                CelestialBodyAccessor planetAccessor = (CelestialBodyAccessor) level;
                if (planetAccessor.isPlanet()) {
                   darkLevelFromPlanet = DayNightCycleHandler.getDarknessLightLevel(pPos, level);
                }
            }
        }
        else if (this instanceof WorldGenRegion worldGenRegion) {
            Level level = worldGenRegion.getLevel();
            CelestialBodyAccessor planetAccessor = (CelestialBodyAccessor) level;
            if (planetAccessor.isPlanet()) {
                darkLevelFromPlanet = DayNightCycleHandler.getDarknessLightLevel(pPos, level);
            }
        }
        if (darkLevelFromPlanet != null) {
            return darkLevelFromPlanet;
        } else {
            return getMaxLocalRawBrightness(pPos, this.getSkyDarken());
        }
    }
}
