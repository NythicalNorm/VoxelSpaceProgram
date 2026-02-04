package com.nythicalnorm.voxelspaceprogram.mixin.spaceentites;

import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerSpaceMixin {
    @Inject(method = "serverAiStep", at = @At(value = "TAIL"))
    public void serverAIstep(CallbackInfo ci) {
        LocalPlayer player = ((LocalPlayer)(Object) this);

        if (player.level() != null && player.level().dimension().equals(SpaceDimension.SPACE_LEVEL_KEY) && !player.onGround() &&
                !player.getAbilities().flying) {
            player.xxa = 0.0f;
            player.zza = 0.0f;
        }
    }
}
