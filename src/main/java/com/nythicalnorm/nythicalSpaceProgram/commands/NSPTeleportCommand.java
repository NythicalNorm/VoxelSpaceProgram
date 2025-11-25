package com.nythicalnorm.nythicalSpaceProgram.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nythicalnorm.nythicalSpaceProgram.NythicalSpaceProgram;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.SpaceDimension;
import com.nythicalnorm.nythicalSpaceProgram.dimensions.DimensionTeleporter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
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
        .executes((stack) -> {
            return NSPTeleport(stack.getSource(), Collections.singleton(stack.getSource().getEntityOrException()));
        })));
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
