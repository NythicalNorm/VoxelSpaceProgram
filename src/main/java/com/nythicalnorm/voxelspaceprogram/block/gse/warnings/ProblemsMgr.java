package com.nythicalnorm.voxelspaceprogram.block.gse.warnings;

import com.nythicalnorm.voxelspaceprogram.network.PacketHandler;
import com.nythicalnorm.voxelspaceprogram.network.assembler.ClientboundAssemblerProblems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ProblemsMgr {
    private final HashMap<String, ProblemInstance> assemblerProblems;
    private boolean updated = false;

    public ProblemsMgr() {
        assemblerProblems = new HashMap<>();
    }

    public void setProblem(AssemblyProblem problem, boolean isActive, @Nullable String argument) {
        ProblemInstance instance = assemblerProblems.get(problem.getId());

        if (isActive) {
            if (problem.isAdditive() && argument != null) {
                instance.addArgument(argument);
            } else {
                assemblerProblems.put(problem.getId(), new ProblemInstance(problem));
            }
        } else {
            if (problem.isAdditive() && argument != null) {
                instance.removeArgument(argument);
            } else {
                assemblerProblems.remove(problem.getId());
            }
        }
        updated = true;
    }

    public void setProblem(AssemblyProblem problem, boolean isActive) {
        setProblem(problem, isActive, null);
    }

    public void clearProblems() {
        assemblerProblems.clear();
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public Component[] getAssemblerProblems() {
        assemblerProblems.values().stream().sorted(Comparator.comparingInt(ProblemInstance::getSeviratiy));
        Component[] problemComponents = new Component[assemblerProblems.size()];
        int index = 0;

        for (ProblemInstance instance : assemblerProblems.values()) {
            problemComponents[index] = instance.getChatComponent();
            index++;
        }

        return problemComponents;
    }

    public void sendPacketIfUpdated(Player menuOpenedPlayer) {
        if (updated) {
            Component[] assemblerProblems = getAssemblerProblems();
            if (menuOpenedPlayer instanceof ServerPlayer plr) {
                PacketHandler.sendToPlayer(new ClientboundAssemblerProblems(assemblerProblems), plr);
                updated = false;
            }
        }
    }

    private static class ProblemInstance {
        private final AssemblyProblem problem;
        private final @Nullable List<String> stringArguments;

        public ProblemInstance(AssemblyProblem problem) {
            this.problem = problem;
            this.stringArguments = new ArrayList<>();
        }

        public void addArgument(@Nullable String argument) {
            stringArguments.add(argument);
        }

        public void removeArgument(@Nullable String argument) {
            stringArguments.remove(argument);
        }

        public Component getChatComponent() {
            if (stringArguments.isEmpty()) {
                return problem.getComponent();
            } else {
                return problem.getComponent(stringArguments.toArray());
            }
        }

        public int getSeviratiy() {
            return problem.getSeviratiy();
        }
    }
}
