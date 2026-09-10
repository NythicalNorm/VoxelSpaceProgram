package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.Item.NSPItems;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class NSPRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public NSPRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NSPBlocks.HYPERGOLIC_STEEL_TANK.get())
                .pattern("XRX")
                .pattern("X#X")
                .pattern("XRX")
                .define('#', Items.GLASS_PANE)
                .define('X', Items.IRON_INGOT)
                .define('R', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, NSPItems.HANDHELD_PROPELLER.get())
                .pattern("XRX")
                .pattern("R#R")
                .pattern("XRX")
                .define('#', Items.IRON_BLOCK)
                .define('X', Items.REDSTONE)
                .define('R', Items.IRON_BARS)
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NSPBlocks.MAGNETIZED_IRON_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', NSPItems.MAGNETIZED_IRON_INGOT.get())
                .unlockedBy(getHasName(NSPItems.MAGNETIZED_IRON_INGOT.get()), has(NSPItems.MAGNETIZED_IRON_INGOT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NSPItems.MAGNET_BOOTS.get())
                .pattern("X X")
                .pattern("X X")
                .define('X', NSPItems.MAGNETIZED_IRON_INGOT.get())
                .unlockedBy(getHasName(NSPItems.MAGNETIZED_IRON_INGOT.get()), has(NSPItems.MAGNETIZED_IRON_INGOT.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless (RecipeCategory.BUILDING_BLOCKS, NSPItems.MAGNETIZED_IRON_INGOT.get(), 9)
                .requires(NSPBlocks.MAGNETIZED_IRON_BLOCK.get(), 1)
                .unlockedBy(getHasName(NSPBlocks.MAGNETIZED_IRON_BLOCK.get()), has(NSPBlocks.MAGNETIZED_IRON_BLOCK.get()))
                .save(pWriter);
    }
}
