package com.nythicalnorm.voxelspaceprogram.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.BoundingBlock;
import com.nythicalnorm.voxelspaceprogram.block.multiblock.BoundingBlockEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.VSPClientStage;
import com.nythicalnorm.voxelspaceprogram.rendering.MultiBlockPreviewRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin implements VSPClientStage {
    @Unique
    MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer = new MultiBlockPreviewRenderer();
    @Unique

    @Override
    public MultiBlockPreviewRenderer vsp$multiBlockPreviewRenderer() {
        return this.vsp$multiBlockPreviewRenderer;
    }

    @Shadow
    @Nullable
    public ClientLevel level;

    @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"))
    public BlockPos changeBlockPosToMainMultiblock(BlockHitResult instance, Operation<BlockPos> original) {
        BlockPos blockPos = original.call(instance);
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof BoundingBlock) {
            if (this.level.getBlockEntity(blockPos) instanceof BoundingBlockEntity boundingBlockEntity && boundingBlockEntity.hasReceivedCoords()) {
                blockPos = boundingBlockEntity.getMainPos();
            }
        }
        return blockPos;
    }

    @WrapOperation(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getBlockPos()Lnet/minecraft/core/BlockPos;"))
    public BlockPos changeBlockPosToMainMultiblock2(BlockHitResult instance, Operation<BlockPos> original) {
        BlockPos blockPos = original.call(instance);
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof BoundingBlock) {
            if (this.level.getBlockEntity(blockPos) instanceof BoundingBlockEntity boundingBlockEntity && boundingBlockEntity.hasReceivedCoords()) {
                blockPos = boundingBlockEntity.getMainPos();
            }
        }
        return blockPos;
    }
}
