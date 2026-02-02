package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.entity.animal.Fox$SleepGoal")
public class FoxSleepGoalMixin {
    @Shadow
    @Final
    Fox this$0; // Shadow the field

    @ModifyExpressionValue(method = "canSleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        if (((CelestialBodyAccessor) this$0.level()).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(this$0.getX(), this$0.getY(), this$0.getZ(), this$0.level());
            return isDay.orElse(original);
        }
        return original;
    }
}