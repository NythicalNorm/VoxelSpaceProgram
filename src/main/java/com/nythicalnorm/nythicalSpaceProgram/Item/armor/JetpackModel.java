package com.nythicalnorm.nythicalSpaceProgram.Item.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class JetpackModel<T extends LivingEntity> extends HumanoidModel<T> {
    private static final float pYOffset = 0.0f;
    private static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0F);

    public JetpackModel(ModelPart pRoot) {
        super(pRoot);
    }

    public static LayerDefinition getSpacesuitLayer() {
        return LayerDefinition.create(JetpackModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32);
    }
    public static MeshDefinition createBodyLayer(CubeDeformation pCubDeformation) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, pCubDeformation), PartPose.offset(0.0F, 0.0F + pYOffset, 0.0F));
        return meshdefinition;
    }
}