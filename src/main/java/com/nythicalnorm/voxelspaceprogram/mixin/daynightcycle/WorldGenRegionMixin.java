package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WorldGenRegion.class)
public class WorldGenRegionMixin {
    @Inject(method = "getCurrentDifficultyAt", at= @At(value = "RETURN"),cancellable = true)
    public void getCurrentDifficultyAt(BlockPos pPos, CallbackInfoReturnable<DifficultyInstance> cir) {
        WorldGenRegion worldGenRegion = (WorldGenRegion) (Object)this;
        Level level = worldGenRegion.getLevel();
        if (!level.isClientSide()) {
            PlanetaryBody plnt = ((PlanetAccessor)level).getPlanetaryBody();
            Optional<Long> currentTime = Optional.empty();
            if (plnt != null && SolarSystem.getInstance().isPresent()) {
                currentTime = DayNightCycleHandler.getDayTime(pPos, plnt, SolarSystem.getInstance().get().getCurrentTime());
            }
            if (currentTime.isPresent()) {
                cir.setReturnValue(new DifficultyInstance(level.getDifficulty(), level.getDayTime(), 0L, level.getMoonBrightness()));
            }
        }
    }
}
