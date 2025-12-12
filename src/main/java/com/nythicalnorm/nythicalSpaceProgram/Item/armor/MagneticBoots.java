package com.nythicalnorm.nythicalSpaceProgram.Item.armor;

import com.nythicalnorm.nythicalSpaceProgram.Item.ModItems;
import com.nythicalnorm.nythicalSpaceProgram.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagneticBoots extends ArmorItem {
    public MagneticBoots(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        ItemStack boots = player.getInventory().getArmor(0);

        if (!level.isClientSide() && !boots.isEmpty()) {
            if (boots.getItem() == ModItems.MAGNET_BOOTS.get() && !player.isSpectator() && !player.getAbilities().flying
                    && !player.noPhysics && !player.onGround()) {

                if (isBlockMagneticClose(player.position(), player.blockPosition(), level)) {
                    Vec3 force = new Vec3(0, -0.2d, 0);
                    player.setDeltaMovement(player.getDeltaMovement().add(force));
                    ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));
                }
            }
        }
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    private boolean isBlockMagneticClose(Vec3 playerpos, BlockPos playerBlockpos, Level level) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 1; y <= 2; y++) {
                    BlockPos searchingBlock = playerBlockpos.below(y).north(z).east(x);

                    if ((playerpos.subtract(searchingBlock.getCenter())).horizontalDistance() > 0.795d) {
                        continue;
                    }

                    if (level.getBlockState(searchingBlock).is(ModTags.Blocks.MAGNETIC_METALS)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}