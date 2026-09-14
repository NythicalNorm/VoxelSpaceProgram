package com.nythicalnorm.voxelspaceprogram.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.VSPMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @WrapOperation(method = "handleBlockBreakAction",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;canReach(Lnet/minecraft/core/BlockPos;D)Z"))
    public boolean extendCanReach(ServerPlayer instance, BlockPos blockPos, double padding, Operation<Boolean> original) {
        if (instance.level().getBlockState(blockPos).getBlock() instanceof VSPMultiblock vspMultiblock) {
            padding = padding + (vspMultiblock.getMaxBlockSize() * 0.5d);
        }
        return original.call(instance, blockPos, padding);
    }
}
