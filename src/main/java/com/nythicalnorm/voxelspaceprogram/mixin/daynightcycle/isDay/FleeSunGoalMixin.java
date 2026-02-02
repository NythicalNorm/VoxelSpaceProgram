package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(FleeSunGoal.class)
public class FleeSunGoalMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    private double wantedX;

    @Shadow
    private double wantedY;

    @Shadow
    private double wantedZ;

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        if (((CelestialBodyAccessor)level).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(wantedX, wantedY, wantedZ, level);
            return isDay.orElse(original);
        }
        return original;
    }
}
