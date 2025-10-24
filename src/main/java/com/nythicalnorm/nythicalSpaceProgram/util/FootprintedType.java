package com.nythicalnorm.nythicalSpaceProgram.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum FootprintedType implements StringRepresentable {
    NOFOOTPRINTS("none"),
    TWOBOOTXFACING("2bootfacingx"),
    TWOBOOTZFACING("2bootfacingz"),
    TWOBOOTSTANDINGXFACING("2bootstandingfacingx"),
    TWOBOOTSTANDINGZFACING("2bootstandingfacingz"),

    LEFTBOOTXFACING("leftbootfacingx"),
    LEFTBOOTZFACING("leftbootfacingz"),
    RIGHTBOOTXFACING("rightbootfacingx"),
    RIGHTBOOTZFACING("rightbootfacingz");



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