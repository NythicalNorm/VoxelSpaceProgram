package com.nythicalnorm.voxelspaceprogram.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class VSPNotCondition implements net.minecraft.client.renderer.block.model.multipart.Condition {
    public static final String TOKEN = "VSP_NOT";
    private final net.minecraft.client.renderer.block.model.multipart.Condition condition;

    public VSPNotCondition(net.minecraft.client.renderer.block.model.multipart.Condition condition) {
        this.condition = condition;
    }

    @Override
    public @NotNull Predicate<BlockState> getPredicate(@NotNull StateDefinition<Block, BlockState> stateDefinition) {
        return this.condition.getPredicate(stateDefinition).negate();
    }
}
