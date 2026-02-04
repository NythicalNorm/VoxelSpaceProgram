package com.nythicalnorm.voxelspaceprogram.mixin.spaceentites;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public class ItemEntitySpaceMixin {
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.98F))
    public float changeFrictionMultiplier(float constant) {
        ItemEntity itemEntity = ((ItemEntity)(Object) this);

        if (itemEntity.level() != null && itemEntity.level().dimension().equals(SpaceDimension.SPACE_LEVEL_KEY)) {
            return 1.0F;
        } else {
            return constant;
        }
    }
}
