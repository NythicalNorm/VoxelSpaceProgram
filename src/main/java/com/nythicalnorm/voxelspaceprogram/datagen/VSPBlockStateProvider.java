package com.nythicalnorm.voxelspaceprogram.datagen;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.VSPBlocks;
import com.nythicalnorm.voxelspaceprogram.fluid.VSPFluids;
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

public class VSPBlockStateProvider extends BlockStateProvider {
    public VSPBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VoxelSpaceProgram.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(VSPBlocks.ALUMINIUM_ORE.get(), cubeAll(VSPBlocks.ALUMINIUM_ORE.get()));
        simpleBlockWithItem(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get(), cubeAll(VSPBlocks.DEEPSLATE_ALUMINIUM_ORE.get()));
        simpleBlockWithItem(VSPBlocks.ALUMINIUM_BLOCK.get(), cubeAll(VSPBlocks.ALUMINIUM_BLOCK.get()));
        simpleBlockWithItem(VSPBlocks.RAW_ALUMINIUM_BLOCK.get(), cubeAll(VSPBlocks.RAW_ALUMINIUM_BLOCK.get()));

        simpleBlockWithItem(VSPBlocks.MAGNETIZED_IRON_BLOCK.get(), cubeAll(VSPBlocks.MAGNETIZED_IRON_BLOCK.get()));
        SetFootprintBlockState(VSPBlocks.LUNAR_REGOLITH);
        simpleBlockWithItem(VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get(), cubeAll(VSPBlocks.CRYOGENIC_AIR_SEPARATOR.get()));
        modelBlockWithItem(VSPBlocks.MAGNETIZER.get(), new ModelFile.UncheckedModelFile(VoxelSpaceProgram.rl( "block/magnetizer")));

        connectedBlock(VSPBlocks.HYPERGOLIC_STEEL_TANK);

        simpleBlockWithItem(VSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get(), cubeAll(VSPBlocks.VEHICLE_ASSEMBLY_PLATFORM.get()));
        simpleBlockWithItem(VSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD.get(), cubeAllCutout(VSPBlocks.VEHICLE_ASSEMBLY_SCAFFOLD));
        simpleBlockWithItem(VSPBlocks.VEHICLE_ASSEMBLER.get(), cubeAll(VSPBlocks.VEHICLE_ASSEMBLER.get()));

        fluidBlock(VSPFluids.LIQUID_OXYGEN.block);
        fluidBlock(VSPFluids.NITROGEN.block);
        connectedBlock(VSPBlocks.CRYOGENIC_AIR_SEPARATOR_PART);

        rocketryEntityBlock(VSPBlocks.THREE_KEROLOX, VSPBlocks.MAGNETIZED_IRON_BLOCK, 1f/4f);
        rocketryEntityBlock(VSPBlocks.TWO_KEROLOX, VSPBlocks.ALUMINIUM_BLOCK, 1f/3f);

        machineryEntityBlock(VSPBlocks.SHEET_METAL_ROLLER, VSPBlocks.ALUMINIUM_BLOCK,
                1f/2.5f,
                32,
                16,
                2.6f,
                0.0f
        );
        particleOnlyBlock(VSPBlocks.BOUNDING_BLOCK, VSPBlocks.MAGNETIZED_IRON_BLOCK);
    }

    protected void rocketryEntityBlock(RegistryObject<Block> block, RegistryObject<Block> particleBlock, float scale) {
        particleOnlyBlock(block, particleBlock);

        Vector3f rot = new Vector3f(-90f, 0f, 0f);
        Vector3f pos = new Vector3f(0f, 4f, 0f);
        float invScale = 1.0f / scale;

        itemModels().getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", blockTexture(particleBlock.get()))
                .transforms().transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .translation(pos.x, pos.y, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .translation(pos.x, pos.y, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                    .translation(pos.x, pos.y, pos.z - 0.25f)
                    .rotation(rot.x + 45f, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .translation(pos.x, pos.y, pos.z - 0.25f)
                    .rotation(rot.x + 45f, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.FIXED)
                    .translation(pos.x, pos.y, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.GROUND)
                    .translation(pos.x, pos.y + (invScale * 2.0f), pos.z)
                    .rotation(rot.x, rot.y, rot.z).scale(scale).end()
                .transform(ItemDisplayContext.GUI)
                    .translation(pos.x, pos.y, pos.z)
                    .rotation(rot.x + 22.5f, rot.y, rot.z + 45f)
                    .scale(scale).end();
    }

    protected void machineryEntityBlock(RegistryObject<Block> block,
                                        RegistryObject<Block> particleBlock,
                                        float scale,
                                        int xWidth,
                                        int zWidth,
                                        float guiX,
                                        float guiY) {
        particleOnlyBlock(block, particleBlock);
        float halfX = (float) xWidth * 0.25f * scale;
        Vector3f rot = new Vector3f(0f, -180f, 0f);
        Vector3f pos = new Vector3f(0f, 0f, 0.0f);

        itemModels().getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", blockTexture(particleBlock.get()))
                .transforms().transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .translation(pos.x - halfX, pos.y + 2.0f, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .translation(pos.x + halfX, pos.y + 2.0f, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                    .translation(pos.x - halfX, pos.y, pos.z)
                    .rotation(rot.x + 45f, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .translation(pos.x + halfX, pos.y, pos.z)
                    .rotation(rot.x + 45f, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.FIXED)
                    .translation(pos.x, pos.y, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.GROUND)
                    .translation(pos.x, pos.y + 4.0f, pos.z)
                    .rotation(rot.x, rot.y, rot.z)
                    .scale(scale).end()
                .transform(ItemDisplayContext.GUI)
                    .translation(pos.x + guiX, pos.y + guiY, 0.0f)
                    .rotation(rot.x + 22.5f, rot.y + 45.0f, rot.z)
                    .scale(scale).end();
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

    public ModelFile cubeAllCutout(RegistryObject<Block> block) {
        return models().cubeAll(block.getId().getPath(), blockTexture(block.get())).renderType("minecraft:cutout");
    }

    protected void SetFootprintBlockState (RegistryObject<Block> block) {
        getVariantBuilder(block.get()).forAllStates(state -> {
            FootprintedType footprintType = state.getValue(FOOTPRINTTYPE);
            ResourceLocation blockModel = VoxelSpaceProgram.rl( "block/lunar_regolith");

            if (footprintType == FootprintedType.NOFOOTPRINTS) {
               return ConfiguredModel.allYRotations(models().cubeAll("lunar_regolith", blockModel), 0, false);
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
        simpleBlock(block.get(), cubeAll(block.get()));
    }

    protected void connectedBlock(RegistryObject<Block> block) {
        simpleBlockWithItem(block.get(), models().getExistingFile(block.getKey().location()));
    }
}
