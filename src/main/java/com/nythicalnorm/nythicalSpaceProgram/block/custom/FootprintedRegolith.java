package com.nythicalnorm.nythicalSpaceProgram.block.custom;

import com.nythicalnorm.nythicalSpaceProgram.util.FootprintedType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class FootprintedRegolith extends Block {
    public static final EnumProperty<FootprintedType> FOOTPRINTTYPE = EnumProperty.<FootprintedType>create("footprinttype", FootprintedType.class);


    public FootprintedRegolith(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FOOTPRINTTYPE, FootprintedType.NOFOOTPRINTS));
    }

    @Override
    public void stepOn(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Entity pEntity) {
        if (!pLevel.isClientSide() && pEntity instanceof LivingEntity) {
            Enum<FootprintedType> currentState = pState.getValue(FOOTPRINTTYPE);
            if (currentState == FootprintedType.NOFOOTPRINTS) {

                float entityLookdir = pEntity.getRotationVector().y;
                boolean isRightFoot = false;
                isRightFoot = pPos.getX() % 2 == 0;
                if (pPos.getZ() % 2 == 0) {
                    isRightFoot = !isRightFoot;
                }

                if (entityLookdir > 120 || entityLookdir < -120 || (entityLookdir < 30 && entityLookdir > -30)) {
                    if (pEntity.isSprinting()) {
                        if (isRightFoot){
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.RIGHTBOOTZFACING));
                        }
                        else {
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.LEFTBOOTZFACING));
                        }
                    }
                    else {
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.TWOBOOTZFACING));
                    }
                }
                else {
                    if (pEntity.isSprinting()) {
                        if (isRightFoot){
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.RIGHTBOOTXFACING));
                        }
                        else {
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.LEFTBOOTXFACING));
                        }
                    }
                    else {
                        pLevel.setBlockAndUpdate(pPos, pState.setValue(FOOTPRINTTYPE, FootprintedType.TWOBOOTXFACING));
                    }
                }

            }
        }
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FOOTPRINTTYPE);
        super.createBlockStateDefinition(pBuilder);
    }
}
