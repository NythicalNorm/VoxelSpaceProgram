package com.nythicalnorm.nythicalSpaceProgram.screen;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NythicalSpaceProgram.MODID);

    public static final RegistryObject<MenuType<MagnetizerMenu>> MAGNETIZER_MENU =
            registerMenuTypes("magnetizer_menu", MagnetizerMenu::new);

    public static final RegistryObject<MenuType<CryogenicAirSeparatorMenu>> CRYOGENIC_AIR_SEPARATOR_MENU =
            registerMenuTypes("cryogenic_air_separator_menu", CryogenicAirSeparatorMenu::new);


    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuTypes(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
