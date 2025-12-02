package com.nythicalnorm.nythicalSpaceProgram.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.common.PlanetaryBody;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.DimensionTeleporter;
import com.nythicalnorm.nythicalSpaceProgram.solarsystem.OrbitalElements;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;

public class NSPTeleportCommand {
    public NSPTeleportCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nsp-tp").requires((stack) -> {
            return stack.hasPermission(2);
        })
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("planets", PlanetArgument.planetArgument())
                    .then(Commands.argument("orbit", Vec3Argument.vec3()).executes((stack) -> {
                        return TeleportToOrbit(stack.getSource(), Collections.singleton(stack.getSource().getEntityOrException()), stack.getArgument("planets", String.class), stack.getSource().getPosition());
                    })
                    )
                )
            )
        );
    }

    private int TeleportToOrbit(CommandSourceStack pSource, Collection<? extends Entity> pTargets, String body, Vec3 pos) {
        for(Entity entity : pTargets) {
            if (entity instanceof ServerPlayer) {
                if (NythicalSpaceProgram.getSolarSystem().isPresent()) {
                    OrbitalElements orbitalElement = new OrbitalElements(pos.x, 0d, pos.y, pos.z, 0f, 0f);
                    NythicalSpaceProgram.getSolarSystem().get().playerJoinOrbit(body, (ServerPlayer) entity, orbitalElement);
                }
            }
            pSource.sendSuccess(() -> {
                return Component.translatable("nythicalspaceprogram.commands.dimTeleport");
            }, true);
        }
        return 1;
    }

    private int NSPTeleport(CommandSourceStack pSource, Collection<? extends Entity> pTargets) throws CommandSyntaxException {
        for(Entity entity : pTargets) {
            if (entity instanceof ServerPlayer) {
                TeleportPlayer((ServerPlayer) entity);
            }
            pSource.sendSuccess(() -> {
                return Component.translatable("nythicalspaceprogram.commands.dimTeleport");
            }, true);
        }
        return pTargets.size();
    }

    private void TeleportPlayer(ServerPlayer player) {
        NythicalSpaceProgram.log("OMG");
        MinecraftServer minecraftserver = player.getServer();
        ResourceKey<Level> resourcekey = player.level().dimension() == SpaceDimension.SPACE_LEVEL_KEY ? Level.OVERWORLD : SpaceDimension.SPACE_LEVEL_KEY;

        ServerLevel portalDimension = minecraftserver.getLevel(resourcekey);
        if (portalDimension != null && !player.isPassenger()) {
            player.changeDimension(portalDimension, new DimensionTeleporter(new Vec3(0d, 128d, 0d)));
        }
    }
}
