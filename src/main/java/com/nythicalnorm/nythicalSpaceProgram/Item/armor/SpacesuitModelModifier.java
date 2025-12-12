package com.nythicalnorm.nythicalSpaceProgram.Item.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class SpacesuitModelModifier implements IClientItemExtensions {
    @Override
    public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        original.rightArm.x = -6.5f;
        original.leftArm.x = 6.5f;
        return IClientItemExtensions.super.getGenericArmorModel(livingEntity, itemStack, equipmentSlot, original);
    }
}
