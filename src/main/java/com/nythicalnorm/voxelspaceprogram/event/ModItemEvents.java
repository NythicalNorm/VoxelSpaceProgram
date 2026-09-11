package com.nythicalnorm.voxelspaceprogram.event;

import com.nythicalnorm.voxelspaceprogram.Item.VSPItems;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModItemEvents {
    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent event) {
        if (!ServerPlayer.class.isAssignableFrom(event.getEntity().getClass())) {
            return;
        } else {
            if (event.getEntity().isUsingItem() && event.getEntity().getUseItem().getItem() == VSPItems.HANDHELD_PROPELLER.get()) {
                Vec3 force = event.getEntity().getLookAngle().normalize();
                force = force.scale(0.05d);
                event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().add(force));
                ((ServerPlayer) event.getEntity()).connection.send(new ClientboundSetEntityMotionPacket(event.getEntity()));
            }
        }
    }
}
