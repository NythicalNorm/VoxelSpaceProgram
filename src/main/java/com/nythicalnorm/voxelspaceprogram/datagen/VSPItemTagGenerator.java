package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class VSPItemTagGenerator extends ItemTagsProvider {
    public VSPItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, VoxelSpaceProgram.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(forgeTag("ingots/aluminum")).add(VSPItems.ALUMINUM_INGOT.get());
        tag(forgeTag("raw_materials/aluminum")).add(VSPItems.RAW_ALUMINUM.get());
        tag(forgeTag("storage_blocks/aluminum")).add(VSPBlocks.ALUMINIUM_BLOCK.get().asItem());
        tag(forgeTag("storage_blocks/raw_aluminum")).add(VSPBlocks.RAW_ALUMINIUM_BLOCK.get().asItem());
        tag(forgeTag("ores/aluminum")).add(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get().asItem(), VSPBlocks.ALUMINIUM_ORE.get().asItem());

        //different spelling
        tag(forgeTag("ingots/aluminium")).add(VSPItems.ALUMINUM_INGOT.get());
        tag(forgeTag("raw_materials/aluminium")).add(VSPItems.RAW_ALUMINUM.get());
        tag(forgeTag("storage_blocks/aluminium")).add(VSPBlocks.ALUMINIUM_BLOCK.get().asItem());
        tag(forgeTag("storage_blocks/raw_aluminium")).add(VSPBlocks.RAW_ALUMINIUM_BLOCK.get().asItem());
        tag(forgeTag("ores/aluminium")).add(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get().asItem(), VSPBlocks.ALUMINIUM_ORE.get().asItem());
    }

    protected TagKey<Item> forgeTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }
}
