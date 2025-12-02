package com.nythicalnorm.nythicalSpaceProgram.mixin;


import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.util.DayNightCycleHandler;
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
            if (NythicalSpaceProgram.getCelestialStateSupplier().isPresent()) {
                if (NythicalSpaceProgram.getCelestialStateSupplier().get().isOnPlanet()) {
                    return NythicalSpaceProgram.getCelestialStateSupplier().get().getPlayerData().getSunAngle();
                }
            }
        }
        else {
            if (NythicalSpaceProgram.getSolarSystem().isPresent()){
                PlanetaryBody planet = NythicalSpaceProgram.getSolarSystem().get().getPlanets().getDimensionPlanet(dimensionType());
                //there might be some issue
                if (planet != null) {
                    return DayNightCycleHandler.getSunAngleAtSpawn(planet);
                }
            }

        }

        return this.dimensionType().timeOfDay(this.dayTime());
    }
}
