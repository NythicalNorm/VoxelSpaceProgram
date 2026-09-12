package com.nythicalnorm.voxelspaceprogram.fluid.containers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class CryogenicBucketItem extends BucketItem {
    public CryogenicBucketItem(Supplier<? extends Fluid> supplier, Item.Properties builder) {
        super(supplier, builder);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (player != null && !level.isClientSide()) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == stack.getItem() ||
                    player.getItemInHand(InteractionHand.OFF_HAND).getItem() == stack.getItem()) {
                player.hurt(level.damageSources().freeze(), 2);
            }
        }
    }
}
