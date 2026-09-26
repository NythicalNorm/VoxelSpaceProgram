package com.nythicalnorm.voxelspaceprogram.rendering.plumes;

import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.EngineEntity;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.VSPClientStage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ClientPlumeHandler {
    public static void addEngineBlockPlume(EngineEntity engineEntity, BlockPos blockPos) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ((VSPClientStage)Minecraft.getInstance()).vsp$getPlumeManager().addEngineBlockPlume(
                        engineEntity, blockPos
        ));
    }

    public static void removeEngineBlockPlume(BlockPos blockPos) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ((VSPClientStage)Minecraft.getInstance()).vsp$getPlumeManager().removeEngineBlockPlume(blockPos)
        );
    }
}
