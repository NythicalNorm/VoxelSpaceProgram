package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle;


import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import com.nythicalnorm.voxelspaceprogram.util.SidedCallsUtil;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
    default float getTimeOfDay(float pPartialTick) throws Exception {
        if (this.isClientSide()) {
          Float result = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> SidedCallsUtil::getPlayerSunAngle).call();
          if (result != null) {
             return result;
          }
        }
        else {
            PlanetAccessor planetAccessor = (PlanetAccessor) this;
            if (planetAccessor.isPlanet()) {
                return DayNightCycleHandler.getSunAngleAtSpawn(planetAccessor.getPlanetaryBody());
            }
        }
        return this.dimensionType().timeOfDay(this.dayTime());
    }
}
