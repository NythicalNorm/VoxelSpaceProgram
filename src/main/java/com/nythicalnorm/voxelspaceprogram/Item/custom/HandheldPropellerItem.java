package com.nythicalnorm.voxelspaceprogram.Item.custom;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.sound.VSPSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HandheldPropellerItem extends Item {
    public HandheldPropellerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        pLevel.playSeededSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                VSPSounds.HANDHELD_PROPELLER_START.get(), SoundSource.AMBIENT, 1f, 1f,0);
       return InteractionResultHolder.sidedSuccess(itemstack, false);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        if (pStack.getItem() == VSPItems.HANDHELD_PROPELLER.get()) {
            return 1000;
        }
        return super.getUseDuration(pStack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack pStack) {
        if (pStack.getItem() == VSPItems.HANDHELD_PROPELLER.get()) {
            return UseAnim.BOW;
        }
        else {
            return super.getUseAnimation(pStack);
        }
    }
}
