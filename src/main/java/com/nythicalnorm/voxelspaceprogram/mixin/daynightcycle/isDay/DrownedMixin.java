package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Drowned.class)
public class DrownedMixin {
    @ModifyExpressionValue(method = "okTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        Drowned drowned = (Drowned) (Object) this;
        if (((CelestialBodyAccessor) drowned.level()).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(drowned.getX(), drowned.getY(), drowned.getZ(), drowned.level());
            return isDay.orElse(original);
        }
        return original;
    }
}
