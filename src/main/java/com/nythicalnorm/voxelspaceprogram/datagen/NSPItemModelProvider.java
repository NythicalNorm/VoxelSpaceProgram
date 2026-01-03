package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.Item.NSPItems;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class NSPItemModelProvider extends ItemModelProvider {
    public NSPItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VoxelSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        item3dOnlyinHand(NSPItems.HANDHELD_PROPELLER);
        simpleItem(NSPItems.MAGNET_BOOTS);
        simpleItem(NSPItems.MAGNETIZED_IRON_INGOT);

        simpleItem(NSPItems.SPACESUIT_HELMET);
        simpleItem(NSPItems.CREATIVE_SPACESUIT_CHESTPLATE);
        simpleItem(NSPItems.SPACESUIT_LEGGINGS);
        simpleItem(NSPItems.SPACESUIT_BOOTS);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        assert item.getId() != null;
        return  withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                VoxelSpaceProgram.rl( "item/" + item.getId().getPath()));
    }

    private void item3dOnlyinHand(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath() + "_2d",
                ResourceLocation.parse("item/handheld")).texture("layer0",
                VoxelSpaceProgram.rl( "item/" + item.getId().getPath()));

//        ModelFile.ExistingModelFile val = getExistingFile(NythicalSpaceProgram.rl( "item/" + item.getId().getPath() + "_3d"));
//
//        withExistingParent(item.getId().getPath(), ResourceLocation.parse("item/handheld"))
//                .customLoader(SeparateTransformsModelBuilder::begin)
//                .base()
//                .perspective(ItemDisplayContext.GUI, getBuilder("item/" + item.getId().getPath() + "_2d"))
//                .perspective(ItemDisplayContext.GROUND, getBuilder("item/" + item.getId().getPath() + "_2d"))
//                .perspective(ItemDisplayContext.FIXED, getBuilder("item/" + item.getId().getPath() + "_2d"));
    }
}
