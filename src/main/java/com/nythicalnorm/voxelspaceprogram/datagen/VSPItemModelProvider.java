package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class VSPItemModelProvider extends ItemModelProvider {
    public VSPItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VoxelSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(VSPItems.RAW_ALUMINUM);
        simpleItem(VSPItems.ALUMINUM_INGOT);

        //item3dOnlyinHand(VSPItems.HANDHELD_THRUSTER);
        simpleItem(VSPItems.MAGNET_BOOTS);
        simpleItem(VSPItems.MAGNETIZED_IRON_INGOT);

        simpleItem(VSPItems.SPACESUIT_HELMET);
        simpleItem(VSPItems.CREATIVE_SPACESUIT_CHESTPLATE);
        simpleItem(VSPItems.SPACESUIT_LEGGINGS);
        simpleItem(VSPItems.SPACESUIT_BOOTS);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        assert item.getId() != null;
        return  withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                VoxelSpaceProgram.rl( "item/" + item.getId().getPath()));
    }

    private void item3dOnlyinHand(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                VoxelSpaceProgram.rl( "item/" + item.getId().getPath())
        );
    }
}
