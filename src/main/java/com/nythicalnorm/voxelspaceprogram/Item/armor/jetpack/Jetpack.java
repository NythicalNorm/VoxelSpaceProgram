package com.nythicalnorm.voxelspaceprogram.Item.armor.jetpack;

import com.nythicalnorm.voxelspaceprogram.Item.armor.VSPArmorMaterial;
import com.nythicalnorm.voxelspaceprogram.Item.armor.SpacesuitModelModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class Jetpack extends ArmorItem {

    public Jetpack(Properties pProperties) {
        super(VSPArmorMaterial.SPACESUIT, Type.CHESTPLATE, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SpacesuitModelModifier());
    }
}
