package com.nythicalnorm.voxelspaceprogram.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.dimensions.SpaceDimension;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.CelestialBodyAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = VoxelSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {
    private static final UUID gravityUUID = UUID.fromString("a13033dd-12dc-456f-901f-54c63734ac71");

    @SubscribeEvent
    public static void onFallDamage(LivingFallEvent event) {
        float fallDistance = event.getDistance();
        Level level = event.getEntity().level();
        CelestialBodyAccessor planetAccessor = (CelestialBodyAccessor) level;

        if (planetAccessor.isPlanet()){
            double planetAcceleration = planetAccessor.getCelestialBody().getEntityAccelerationDueToGravity();

            if (planetAcceleration <= 0){
                event.setCanceled(true);
            }
            double multfactor = ForgeMod.ENTITY_GRAVITY.get().getDefaultValue() / planetAcceleration;
            event.setDistance(fallDistance/(float) multfactor);
        }

        if (level.dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
            event.setDistance(0);
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void createDefaultAttributes(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            AttributeMap entityAttributes = ((LivingEntity)entity).getAttributes();
            CelestialBodyAccessor planetAccessor = (CelestialBodyAccessor) event.getLevel();

            //Optional<Double> levelGravity = PlanetDimensions.getAccelerationDueToGravityAt(entity.level());
            double tempGravity = 0;
            boolean applyGravityModifier = planetAccessor.isPlanet();

            if (applyGravityModifier) {
                tempGravity = planetAccessor.getCelestialBody().getEntityAccelerationDueToGravity();
            }

            AttributeModifier gravityModifier = new AttributeModifier(gravityUUID, "VoxelSpaceProgram.PlanetGravity",
                    tempGravity - ForgeMod.ENTITY_GRAVITY.get().getDefaultValue(), AttributeModifier.Operation.ADDITION); // Add -0;

            if (entityAttributes.hasAttribute(ForgeMod.ENTITY_GRAVITY.get())) {
                if (entityAttributes.hasModifier(ForgeMod.ENTITY_GRAVITY.get(), gravityUUID)) {
                    Multimap<Attribute, AttributeModifier> ogModifier = ArrayListMultimap.create();
                    ogModifier.put(ForgeMod.ENTITY_GRAVITY.get(), gravityModifier);
                    entityAttributes.removeAttributeModifiers(ogModifier);
                }

                if (applyGravityModifier) {
                    entityAttributes.getInstance(ForgeMod.ENTITY_GRAVITY.get()).addTransientModifier(gravityModifier);
                }
            }
        }
    }

    private static final double blockPushForce = 0.01d;

    @SubscribeEvent
    public static void onBlockUse(PlayerInteractEvent.RightClickBlock event) {
        applyBlockUseVelocity(event.getEntity(), blockPushForce);
    }

    @SubscribeEvent
    public static void onBlockUse(PlayerInteractEvent.LeftClickBlock event) {
        applyBlockUseVelocity(event.getEntity(), -blockPushForce);
    }

    private static void applyBlockUseVelocity(Player player, double force) {
        if (player.level().dimension().equals(SpaceDimension.SPACE_LEVEL_KEY) && player.level().isClientSide() && !player.isShiftKeyDown()) {
            Vec3 lookAngle = player.getLookAngle().normalize();
            player.addDeltaMovement(lookAngle.scale(force));
        }
    }
}
