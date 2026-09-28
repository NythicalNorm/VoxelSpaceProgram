package com.nythicalnorm.voxelspaceprogram.entities;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CommandSeatEntityDummyRenderer extends EntityRenderer<CommandSeatEntity> {
    public CommandSeatEntityDummyRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public boolean shouldRender(CommandSeatEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(CommandSeatEntity pEntity) {
        return null;
    }
}
