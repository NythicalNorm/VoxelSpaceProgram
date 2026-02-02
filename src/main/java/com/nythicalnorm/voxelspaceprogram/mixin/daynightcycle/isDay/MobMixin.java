package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle.isDay;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mob.class)
public class MobMixin {
    // Don't need to actually calculate since, the isSunBurnTick method again calculates the light level again after this check.
    // so calculating isDay is pointless now, though its a little more inefficient now
    @Redirect(method = "isSunBurnTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    public boolean isDay(Level instance) {
        return true;
    }
}
