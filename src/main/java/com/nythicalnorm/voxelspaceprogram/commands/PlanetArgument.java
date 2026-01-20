package com.nythicalnorm.voxelspaceprogram.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.nythicalnorm.voxelspaceprogram.VoxelSpaceProgram;
import com.nythicalnorm.voxelspaceprogram.solarsystem.PlanetsProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PlanetArgument implements ArgumentType<String> {
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((p_260119_) -> {
        return Component.translatable("voxelspaceprogram.commands.planet_invalid", p_260119_);
    });
    final String planet;


    private PlanetArgument(String plnt) {
        this.planet = plnt;
    }

    public static PlanetArgument planetArgument() {
        return new PlanetArgument("suriyan");
    }

    public static PlanetArgument planetArgument(String planet) {
        return new PlanetArgument(planet);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String parsedBody = null;
        if (reader.canRead()) {
            String planetName = reader.readString();
            PlanetsProvider planetsProvider = VoxelSpaceProgram.getAnyPlanetsProvider();

            if (!planetName.isEmpty() && planetsProvider.getPlanet(planetName) != null) {
                parsedBody = planetName;
            }
        }
        if (parsedBody == null) {
            throw ERROR_INVALID.createWithContext(reader,  reader.readString());
        }
        else {
            return parsedBody;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Iterable<String> planets = VoxelSpaceProgram.getAnyPlanetsProvider().getAllPlanetNames();
        return context.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(planets, builder) : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
    public static class Info implements ArgumentTypeInfo<PlanetArgument, PlanetArgument.Info.Template> {
        public void serializeToNetwork(PlanetArgument.Info.Template pTemplate, FriendlyByteBuf pBuffer) {
            pBuffer.writeUtf(pTemplate.planet);
        }

        public PlanetArgument.Info.@NotNull Template deserializeFromNetwork(FriendlyByteBuf pBuffer) {
            String plnt = pBuffer.readUtf();
            return new PlanetArgument.Info.Template(plnt);
        }

        @Override
        public void serializeToJson(Template pTemplate, JsonObject pJson) {
            pJson.addProperty("voxelspaceprogram_planet", pTemplate.planet);
        }

        public PlanetArgument.Info.@NotNull Template unpack(PlanetArgument pArgument) {
            return new PlanetArgument.Info.Template(pArgument.planet);
        }

        public final class Template implements ArgumentTypeInfo.Template<PlanetArgument> {
            public final String planet;

            Template(String pSun) {
                this.planet = pSun;
            }

            public @NotNull PlanetArgument instantiate(@NotNull CommandBuildContext pContext) {
                return PlanetArgument.planetArgument(this.planet);
            }

            public @NotNull ArgumentTypeInfo<PlanetArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
