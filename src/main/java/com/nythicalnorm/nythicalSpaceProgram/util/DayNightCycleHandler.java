package com.nythicalnorm.nythicalSpaceProgram.util;

import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetDimensions;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DayNightCycleHandler {

    @SubscribeEvent
    public static void OnSleepingTimeCheckEvent(SleepingTimeCheckEvent event) {
        Optional<Boolean> isday = isDay(event.getEntity().blockPosition(), event.getEntity().level());
        if (isday.isPresent()){
            if (isday.get()) {
                event.setResult(Event.Result.DENY);
            }
            else {
                event.setResult(Event.Result.ALLOW);
            }
        }
        else {
            event.setResult(Event.Result.DEFAULT);
        }
    }

    public static Optional<Float> getSunAngle(BlockPos pos, Level level) {
        Optional<PlanetaryBody> planet = PlanetDimensions.getDimPlanet(level.dimension());
        if (planet.isPresent()) {
            PlanetaryBody plnt = planet.get();
            Vector3d blockPosOnPlanet = Calcs.planetDimPosToNormalizedVector(pos.getCenter(), plnt.getRadius(), plnt.getRotation(), false);
            Vector3d planetAbsolutePos = plnt.getAbsolutePos().add(blockPosOnPlanet);
            return Optional.of(getSunAngle(blockPosOnPlanet, planetAbsolutePos));
        }
        else {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> isDay(BlockPos pos, Level level) {
        Optional<Integer> DarkenAmount = getDarknessLightLevel(pos,level);
        return DarkenAmount.map(integer -> !level.dimensionType().hasFixedTime() && integer < 4);
    }

    public static Optional<Integer> getDarknessLightLevel(BlockPos pos, Level level) {
        Optional<Float> sunAngle = getSunAngle(pos, level);
        return getDarknessLightLevel(sunAngle, level);
    }

    public static Optional<Integer> getDarknessLightLevel(Optional<Float> sunAngle, Level level) {
        if (sunAngle.isEmpty()) {
            return Optional.empty();
        }

        double rainLevel = 1.0D - (double) (level.getRainLevel(1.0F) * 5.0F) / 16.0D;
        double ThunderLevel = 1.0D - (double) (level.getThunderLevel(1.0F) * 5.0F) / 16.0D;
        double adjustedDarkeness = 0.5D + 2.0D * Mth.clamp(Mth.cos(sunAngle.get() * ((float) Math.PI * 2F)), -0.25D, 0.25D);
        int result =(int) ((1.0D - adjustedDarkeness * rainLevel * ThunderLevel) * 11.0D);
        return Optional.of(result);
    }

    public static float getSunAngle(Vector3d EntityRelativePos, Vector3d planetAbsolutePos) {
        Vector3f entityDir = new Vector3f((float) EntityRelativePos.x,(float) EntityRelativePos.y,(float) EntityRelativePos.z);
        Vector3f sunDir = new Vector3f((float) planetAbsolutePos.x,(float) planetAbsolutePos.y,(float) planetAbsolutePos.z);
        entityDir.normalize();
        sunDir.normalize();
        float diff = sunDir.dot(entityDir);
        diff = (diff + 1.0f) * 0.25f;
        return Mth.clamp(diff, 0f, 1f);
    }

    public static float getSunAngleAtSpawn(PlanetaryBody planetaryBody) {
        Vector3d spawnLocation = new Vector3d(planetaryBody.getRadius(), 0f, 0f);
        Quaternionf planetRot = planetaryBody.getRotation();
        spawnLocation.rotate(new Quaterniond(planetRot.x, planetRot.y,planetRot.z, planetRot.w));
        Vector3d planetAbsolutePos = planetaryBody.getAbsolutePos().add(spawnLocation);
        return getSunAngle(spawnLocation, planetAbsolutePos);
    }
}
