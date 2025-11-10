package com.nythicalnorm.nythicalSpaceProgram.Item;

import com.nythicalnorm.nythicalSpaceProgram.Item.custom.HandheldPropellerItem;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NythicalSpaceProgram.MODID);

    public static final RegistryObject<Item> HANDHELD_PROPELLER = ITEMS.register("handheld_propeller",
            () -> new HandheldPropellerItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MAGNET_BOOTS = ITEMS.register("magnet_boots", () ->
            new ModArmorItem(ModArmorMaterial.MAGNETIC, ArmorItem.Type.BOOTS,  new Item.Properties()));

    public static final RegistryObject<Item> MAGNETIZED_IRON_INGOT = ITEMS.register("magnetized_iron_ingot", () ->
            new Item(new Item.Properties().fireResistant()));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
