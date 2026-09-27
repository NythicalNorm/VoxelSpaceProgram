package com.nythicalnorm.voxelspaceprogram.block.rocket_parts.entity.models;

import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandSeatModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(VoxelSpaceProgram.rl("command_seat"),"main");
    public static final Material MATERIAL_LOCATION = new Material(InventoryMenu.BLOCK_ATLAS, VoxelSpaceProgram.rl( "block/command_seat"));

    private final ModelPart main;
    private final ModelPart podium;
    private final ModelPart chair;

    public CommandSeatModel(ModelPart root) {
        this.main = root.getChild("main");
        this.podium = this.main.getChild("podium");
        this.chair = this.main.getChild("chair");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition podium = main.addOrReplaceChild("podium", CubeListBuilder.create().texOffs(38, 62).addBox(-5.0F, -14.0F, 0.0F, 10.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(38, 51).addBox(-7.0F, -16.0F, 0.0F, 14.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(84, 53).addBox(3.0F, -17.0F, 6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(74, 84).addBox(-1.0F, -26.0F, 8.0F, 2.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 34).addBox(-7.0F, -30.0F, 7.0F, 14.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 44).addBox(3.0F, -17.0F, 0.0F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(84, 56).addBox(5.0F, -22.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(80, 44).addBox(5.0F, -23.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 15.0F));

        PartDefinition cube_r1 = podium.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(84, 44).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -15.05F, 4.0F, 0.48F, 0.0F, 0.0F));

        PartDefinition chair = main.addOrReplaceChild("chair", CubeListBuilder.create().texOffs(0, 34).addBox(-8.0F, 2.0F, -7.0F, 16.0F, 2.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 72).addBox(-5.0F, 4.0F, -4.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 51).addBox(-8.0F, -16.0F, -7.0F, 16.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(74, 72).addBox(-8.0F, 1.0F, -4.0F, 2.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(62, 82).addBox(-8.0F, -15.0F, -4.0F, 2.0F, 16.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 82).addBox(6.0F, 1.0F, -4.0F, 2.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(68, 82).addBox(6.0F, -15.0F, -4.0F, 2.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, -1.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public ModelPart getMain() {
        return main;
    }
}
