package com.nythicalnorm.nythicalSpaceProgram.block.entity.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagnetizerModels {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "magnetizer_models"), "main");
    public static final Material TEXTURE_LOCATION = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(NythicalSpaceProgram.MODID, "block/magnetizer_rotating_table"));
    private final ModelPart magnet_table;
    private final ModelPart magnet;

    public MagnetizerModels(ModelPart root) {
        this.magnet_table = root.getChild("magnet_table");
        this.magnet = this.magnet_table.getChild("magnet");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition magnet_table = partdefinition.addOrReplaceChild("magnet_table", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 7).addBox(-3.0F, -1.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 7).addBox(2.0F, -1.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube5_r1 = magnet_table.addOrReplaceChild("cube5_r1", CubeListBuilder.create().texOffs(12, 16).addBox(-1.0F, 1.4197F, 1.876F, 2.0F, 1.4142F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube4_r1 = magnet_table.addOrReplaceChild("cube4_r1", CubeListBuilder.create().texOffs(7, 8).addBox(-0.8536F, -2.8724F, -2.7895F, 2.0F, 1.0F, 1.4142F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1464F, 0.0444F, 0.0036F, 0.7854F, 0.0F, 0.0F));

        PartDefinition arm2_r1 = magnet_table.addOrReplaceChild("arm2_r1", CubeListBuilder.create().texOffs(6, 12).addBox(6.2421F, -3.0854F, -1.0035F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1464F, 0.0444F, 0.0036F, 0.0F, 0.0F, -0.7854F));

        PartDefinition arm1_r1 = magnet_table.addOrReplaceChild("arm1_r1", CubeListBuilder.create().texOffs(0, 12).addBox(0.9006F, -7.2563F, -1.0035F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1464F, 0.0444F, 0.0036F, 0.0F, 0.0F, 0.7854F));

        PartDefinition magnet = magnet_table.addOrReplaceChild("magnet", CubeListBuilder.create().texOffs(14, 3).addBox(-1.45F, -7.25F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(-4.45F, -7.25F, -2.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(9, 19).addBox(-4.45F, -7.25F, 1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.3F, 0.0F, 0.0F));

        PartDefinition cube8_r1 = magnet.addOrReplaceChild("cube8_r1", CubeListBuilder.create().texOffs(17, 8).addBox(-1.0635F, -0.476F, 1.0593F, 1.4142F, 1.001F, 1.4142F, new CubeDeformation(0.0F))
                .texOffs(17, 8).addBox(-2.4757F, -0.476F, -0.3429F, 1.4142F, 1.001F, 1.4142F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0536F, -6.775F, 0.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public void renderRotatingTable(PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, boolean magnetAnim) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0f,-1.5f,0f);
        VertexConsumer vertexconsumer = TEXTURE_LOCATION.buffer(pBuffer, RenderType::entitySolid);
        //this.magnet_table.yRot = (float) (2*Math.PI/magnetAnim);
        if (magnetAnim) {
            this.magnet.zRot = -0.785398f;
            this.magnet.x = 7.1f;
            this.magnet.y = -2.25f;
        }
        else {
            this.magnet.zRot = 0f;
            this.magnet.x = 2.3f;
            this.magnet.y = 0f;
        }
        this.magnet_table.render(poseStack, vertexconsumer, pPackedLight, pPackedOverlay);
        poseStack.popPose();
    }
}
