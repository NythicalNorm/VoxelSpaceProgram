package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.StrollThroughVillageGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(StrollThroughVillageGoal.class)
public class StrollThroughVillageGoalMixin {
    @Shadow
    @Final
    private PathfinderMob mob;

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        if (((CelestialBodyAccessor)mob.level()).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(mob.getX(), mob.getY(), mob.getZ(), mob.level());
            return isDay.orElse(original);
        }
        return original;
    }
}
