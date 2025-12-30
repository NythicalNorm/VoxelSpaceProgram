package com.nythicalnorm.voxelspaceprogram.mixin.vs;

import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.ArrayDeque;

@Mixin(PhysShipImpl.class)
public class PhysShipImplMixin {
    @Shadow
    @Final
    private ArrayDeque<Vector3dc> invForces;

    @Shadow
    @Final
    private ArrayDeque<Vector3dc> invPosForces;

    @Shadow
    @Final
    private ArrayDeque<Vector3dc> rotForces;

    @Inject(method = "applyQueuedForces", at = @At(value = "HEAD"), cancellable = false, remap = false)
    public void applyForces(CallbackInfo ci) {
//        PhysShipImpl physShip = (PhysShipImpl) (Object) this;
//        invPosForces.forEach(force -> {
//            VoxelSpaceProgram.log("force applied: " + force);
//        });
//        ci.cancel();
    }
}
