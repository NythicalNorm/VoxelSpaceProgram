package com.nythicalnorm.voxelspaceprogram.mixin.spaceentites;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractArrow.class)
public class AbstractArrowSpaceMixin {
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.99F))
    public float changeFrictionMultiplier(float constant) {
        AbstractArrow abstractArrow = ((AbstractArrow)(Object) this);

        if (abstractArrow.level() != null && abstractArrow.level().dimension().equals(SpaceDimension.SPACE_LEVEL_KEY)) {
            return 1.0F;
        } else {
            return constant;
        }
    }
}
