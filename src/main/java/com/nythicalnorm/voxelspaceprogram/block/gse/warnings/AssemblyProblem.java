package com.nythicalnorm.voxelspaceprogram.block.gse.warnings;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class AssemblyProblem {
    private static final String ProblemLangLocation = "assembler_warnings.voxelspaceprogram.";
    private final String id;
    private final boolean isError;
    private final boolean isAdditive;
    private final String stringComponentID;
    private int seviratiy;

    public AssemblyProblem(String pId, boolean isError, boolean isAdditive) {
        this.id = pId;
        this.isError = isError;
        this.isAdditive = isAdditive;
        this.stringComponentID = ProblemLangLocation.concat(pId);
        this.seviratiy = 0;
        if (isError) {
            this.seviratiy = seviratiy + 10;
        }
    }

    public String getId() {
        return id;
    }

    public ChatFormatting getTextColor() {
        return isError ? ChatFormatting.RED : ChatFormatting.YELLOW;
    }

    public int getSeviratiy() {
        return seviratiy;
    }

    public boolean isAdditive() {
        return isAdditive;
    }

    public Component getComponent(Object[] args) {
        return Component.translatable(stringComponentID, args).withStyle(getTextColor());
    }

    public Component getComponent() {
        return Component.translatable(stringComponentID).withStyle(getTextColor());
    }
}
