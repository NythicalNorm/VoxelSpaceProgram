package com.nythicalnorm.voxelspaceprogram.util;

import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetAccessor;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
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

@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DayNightCycleHandler {

    //serverside Only Starting from here to
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
        if (SolarSystem.getInstance().isEmpty()) {
            return Optional.empty();
        }

        PlanetaryBody planet =  ((PlanetAccessor)level).getPlanetaryBody();
        if (planet != null) {
            Vector3d blockPosOnPlanet = Calcs.planetDimPosToNormalizedVector(pos.getCenter(), planet.getRadius(), planet.getRotation(), true);
            Vector3d planetAbsolutePos = planet.getAbsolutePos().add(blockPosOnPlanet);
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


    // client side here
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

    //common static ones here
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


    // this will not work when the starting earth rotation at time 0 is different from it is now
    // reference: https://stackoverflow.com/questions/5188561/signed-angle-between-two-3d-vectors-with-same-origin-within-the-same-plane
    public static Optional<Long> getDayTime(BlockPos pos, PlanetaryBody plnt, long TimeElapsed) {
        Vector3d blockPosOnPlanet = Calcs.planetDimPosToNormalizedVector(pos.getCenter(), plnt.getRadius(), plnt.getRotation(), true);
        blockPosOnPlanet.normalize();
        Vector3d planetAbsolutePos = plnt.getAbsolutePos().add(blockPosOnPlanet);
        float signedAngle = getSunAngle(blockPosOnPlanet, planetAbsolutePos);

//        Vector3d crossProduct = planetAbsolutePos.cross(blockPosOnPlanet);
//        Vector3d Vn = new Vector3d(0,-1,0);
//
//        if (crossProduct.dot(Vn) < 0) {
//            signedAngle = -signedAngle;
//        }
//
//        if (signedAngle < 0f) {
//            signedAngle = 1 - signedAngle;
//        }
//
//        signedAngle += 0.25f;
//
//        double timeNormalized = signedAngle + (TimeElapsed+Math.sin(plnt.getNorthPoleDir().angle))/plnt.getRotationPeriod();
//        timeNormalized = timeNormalized * 24000d;
//
//        if (timeNormalized < 0) {
//            timeNormalized = timeNormalized + 24000d;
//        }
        double extraTime = (double) TimeElapsed / plnt.getRotationPeriod();
        float normalTime = 6000f + (signedAngle*24000f) + (float) Math.floor(extraTime)*24000f;
        return Optional.of((long) normalTime);
    }
}
