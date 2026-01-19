package com.nythicalnorm.voxelspaceprogram.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.nythicalnorm.voxelspaceprogram.SolarSystem;
import com.nythicalnorm.voxelspaceprogram.solarsystem.orbits.OrbitalElements;
import com.nythicalnorm.voxelspaceprogram.solarsystem.bodies.PlanetaryBody;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class NSPTeleportCommand {
    public NSPTeleportCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("vsp-tp").requires((stack) -> {
            return stack.hasPermission(2);
        })
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("planets", PlanetArgument.planetArgument())
                    .then(Commands.argument("semi-major_axis", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("eccentricity", DoubleArgumentType.doubleArg())
                            .then(Commands.argument("inclination", DoubleArgumentType.doubleArg())
                                    .executes((stack) -> {
                                    return TeleportToOrbit(stack.getSource(), EntityArgument.getEntities(stack, "targets"),
                                    stack.getArgument("planets", String.class),
                                    DoubleArgumentType.getDouble(stack, "semi-major_axis"),
                                    DoubleArgumentType.getDouble(stack, "eccentricity"),
                                    DoubleArgumentType.getDouble(stack, "inclination"));
                                })
                            )
                        )
                    )
                )
            )
        );
    }

    private int TeleportToOrbit(CommandSourceStack pSource, Collection<? extends Entity> pTargets, String body,
                                double semiMajorAxisInput, double eccentricity, double inclination) {
        for(Entity entity : pTargets) {
            if (entity instanceof ServerPlayer) {
                SolarSystem.getInstance().ifPresent(solarSystem -> {
                    PlanetaryBody planet = solarSystem.getPlanetsProvider().getPlanet(body);
                    double semiMajorAxis = (semiMajorAxisInput*1000d) + planet.getRadius();
                    if (semiMajorAxisInput < 0) {
                        semiMajorAxis = (semiMajorAxisInput*1000d) - planet.getRadius();
                        //return 0;
                    }
                    long startingAnomaly = solarSystem.getCurrentTime();
                    OrbitalElements orbitalElement = new OrbitalElements(semiMajorAxis, eccentricity, startingAnomaly, inclination, 0d, 0d);
                    solarSystem.playerJoinOrbit(planet, (ServerPlayer) entity, orbitalElement);
                });
            }
            pSource.sendSuccess(() -> {
                return Component.translatable("voxelspaceprogram.commands.dimTeleport");
            }, true);
        }
        return 1;
    }

//    private int NSPTeleport(CommandSourceStack pSource, Collection<? extends Entity> pTargets) throws CommandSyntaxException {
//        for(Entity entity : pTargets) {
//            if (entity instanceof ServerPlayer) {
//                TeleportPlayer((ServerPlayer) entity);
//            }
//            pSource.sendSuccess(() -> {
//                return Component.translatable("voxelspaceprogram.commands.dimTeleport");
//            }, true);
//        }
//        return pTargets.size();
//    }
//
//    private void TeleportPlayer(ServerPlayer player) {
//        VoxelSpaceProgram.log("OMG");
//        MinecraftServer minecraftserver = player.getServer();
//        ResourceKey<Level> resourcekey = player.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY ? Level.OVERWORLD : SpaceDimension.SPACE_LEVEL_KEY;
//
//        ServerLevel portalDimension = minecraftserver.getLevel(resourcekey);
//        if (portalDimension != null && !player.isPassenger()) {
//            player.changeDimension(portalDimension, new DimensionTeleporter(new Vec3(0d, 128d, 0d)));
//        }
//    }
}
