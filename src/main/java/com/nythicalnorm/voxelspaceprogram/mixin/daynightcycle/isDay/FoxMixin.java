package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Fox.class)
public class FoxMixin {
    @ModifyExpressionValue(method = "getAmbientSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        Fox fox = (Fox) (Object) this;
        if (((CelestialBodyAccessor)fox.level()).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(fox.getX(), fox.getY(), fox.getZ(), fox.level());
            return isDay.orElse(original);
        }
        return original;
    }
}
