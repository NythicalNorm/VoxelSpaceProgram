package com.nythicalnorm.nythicalSpaceProgram.datagen;

import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.OXYGEN_PROPELLANT_TANK.get())
                .pattern("XRX")
                .pattern("X#X")
                .pattern("XRX")
                .define('#', Items.GLASS_PANE)
                .define('X', Items.IRON_INGOT)
                .define('R', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.HANDHELD_PROPELLER.get())
                .pattern("XRX")
                .pattern("R#R")
                .pattern("XRX")
                .define('#', Items.IRON_BLOCK)
                .define('X', Items.REDSTONE)
                .define('R', Items.IRON_BARS)
                .unlockedBy(getHasName(Items.IRON_BLOCK), has(Items.IRON_BLOCK))
                .save(pWriter);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAGNETIZED_IRON_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.MAGNETIZED_IRON_INGOT.get())
                .unlockedBy(getHasName(ModItems.MAGNETIZED_IRON_INGOT.get()), has(ModItems.MAGNETIZED_IRON_INGOT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.MAGNET_BOOTS.get())
                .pattern("X X")
                .pattern("X X")
                .define('X', ModItems.MAGNETIZED_IRON_INGOT.get())
                .unlockedBy(getHasName(ModItems.MAGNETIZED_IRON_INGOT.get()), has(ModItems.MAGNETIZED_IRON_INGOT.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless (RecipeCategory.BUILDING_BLOCKS, ModItems.MAGNETIZED_IRON_INGOT.get(), 9)
                .requires(ModBlocks.MAGNETIZED_IRON_BLOCK.get(), 1)
                .unlockedBy(getHasName(ModBlocks.MAGNETIZED_IRON_BLOCK.get()), has(ModBlocks.MAGNETIZED_IRON_BLOCK.get()))
                .save(pWriter);
    }
}
