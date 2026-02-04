package com.nythicalnorm.voxelspaceprogram.mixin.spaceentites;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.spacecraft.player.PlayerOrbitAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntitySpaceMixin {
    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.91F))
    public float changeFrictionMultiplier(float constant) {
        LivingEntity livingEntity = ((LivingEntity)(Object) this);
        float frictionVal = constant;

        if (livingEntity.level() != null && livingEntity.level().dimension().equals(SpaceDimension.SPACE_LEVEL_KEY) && !livingEntity.onGround()) {
            frictionVal = 1.0F;
            if (livingEntity instanceof Player player && player.getAbilities().flying) {
                frictionVal = constant;
            }
        }
        return frictionVal;
    }

//    @WrapOperation(
//            method = "travel",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
//    public void playerMoveCheck(LivingEntity instance, MoverType moverType, Vec3 vec3, Operation<Void> original) {
//        if (instance instanceof Player player && ((PlayerOrbitAccessor)player).getOrbit().isHostOfItsSpace()) {
//            return;
//        } else {
//            original.call(instance, moverType, vec3);
//        }
//    }
}
