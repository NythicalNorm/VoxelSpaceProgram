package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.Item.NSPItems;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.fluid.NSPFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class NSPCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VoxelSpaceProgram.MODID);

    public static final RegistryObject<CreativeModeTab> Main_Mod_Tab = CREATIVE_MODE_TABS.register("voxel_space_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(NSPItems.SPACESUIT_HELMET.get()))
                    .title(Component.translatable("creativetab.Main_VSP"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(NSPItems.HANDHELD_PROPELLER.get());
                        output.accept(NSPItems.MAGNET_BOOTS.get());
                        output.accept(NSPItems.MAGNETIZED_IRON_INGOT.get());

                        output.accept(NSPItems.SPACESUIT_HELMET.get());
                        output.accept(NSPItems.CREATIVE_SPACESUIT_CHESTPLATE.get());
                        output.accept(NSPItems.SPACESUIT_LEGGINGS.get());
                        output.accept(NSPItems.SPACESUIT_BOOTS.get());

                        output.accept(NSPBlocks.MAGNETIZED_IRON_BLOCK.get());

                        output.accept(NSPItems.RAW_ALUMINUM.get());
                        output.accept(NSPItems.ALUMINUM_INGOT.get());
                        output.accept(NSPBlocks.ALUMINIUM_ORE.get());
                        output.accept(NSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get());
                        output.accept(NSPBlocks.ALUMINIUM_BLOCK.get());
                        output.accept(NSPBlocks.RAW_ALUMINIUM_BLOCK.get());

                        output.accept(NSPBlocks.LUNAR_REGOLITH.get());
                        output.accept(NSPBlocks.CRYOGENIC_AIR_SEPARATOR.get());
                        output.accept(NSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get());
                        output.accept(NSPBlocks.MAGNETIZER.get());

                        output.accept(NSPBlocks.HYPERGOLIC_STEEL_TANK.get());

                        output.accept(NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get());
                        output.accept(NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get());
                        output.accept(NSPBlocks.VEHICLE_ASSEMBLER.get());

                        output.accept(NSPFluids.LIQUID_OXYGEN.fluidContainer.get());

                        output.accept(NSPBlocks.THREE_KEROLOX.get());
                        output.accept(NSPBlocks.TWO_KEROLOX.get());
                    })
                    .build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
