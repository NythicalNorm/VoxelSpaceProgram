package com.nythicalnorm.nythicalSpaceProgram.Item;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.fluid.ModFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NythicalSpaceProgram.MODID);

    public static final RegistryObject<CreativeModeTab> Main_Mod_Tab = CREATIVE_MODE_TABS.register("nythical_space_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.AMETHYST_SHARD))
                    .title(Component.translatable("creativetab.Main_NSP"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.HANDHELD_PROPELLER.get());
                        output.accept(ModItems.MAGNET_BOOTS.get());
                        output.accept(ModItems.MAGNETIZED_IRON_INGOT.get());

                        output.accept(ModBlocks.OXYGEN_PROPELLANT_TANK.get());
                        output.accept(ModBlocks.MAGNETIZED_IRON_BLOCK.get());
                        output.accept(ModBlocks.LUNAR_REGOLITH.get());
                        output.accept(ModBlocks.CRYOGENIC_AIR_SEPARATOR.get());
                        output.accept(ModBlocks.MAGNETIZER.get());

                        output.accept(ModFluids.LIQUID_OXYGEN.fluidContainer.get());
                    })
                    .build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
