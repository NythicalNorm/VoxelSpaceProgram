package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class EngineModel {
    private final ModelPart gimbaling;
    private final ModelPart fixed;
    private final Material material;

    public EngineModel(BlockEntityRendererProvider.Context context, ModelLayerLocation layerLocation, ResourceLocation textureLocation) {
        ModelPart modelpart = context.bakeLayer(layerLocation);
        this.fixed = modelpart.getChild("fixed");
        this.gimbaling = this.fixed.getChild("gimbaling");
        this.material = new Material(InventoryMenu.BLOCK_ATLAS, textureLocation);
    }

    public ModelPart getGimbaling() {
        return gimbaling;
    }

    public ModelPart getFixed() {
        return fixed;
    }

    public Material getMaterial() {
        return material;
    }
}
