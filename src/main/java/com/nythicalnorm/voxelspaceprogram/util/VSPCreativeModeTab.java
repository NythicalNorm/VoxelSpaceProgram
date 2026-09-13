package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.fluid.VSPFluids;
import com.nythicalnorm.voxelspaceprogram.fluid.containers.HandheldThrusterFluidHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VSPCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VoxelSpaceProgram.MODID);

    public static final RegistryObject<CreativeModeTab> Main_Mod_Tab = CREATIVE_MODE_TABS.register("voxel_space_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(VSPItems.SPACESUIT_HELMET.get()))
                    .title(Component.translatable("creativetab.Main_VSP"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(VSPItems.HANDHELD_THRUSTER.get());
                        output.accept(HandheldThrusterFluidHandler.getFullFilledItemStack(VSPItems.HANDHELD_THRUSTER.get()),
                                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                        );

                        output.accept(VSPItems.MAGNET_BOOTS.get());
                        output.accept(VSPItems.MAGNETIZED_IRON_INGOT.get());

                        output.accept(VSPItems.SPACESUIT_HELMET.get());
                        output.accept(VSPItems.CREATIVE_SPACESUIT_CHESTPLATE.get());
                        output.accept(VSPItems.SPACESUIT_LEGGINGS.get());
                        output.accept(VSPItems.SPACESUIT_BOOTS.get());

                        output.accept(VSPBlocks.MAGNETIZED_IRON_BLOCK.get());

                        output.accept(VSPItems.RAW_ALUMINUM.get());
                        output.accept(VSPItems.ALUMINUM_INGOT.get());
                        output.accept(VSPBlocks.ALUMINIUM_ORE.get());
                        output.accept(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get());
                        output.accept(VSPBlocks.ALUMINIUM_BLOCK.get());
                        output.accept(VSPBlocks.RAW_ALUMINIUM_BLOCK.get());

                        output.accept(VSPBlocks.LUNAR_REGOLITH.get());

                        output.accept(VSPBlocks.SHEET_METAL_ROLLER.get());
                        output.accept(VSPBlocks.MAGNETIZER.get());
                        output.accept(VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get());
                        output.accept(VSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART.get());

                        output.accept(VSPBlocks.HYPERGOLIC_STEEL_TANK.get());

                        output.accept(VSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get());
                        output.accept(VSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get());
                        output.accept(VSPBlocks.VEHICLE_ASSEMBLER.get());

                        output.accept(VSPFluids.LIQUID_OXYGEN.fluidContainer.get());

                        output.accept(VSPBlocks.THREE_KEROLOX.get());
                        output.accept(VSPBlocks.TWO_KEROLOX.get());
                    })
                    .build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
