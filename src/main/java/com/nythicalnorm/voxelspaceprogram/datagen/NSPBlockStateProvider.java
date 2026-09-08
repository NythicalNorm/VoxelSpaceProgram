package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.NSPBlocks;
import com.nythicalnorm.voxelspaceprogram.fluid.NSPFluids;
import com.nythicalnorm.voxelspaceprogram.block.terrain.luna.FootprintedType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.nythicalnorm.voxelspaceprogram.block.terrain.luna.FootprintedRegolith.FOOTPRINTTYPE;

public class NSPBlockStateProvider extends BlockStateProvider {
    public NSPBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VoxelSpaceProgram.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        simpleBlockWithItem(NSPBlocks.OXYGEN_PROPELLANT_TANK.get(), getColumnCubeModel(NSPBlocks.OXYGEN_PROPELLANT_TANK,
                VoxelSpaceProgram.rl( "block/oxygen_propellant_tank_side"),
                VoxelSpaceProgram.rl( "block/oxygen_propellant_tank_end")));

        simpleBlockWithItem(NSPBlocks.MAGNETIZED_IRON_BLOCK.get(), cubeAll(NSPBlocks.MAGNETIZED_IRON_BLOCK.get()));
        SetFootprintBlockState(NSPBlocks.LUNAR_REGOLITH);
        simpleBlockWithItem(NSPBlocks.CRYOGENIC_AIR_SEPARATOR.get(), cubeAll(NSPBlocks.CRYOGENIC_AIR_SEPARATOR.get()));
        modelBlockWithItem(NSPBlocks.MAGNETIZER.get(), new ModelFile.UncheckedModelFile(
                VoxelSpaceProgram.rl( "block/magnetizer")));

        simpleBlockWithItem(NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(), cubeAll(NSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get()));
        simpleBlockWithItem(NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get(), cubeAll(NSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get()));
        simpleBlockWithItem(NSPBlocks.VEHICLE_ASSEMBLER.get(), cubeAll(NSPBlocks.VEHICLE_ASSEMBLER.get()));

        fluidBlock(NSPFluids.LIQUID_OXYGEN.block);
        connectedBlock(NSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART);

        rocketryEntityBlock(NSPBlocks.THREE_KEROLOX, NSPBlocks.MAGNETIZED_IRON_BLOCK, 1f/4f, 4f);
        rocketryEntityBlock(NSPBlocks.TWO_KEROLOX, NSPBlocks.MAGNETIZED_IRON_BLOCK, 1f/3f, 8f);
        particleOnlyBlock(NSPBlocks.BOUNDING_BLOCK, NSPBlocks.MAGNETIZED_IRON_BLOCK);
    }

    protected void rocketryEntityBlock(RegistryObject<Block> block, RegistryObject<Block> particleBlock, float scale, float yHeight) {
        particleOnlyBlock(block, particleBlock);

        Vector3f rot = new Vector3f(-90f, 0f, 0f);
        Vector3f pos = new Vector3f(0f, yHeight, 0f);
        itemModels().getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", blockTexture(particleBlock.get()))
                .transforms().transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).scale(scale).translation(pos.x, pos.y, pos.z).rotation(rot.x, rot.y, rot.z).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).translation(pos.x, pos.y, pos.z).rotation(rot.x, rot.y, rot.z).scale(scale).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).translation(pos.x, pos.y, pos.z - 0.25f).rotation(rot.x + 45f, rot.y, rot.z).scale(scale).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).translation(pos.x, pos.y, pos.z - 0.25f).rotation(rot.x + 45f, rot.y, rot.z).scale(scale).end()
                .transform(ItemDisplayContext.FIXED).translation(pos.x, pos.y, pos.z).rotation(rot.x, rot.y, rot.z).scale(scale).end()
                .transform(ItemDisplayContext.GROUND).translation(pos.x, pos.y + 8f, pos.z).rotation(rot.x, rot.y, rot.z).scale(scale).end()
                .transform(ItemDisplayContext.GUI).translation(pos.x, pos.y, pos.z).rotation(rot.x + 22.5f, rot.y, rot.z + 45f).scale(scale).end();
    }

    protected void particleOnlyBlock(RegistryObject<Block> block, RegistryObject<Block> particleBlock) {
        models().getBuilder(block.getId().getPath()).texture("particle", blockTexture(particleBlock.get()));
        getVariantBuilder(block.get()).forAllStates(blockState ->
                ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(block.getKey().location())).build() // Creates the array of configured models
        );
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
                       VoxelSpaceProgram.rl( "block/lunar_regolith")))};
           }

           else {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeTop("lunar_regolith_" + state.getValue(FOOTPRINTTYPE),
                        VoxelSpaceProgram.rl( "block/lunar_regolith"),
                        VoxelSpaceProgram.rl( "block/lunar_regolith_" + state.getValue(FOOTPRINTTYPE))
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

    protected void connectedBlock(RegistryObject<Block> block) {
        simpleBlockWithItem(block.get(), models().getExistingFile(block.getKey().location()));
    }
}
