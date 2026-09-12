package com.nythicalnorm.voxelspaceprogram.Item.custom;

import com.nythicalnorm.voxelspaceprogram.fluid.containers.HandheldThrusterFluidHandler;
import com.nythicalnorm.voxelspaceprogram.sound.VSPSounds;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class HandheldThrusterItem extends Item {
    public static final int CAPACITY = 2000;
    public HandheldThrusterItem(Properties pProperties) {
        super(pProperties.stacksTo(1).setNoRepair());
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (pLevel.isClientSide()) {
            return;
        }

        pStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidItem -> {
            if (fluidItem.drain(1, IFluidHandler.FluidAction.EXECUTE).getAmount() > 0) {
                Vec3 force = pLivingEntity.getLookAngle().normalize();
                if (pRemainingUseDuration % 5 == 0) {
                    Vec3 spawnPos = new Vec3(force.x(), force.y(), force.z()).scale(1.0f).add(pLivingEntity.position());
                    Vec3 spawnDir = new Vec3(force.x(), force.y(), force.z());

                    ((ServerLevel) pLevel).sendParticles(
                            ParticleTypes.CLOUD,
                            spawnPos.x(), spawnPos.y(), spawnPos.z(),
                            5,
                            spawnDir.x(), spawnDir.y(), spawnDir.z(),
                            0.08f
                    );
                }

                pLivingEntity.setDeltaMovement(pLivingEntity.getDeltaMovement().add(force.scale(-0.05d)));
                pLivingEntity.resetFallDistance();
                if (pLivingEntity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                }
            }
        });
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (getFluidAmount(itemstack) > 0) {
            if (!pLevel.isClientSide()) {
                pPlayer.startUsingItem(pUsedHand);
                pLevel.playSeededSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), VSPSounds.HANDHELD_PROPELLER_START.get(), SoundSource.AMBIENT, 1f, 1f, 0);
                return InteractionResultHolder.consume(itemstack);
            }
        }

       return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag) {
        return new ICapabilityProvider() {
            private final LazyOptional<IFluidHandlerItem> handler = LazyOptional.of(() -> new HandheldThrusterFluidHandler(stack));

            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                if (capability == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                    return handler.cast();
                }

                return LazyOptional.empty();
            }
        };
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0f * getFluidAmount(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb((float) getFluidAmount(stack) / CAPACITY * 0.33f, 1.0f, 1.0f);
    }

    public int getFluidAmount(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);

        if (handler == null) {
            return 0;
        }

        return handler.getFluidInTank(0).getAmount();
    }
}
