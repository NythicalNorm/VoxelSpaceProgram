package com.nythicalnorm.nythicalSpaceProgram.datagen;
import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NythicalSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        item3dOnlyinHand(ModItems.HANDHELD_PROPELLER);
        simpleItem(ModItems.MAGNET_BOOTS);
        simpleItem(ModItems.MAGNETIZED_IRON_INGOT);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        assert item.getId() != null;
        return  withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "item/" + item.getId().getPath()));
    }

    private void item3dOnlyinHand(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath() + "_2d",
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "item/" + item.getId().getPath()));

//        ModelFile.ExistingModelFile val = getExistingFile(ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "item/" + item.getId().getPath() + "_3d"));
//
//        withExistingParent(item.getId().getPath(), ResourceLocation.parse("item/handheld"))
//                .customLoader(SeparateTransformsModelBuilder::begin)
//                .base()
//                .perspective(ItemDisplayContext.GUI, getBuilder("item/" + item.getId().getPath() + "_2d"))
//                .perspective(ItemDisplayContext.GROUND, getBuilder("item/" + item.getId().getPath() + "_2d"))
//                .perspective(ItemDisplayContext.FIXED, getBuilder("item/" + item.getId().getPath() + "_2d"));
    }
}
