package com.nythicalnorm.nythicalSpaceProgram.datagen;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.block.ModBlocks;
import com.nythicalnorm.nythicalSpaceProgram.fluid.ModFluids;
import com.nythicalnorm.nythicalSpaceProgram.util.FootprintedType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import static com.nythicalnorm.nythicalSpaceProgram.block.custom.FootprintedRegolith.FOOTPRINTTYPE;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NythicalSpaceProgram.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        simpleBlockWithItem(ModBlocks.OXYGEN_PROPELLANT_TANK.get(), getColumnCubeModel(ModBlocks.OXYGEN_PROPELLANT_TANK,
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/oxygen_propellant_tank_side"),
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/oxygen_propellant_tank_end")));

        simpleBlockWithItem(ModBlocks.MAGNETIZED_IRON_BLOCK.get(), cubeAll(ModBlocks.MAGNETIZED_IRON_BLOCK.get()));
        SetFootprintBlockState(ModBlocks.LUNAR_REGOLITH);
        simpleBlockWithItem(ModBlocks.CRYOGENIC_AIR_SEPARATOR.get(), cubeAll(ModBlocks.CRYOGENIC_AIR_SEPARATOR.get()));
        modelBlockWithItem(ModBlocks.MAGNETIZER.get(), new ModelFile.UncheckedModelFile(
                ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/magnetizer")));

        fluidBlock(ModFluids.LIQUID_OXYGEN.block);
    }

    protected void modelBlockWithItem(Block block, ModelFile model) {
        horizontalBlock(block, model);
        simpleBlockItem(block, model);
    }

    protected void SetFootprintBlockState (RegistryObject<Block> block) {
        getVariantBuilder(block.get()).forAllStates(state -> {
            FootprintedType myEnumval = state.getValue(FOOTPRINTTYPE);
            if (myEnumval == FootprintedType.NOFOOTPRINTS) {
               return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("lunar_regolith",
                       ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/lunar_regolith")))};
           }

           else { //if (state.getValue(FOOTPRINTTYPE) == FootprintedType.TWOBOOTZFACING) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeTop("lunar_regolith_" + state.getValue(FOOTPRINTTYPE),
                        ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/lunar_regolith"),
                        ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/lunar_regolith_" + state.getValue(FOOTPRINTTYPE))
                ))};
           }
        });

        simpleBlockItem(block.get(), cubeAll(block.get()));
    }

    protected ModelFile getColumnCubeModel(@NotNull RegistryObject<Block> block, ResourceLocation side, ResourceLocation end) {
        assert block.getId() != null;
        return models().withExistingParent(block.getId().getPath(), ModelProvider.BLOCK_FOLDER + "/cube_column_horizontal")
                .texture("side", side)
                .texture("end", end);
    }

    protected void fluidBlock(RegistryObject<LiquidBlock> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

    protected void genCryogenicAirSeparatorPartState (RegistryObject<Block> block) {
        getVariantBuilder(block.get()).forAllStates(state -> {
            FootprintedType myEnumval = state.getValue(FOOTPRINTTYPE);
            if (myEnumval == FootprintedType.NOFOOTPRINTS) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("lunar_regolith",
                        ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/lunar_regolith")))};
            }

            else { //if (state.getValue(FOOTPRINTTYPE) == FootprintedType.TWOBOOTZFACING) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeTop("lunar_regolith_" + state.getValue(FOOTPRINTTYPE),
                        ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/lunar_regolith"),
                        ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/lunar_regolith_" + state.getValue(FOOTPRINTTYPE))
                ))};
            }
        });

        simpleBlockItem(block.get(), cubeAll(block.get()));
    }
}
