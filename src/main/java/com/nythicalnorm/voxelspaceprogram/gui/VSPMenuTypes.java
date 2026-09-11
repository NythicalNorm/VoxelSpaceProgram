package com.nythicalnorm.voxelspaceprogram.gui;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.gse.screen.VehicleAssemblerMenu;
import com.nythicalnorm.voxelspaceprogram.block.gse.screen.VehicleAssemblerScreen;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.screen.CryogenicAirSeparatorMenu;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.screen.CryogenicAirSeparatorScreen;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.screen.MagnetizerMenu;
import com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.screen.MagnetizerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VSPMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VoxelSpaceProgram.MODID);

    public static final RegistryObject<MenuType<MagnetizerMenu>> MAGNETIZER_MENU =
            registerMenuTypes("magnetizer_menu", MagnetizerMenu::new);

    public static final RegistryObject<MenuType<CryogenicAirSeparatorMenu>> CRYOGENIC_AIR_SEPARATOR_MENU =
            registerMenuTypes("cryogenic_air_separator_menu", CryogenicAirSeparatorMenu::new);

    public static final RegistryObject<MenuType<VehicleAssemblerMenu>> VEHICLE_ASSEMBLER_MENU =
            registerMenuTypes("vehicle_assembler_menu", VehicleAssemblerMenu::new);


    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuTypes(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static void registerMenus() {
        MenuScreens.register(VSPMenuTypes.MAGNETIZER_MENU.get(), MagnetizerScreen::new);
        MenuScreens.register(VSPMenuTypes.CRYOGENIC_AIR_SEPARATOR_MENU.get(), CryogenicAirSeparatorScreen::new);
        MenuScreens.register(VSPMenuTypes.VEHICLE_ASSEMBLER_MENU.get(), VehicleAssemblerScreen::new);
    }
}
