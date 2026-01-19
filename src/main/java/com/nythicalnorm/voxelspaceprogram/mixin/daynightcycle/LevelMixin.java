package com.nythicalnorm.voxelspaceprogram.mixin.daynightcycle;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import com.nythicalnorm.voxelspaceprogram.util.DayNightCycleHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Level.class)
public class LevelMixin implements PlanetAccessor {
    @Unique
    PlanetaryBody planetaryBody;

    @Override
    public boolean isPlanet() {
        return planetaryBody != null;
    }

    @Override
    public PlanetaryBody getPlanetaryBody() {
        return planetaryBody;
    }

    @Override
    public void setPlanetaryBody(PlanetaryBody planetaryBody) {
        this.planetaryBody = planetaryBody;
    }

    // Still makes no sense why mixining this function affects passive mob spawn as this seems to just
    // linearly increase difficulty and caps out at 3 days...???
    @Inject(method = "getCurrentDifficultyAt", at= @At(value = "HEAD"),cancellable = true)
    public void getCurrentDifficultyAt(BlockPos pPos, CallbackInfoReturnable<DifficultyInstance> cir) {
        Level level = (Level) (Object)this;

        if (!level.isClientSide()) {
            Optional<Long> currentTime = Optional.empty();
            if (isPlanet() && SolarSystem.getInstance().isPresent()) {
                currentTime = DayNightCycleHandler.getDayTime(pPos, getPlanetaryBody(), SolarSystem.getInstance().get().getCurrentTime());
            }

            if (currentTime.isPresent()) {
                long i = 0L;
                float f = 0.0F;

                if (level.hasChunkAt(pPos)) {
                    f = level.getMoonBrightness();
                    i = level.getChunkAt(pPos).getInhabitedTime();
                }

                cir.setReturnValue(new DifficultyInstance(level.getDifficulty(), currentTime.get(), i, f));
            }
        }
    }
}
