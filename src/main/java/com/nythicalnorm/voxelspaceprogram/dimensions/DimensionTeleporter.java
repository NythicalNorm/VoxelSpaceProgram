package com.nythicalnorm.voxelspaceprogram.dimensions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

    @Override
    public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
        return false;
    }

    @Override
    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(newPos, Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }
}