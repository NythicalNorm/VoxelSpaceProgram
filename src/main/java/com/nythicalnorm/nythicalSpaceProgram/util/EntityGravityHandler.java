package com.nythicalnorm.nythicalSpaceProgram.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetLevelData;
import com.nythicalnorm.nythicalSpaceProgram.planet.PlanetLevelDataProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = NythicalSpaceProgram.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityGravityHandler {
    private static final UUID gravityUUID = UUID.fromString("a13033dd-12dc-456f-901f-54c63734ac71");

    @SubscribeEvent
    public static void onFallDamage(LivingFallEvent event) {
        float fallDistance = event.getDistance();
        Level level = event.getEntity().level();

        level.getCapability(PlanetLevelDataProvider.PLANET_LEVEL_DATA).ifPresent(planetLevelData -> {
            double planetAcceleration = planetLevelData.getAccelerationDueToGravity(NythicalSpaceProgram.getSolarSystem().get().getPlanets());

            if (planetAcceleration <= 0){
                event.setCanceled(true);
            }
            double multfactor = ForgeMod.ENTITY_GRAVITY.get().getDefaultValue() / planetAcceleration;
            event.setDistance(fallDistance/(float) multfactor);
        });

        if (level.dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
            event.setDistance(0);
        }
    }

    @SubscribeEvent // on the mod event bus
    public static void createDefaultAttributes(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            AttributeMap entityAttributes = ((LivingEntity)entity).getAttributes();
            LazyOptional<PlanetLevelData> plntData = event.getLevel().getCapability(PlanetLevelDataProvider.PLANET_LEVEL_DATA);

            //Optional<Double> levelGravity = PlanetDimensions.getAccelerationDueToGravityAt(entity.level());
            double tempGravity = 0;
            boolean applyGravityModifier = (plntData.resolve().isPresent() && NythicalSpaceProgram.getSolarSystem().isPresent());

            if (applyGravityModifier) {
                tempGravity = plntData.resolve().get().getAccelerationDueToGravity(NythicalSpaceProgram.getSolarSystem().get().getPlanets());
            }

            AttributeModifier gravityModifier = new AttributeModifier(gravityUUID, "NythicalSpaceProgram.PlanetGravity",
                    tempGravity - ForgeMod.ENTITY_GRAVITY.get().getDefaultValue(), AttributeModifier.Operation.ADDITION); // Add -0;

            if (entityAttributes.hasAttribute(ForgeMod.ENTITY_GRAVITY.get())) {
                if (entityAttributes.hasModifier(ForgeMod.ENTITY_GRAVITY.get(), gravityUUID)) {
                    Multimap<Attribute, AttributeModifier> ogModifier = ArrayListMultimap.create();
                    ogModifier.put(ForgeMod.ENTITY_GRAVITY.get(), gravityModifier);
                    entityAttributes.removeAttributeModifiers(ogModifier);
                }

                if (applyGravityModifier || event.getLevel().dimension() == SpaceDimension.SPACE_LEVEL_KEY) {
                    entityAttributes.getInstance(ForgeMod.ENTITY_GRAVITY.get()).addTransientModifier(gravityModifier);
                }
            }
        }
    }
}
