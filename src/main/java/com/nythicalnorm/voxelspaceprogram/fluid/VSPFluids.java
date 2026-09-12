package com.nythicalnorm.voxelspaceprogram.fluid;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class VSPFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, VoxelSpaceProgram.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, VoxelSpaceProgram.MODID);

    public static final FluidRegistryContainer LIQUID_OXYGEN = new FluidRegistryContainer("liquid_oxygen_fluid",
            FluidType.Properties.create()
                    .canDrown(true)
                    .canSwim(false)
                    .canPushEntity(true)
                    .canExtinguish(true)
                    .density(1_141)
                    .temperature(54)
            ,
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(VoxelSpaceProgram.MODID, "liquid_oxygen_fluid")
                    .fogColor(0f,0f,1f)
                    .tint(0xFF0091c1)
                    .flowing("cryogenic_fluid")
                    .still("cryogenic_fluid")
                    .overlay("cryogenic_fluid")),
            BlockBehaviour.Properties.copy(Blocks.WATER), new Item.Properties(), true, false);

    public static final FluidRegistryContainer NITROGEN = new FluidRegistryContainer("nitrogen_fluid",
            FluidType.Properties.create()
                    .canDrown(true)
                    .canSwim(false)
                    .canPushEntity(true)
                    .canExtinguish(true)
                    .density(2)
            ,
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(VoxelSpaceProgram.MODID, "nitrogen_fluid")
                            .fogColor(0f,0f,1f)
                            .tint(0x80F5FFED)
                            .flowing("cryogenic_fluid")
                            .still("cryogenic_fluid")
                            .overlay("cryogenic_fluid")),
            BlockBehaviour.Properties.copy(Blocks.WATER), new Item.Properties(), true, true);
}
