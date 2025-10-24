package com.nythicalnorm.nythicalSpaceProgram.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum FootprintedType implements StringRepresentable {
    NOFOOTPRINTS("none"),
    TWOBOOTXFACING("2bootfacingx"),
    TWOBOOTZFACING("2bootfacingz"),
    ONEBOOTXFACING("1bootfacingx"),
    ONEBOOTZFACING("1bootfacingz"),

    TWOBOOTXZPosFACING("2bootfacingposxz"),
    TWOBOOTXZNegFACING("2bootfacingnegxz"),
    ONEBOOTXZPosFACING("1bootfacingposxz"),
    ONEBOOTXZNegFACING("1bootfacingnegxz");



    private final String name;

    FootprintedType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}