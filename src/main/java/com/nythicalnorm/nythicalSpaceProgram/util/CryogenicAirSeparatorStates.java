package com.nythicalnorm.nythicalSpaceProgram.util;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.StringRepresentable;

public enum CryogenicAirSeparatorStates implements StringRepresentable {
    TOPNORTHLEFT("topnorthleft"),
    TOPNORTHMID("topnorthmid"),
    TOPNORTHRIGHT("topnorthright"),
    TOPMIDLEFT("topmidleft"),
    TOPMIDMID("topmidmid"),
    TOPMIDRIGHT("topmidright"),
    TOPSOUTHLEFT("topsouthleft"),
    TOPSOUTHMID("topsouthmid"),
    TOPSOUTHRIGHT("topsouthright"),

    MIDNORTHLEFT("midnorthleft"),
    MIDNORTHMID("midnorthmid"),
    MIDNORTHRIGHT("midnorthright"),
    MIDMIDLEFT("midmidleft"),
    NORMAL("normal"),
    MIDMIDRIGHT("midmidright"),
    MIDSOUTHLEFT("midsouthleft"),
    MIDSOUTHMID("midsouthmid"),
    MIDSOUTHRIGHT("midsouthright"),

    BOTTOMNORTHLEFT("bottomnorthleft"),
    BOTTOMNORTHMID("bottomnorthmid"),
    BOTTOMNORTHRIGHT("bottomnorthright"),
    BOTTOMMIDLEFT("bottommidleft"),
    BOTTOMMIDMID("bottommidmid"),
    BOTTOMMIDRIGHT("bottommidright"),
    BOTTOMSOUTHLEFT("bottomsouthleft"),
    BOTTOMSOUTHMID("bottomsouthmid"),
    BOTTOMSOUTHRIGHT("bottomsouthright"),

    SPECIALNORTHLEFT("bottomnorthleft"),
    SPECIALNORTHMID("bottomnorthmid"),
    SPECIALNORTHRIGHT("bottomnorthright"),
    SPECIALMIDLEFT("bottommidleft"),
    SPECIALMIDMID("bottommidmid"),
    SPECIALMIDRIGHT("bottommidright"),
    SPECIALSOUTHLEFT("bottomsouthleft"),
    SPECIALSOUTHMID("bottomsouthmid"),
    SPECIALSOUTHRIGHT("bottomsouthright"),

    OXYGENOUTPUT("oxygenoutput"),
    NITROGENOUTPUT("nitrogenoutput"),
    METHANEOUTPUT("methaneoutput");

    private final String name;

    CryogenicAirSeparatorStates(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
