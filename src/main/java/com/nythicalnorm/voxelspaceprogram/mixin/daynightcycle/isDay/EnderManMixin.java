package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(EnderMan.class)
public class EnderManMixin {
    @ModifyExpressionValue(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(boolean original) {
        EnderMan enderMan = (EnderMan) (Object) this;
        if (((CelestialBodyAccessor)enderMan.level()).isPlanet()) {
            Optional<Boolean> isDay = DayNightCycleHandler.isDay(enderMan.getX(), enderMan.getY(), enderMan.getZ(), enderMan.level());
            return isDay.orElse(original);
        }
        return original;
    }
}
