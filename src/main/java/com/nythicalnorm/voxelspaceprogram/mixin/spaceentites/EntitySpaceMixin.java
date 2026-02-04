package com.nythicalnorm.voxelspaceprogram.mixin.spaceentites;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntitySpaceMixin {
    @Shadow
    private Level level;

    @ModifyReturnValue(method = "isNoGravity", at = @At(value = "TAIL"))
    public boolean isNoGravity(boolean original) {
        if (level.dimension().equals(SpaceDimension.SPACE_LEVEL_KEY)) {
            return true;
        } else {
            return original;
        }
    }
}
