package com.nythicalnorm.nythicalSpaceProgram.Item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class Jetpack extends ArmorItem implements IClientItemExtensions {

    public Jetpack(Properties pProperties) {
        super(ModArmorMaterial.SPACESUIT, Type.CHESTPLATE, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SpacesuitModelModifier());
    }

    //    @Override
//    public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
//        original.body.skipDraw = true;
//        return IClientItemExtensions.super.getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, null);
//    }
}
