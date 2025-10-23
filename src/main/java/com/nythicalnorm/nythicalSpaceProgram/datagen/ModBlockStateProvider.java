package com.nythicalnorm.nythicalSpaceProgram.datagen;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NythicalSpaceProgram.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        simpleBlockWithItem(ModBlocks.OXYGEN_PROPELLANT_TANK.get(), getColumnCubeModel(ModBlocks.OXYGEN_PROPELLANT_TANK,
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/oxygen_propellant_tank_side"),
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/oxygen_propellant_tank_end")));
    }

    protected ModelFile getColumnCubeModel(RegistryObject<Block> block, ResourceLocation side, ResourceLocation end) {
        assert block.getId() != null;
        return models().withExistingParent(block.getId().getPath(), ModelProvider.BLOCK_FOLDER + "/cube_column_horizontal")
                .texture("side", side)
                .texture("end", end);
    }
}
