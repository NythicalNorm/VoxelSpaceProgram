package com.nythicalnorm.nythicalSpaceProgram.dimensions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DimensionTeleporter implements ITeleporter {
    private Vec3 newPos = Vec3.ZERO;

    public DimensionTeleporter(Vec3 pos) {
        this.newPos = pos;
    }

//    @Override
//    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destinationWorld,
//                              float yaw, Function<Boolean, Entity> repositionEntity) {
//
//        entity = repositionEntity.apply(false);
//
//        BlockPos destinationPos = newPos;
//
//        entity.setPos(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ());
//
//        return entity;
//    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(newPos, Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }
}