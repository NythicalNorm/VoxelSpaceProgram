package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle;


import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelTimeAccess.class)
public interface LevelTimeAccessMixin extends LevelReader {

    @Shadow
    long dayTime();
    /**
     * @author NythicalNorm
     * @reason  This is the easiest way to change the apparent dayTime on the client side I would use inject if I could
     * but this is an interface. The use of the original function is still there and I will make sure the  function
     * doesn't crash the whole thing. And even if there is another mod replacing this it won't crash the game, I mean
     * changing a float output shouldn't cause problems, I will make sure my value is clamped to the original 0 - 1.
     */
    @Overwrite
    default float getTimeOfDay(float pPartialTick) {
        if (this.isClientSide()) {
            if (VoxelSpaceProgram.getCelestialStateSupplier().isPresent()) {
                if (VoxelSpaceProgram.getCelestialStateSupplier().get().isOnPlanet()) {
                    return VoxelSpaceProgram.getCelestialStateSupplier().get().getPlayerOrbit().getSunAngle();
                }
            }
        }
        else {
            if (VoxelSpaceProgram.getSolarSystem().isPresent()){
                PlanetaryBody planet = VoxelSpaceProgram.getSolarSystem().get().getPlanetsProvider().getDimensionPlanet(dimensionType());
                //there might be some issue
                if (planet != null) {
                    return DayNightCycleHandler.getSunAngleAtSpawn(planet);
                }
            }

        }

        return this.dimensionType().timeOfDay(this.dayTime());
    }
}
