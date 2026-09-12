package com.nythicalnorm.voxelspaceprogram.datagen;

import com.google.common.collect.ImmutableList;
import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class VSPRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public VSPRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }
    public static final ImmutableList<ItemLike> ALUMINUM_SMELTABLES =
            ImmutableList.of(VSPBlocks.ALUMINIUM_ORE.get().asItem(),
                    VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get().asItem(),
                    VSPItems.RAW_ALUMINUM.get()
            );

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        oreSmelting(pWriter,
                ALUMINUM_SMELTABLES,
                RecipeCategory.MISC,
                VSPItems.ALUMINUM_INGOT.get(),
                0.7f, // Experience
                200,   // Cooking time in ticks (200 ticks = 10 seconds)
                "aluminum_ingot"
        );

        oreBlasting(pWriter,
                ALUMINUM_SMELTABLES,
                RecipeCategory.MISC,
                VSPItems.ALUMINUM_INGOT.get(),
                0.7f, // Experience
                100,   // Cooking time in ticks (200 ticks = 10 seconds)
                "aluminum_ingot"
        );

        nineBlockStorageRecipes(pWriter,
                RecipeCategory.MISC,
                VSPItems.ALUMINUM_INGOT.get(),
                RecipeCategory.BUILDING_BLOCKS,
                VSPBlocks.ALUMINIUM_BLOCK.get().asItem()
        );

        nineBlockStorageRecipes(pWriter,
                RecipeCategory.MISC,
                VSPItems.RAW_ALUMINUM.get(),
                RecipeCategory.BUILDING_BLOCKS,
                VSPBlocks.RAW_ALUMINIUM_BLOCK.get().asItem()
        );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, VSPBlocks.HYPERGOLIC_STEEL_TANK.get())
                .pattern("XRX")
                .pattern("X#X")
                .pattern("XRX")
                .define('#', Items.GLASS_PANE)
                .define('X', Items.IRON_INGOT)
                .define('R', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, VSPItems.HANDHELD_THRUSTER.get())
                .pattern("XRX")
                .pattern("R#R")
                .pattern("XRX")
                .define('#', Items.IRON_BLOCK)
                .define('X', Items.REDSTONE)
                .define('R', Items.IRON_BARS)
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, VSPBlocks.MAGNETIZED_IRON_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', VSPItems.MAGNETIZED_IRON_INGOT.get())
                .unlockedBy(getHasName(VSPItems.MAGNETIZED_IRON_INGOT.get()), has(VSPItems.MAGNETIZED_IRON_INGOT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, VSPItems.MAGNET_BOOTS.get())
                .pattern("X X")
                .pattern("X X")
                .define('X', VSPItems.MAGNETIZED_IRON_INGOT.get())
                .unlockedBy(getHasName(VSPItems.MAGNETIZED_IRON_INGOT.get()), has(VSPItems.MAGNETIZED_IRON_INGOT.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless (RecipeCategory.BUILDING_BLOCKS, VSPItems.MAGNETIZED_IRON_INGOT.get(), 9)
                .requires(VSPBlocks.MAGNETIZED_IRON_BLOCK.get(), 1)
                .unlockedBy(getHasName(VSPBlocks.MAGNETIZED_IRON_BLOCK.get()), has(VSPBlocks.MAGNETIZED_IRON_BLOCK.get()))
                .save(pWriter);
    }
}
