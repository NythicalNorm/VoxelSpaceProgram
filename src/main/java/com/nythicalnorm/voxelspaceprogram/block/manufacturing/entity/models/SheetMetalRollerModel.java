package com.nythicalnorm.voxelspaceprogram.block.manufacturing.entity.models;

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
public class SheetMetalRollerModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(VoxelSpaceProgram.rl( "sheet_metal_roller_model"), "main");
    public static final Material MATERIAL_LOCATION = new Material(InventoryMenu.BLOCK_ATLAS, VoxelSpaceProgram.rl( "block/sheet_metal_roller"));
    private final ModelPart main;
    private final ModelPart machine;
    private final ModelPart piston;
    private final ModelPart wheel1;
    private final ModelPart wheel2;

    public SheetMetalRollerModel(ModelPart root) {
        this.main = root.getChild("main");
        this.machine = this.main.getChild("machine");
        this.piston = this.main.getChild("piston");
        this.wheel1 = this.main.getChild("wheel1");
        this.wheel2 = this.main.getChild("wheel2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition machine = main.addOrReplaceChild("machine", CubeListBuilder.create().texOffs(30, 78).addBox(4.0F, -20.0F, 2.0F, 2.0F, 12.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, -8.0F, 11.0F, 16.0F, 8.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(66, 0).addBox(-7.0F, -8.0F, -4.0F, 14.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(52, 51).addBox(-6.0F, -8.0F, 2.0F, 12.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(78, 94).addBox(-7.0F, -3.0F, 2.0F, 1.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(95, 48).addBox(6.0F, -3.0F, 2.0F, 1.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(64, 25).addBox(4.0F, -21.0F, 12.0F, 3.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-4.0F, -10.0F, -2.0F, 8.0F, 2.0F, 24.0F, new CubeDeformation(0.0F))
                .texOffs(0, 51).addBox(-7.0F, -24.0F, 12.0F, 14.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 66).addBox(-7.0F, -21.0F, 12.0F, 3.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(72, 68).addBox(-6.0F, -20.0F, 2.0F, 2.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition piston = main.addOrReplaceChild("piston", CubeListBuilder.create().texOffs(54, 78).addBox(-1.0F, -29.0F, 17.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(54, 94).addBox(-3.0F, -20.0F, 15.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition wheel1 = main.addOrReplaceChild("wheel1", CubeListBuilder.create().texOffs(66, 14).addBox(-8.0F, -2.5F, -2.5F, 16.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(94, 32).addBox(-8.0F, -1.5F, -3.5F, 16.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 36).addBox(-8.0F, -1.5F, 2.5F, 16.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 90).addBox(-8.0F, 2.5F, -1.5F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(92, 90).addBox(-8.0F, -3.5F, -1.5F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.5F, 6.5F, -0.8727F, 0.0F, 0.0F));

        PartDefinition wheel2 = main.addOrReplaceChild("wheel2", CubeListBuilder.create().texOffs(30, 68).addBox(-8.0F, -2.5F, -2.5F, 16.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(94, 40).addBox(-8.0F, -1.5F, -3.5F, 16.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 44).addBox(-8.0F, -1.5F, 2.5F, 16.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 24).addBox(-8.0F, 2.5F, -1.5F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(94, 28).addBox(-8.0F, -3.5F, -1.5F, 16.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.5F, 6.5F, -0.7418F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public ModelPart getMain() {
        return main;
    }
    public ModelPart getMachine() {
        return machine;
    }
    public ModelPart getPiston() {
        return piston;
    }
    public ModelPart getWheel1() {
        return wheel1;
    }
    public ModelPart getWheel2() {
        return wheel2;
    }
}
