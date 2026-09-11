package com.nythicalnorm.voxelspaceprogram.recipe;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSPRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VoxelSpaceProgram.MODID);

    public static final RegistryObject<RecipeSerializer<MagnetizerRecipe>> MAGNETIZER_SERIALIZER =
            SERIALIZERS.register("magnetizer", () -> MagnetizerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
