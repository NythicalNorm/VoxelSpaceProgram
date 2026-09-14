package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.Item.MultiBlockItem;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.block.rocket_parts.rendering.VSPClientStage;
import com.nythicalnorm.voxelspaceprogram.rendering.MultiBlockPreviewRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    @SubscribeEvent
    public static void postPlayerRender(RenderPlayerEvent.Pre event) {
        PlayerModel<AbstractClientPlayer> playerModel = event.getRenderer().getModel();

        if (event.getEntity().getSlot(102).get().is(VSPItems.CREATIVE_SPACESUIT_CHESTPLATE.get())) {
            playerModel.leftArm.visible = false;
            playerModel.rightArm.visible = false;
            playerModel.leftSleeve.visible = false;
            playerModel.rightSleeve.visible = false;
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)
            return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null)
            return;

        ItemStack stack = mc.player.getMainHandItem();

        if ((stack.getItem() instanceof MultiBlockItem multiBlockItem) && multiBlockItem.renderBlockPreview()) {
            MultiBlockPreviewRenderer previewRenderer = ((VSPClientStage)mc).vsp$multiBlockPreviewRenderer();

            previewRenderer.renderBlockItemPreview(event.getPoseStack(), mc, multiBlockItem, multiBlockItem.getMultiblock());
        }
    }

    @SubscribeEvent
    public static void OnKeyInput (InputEvent.Key event) {
//        if (PSKeyBinds.USE_PLAYER_JETPACK_KEY.consumeClick()) {
//            LocalPlayer player = Minecraft.getInstance().player;
//            ItemStack chestplateItem = player.getSlot(102).get();
//
//            if (chestplateItem.getItem() instanceof Jetpack) {
//                PSClient.getInstance().ifPresent(psClient -> {
//                    if (psClient.doRender()) {
//                        Minecraft.getInstance().setScreen(new PlayerSpacecraftScreen(chestplateItem, player, psClient));
//                        psClient.setControllingBody(psClient.getPlayerOrbit());
//                    }
//                });
//            }
//        }
    }
}
