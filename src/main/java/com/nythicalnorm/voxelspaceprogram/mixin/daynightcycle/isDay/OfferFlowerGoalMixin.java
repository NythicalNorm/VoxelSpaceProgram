package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(OfferFlowerGoal.class)
public class OfferFlowerGoalMixin {
    @Final
    @Shadow
    private IronGolem golem;

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        if (((CelestialBodyAccessor)golem.level()).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(golem.getX(), golem.getY(), golem.getZ(), golem.level());
            return isDay.orElse(original);
        }
        return original;
    }
}
