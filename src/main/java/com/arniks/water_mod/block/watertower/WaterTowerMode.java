package com.arniks.water_mod.block.watertower;

import net.minecraft.util.StringRepresentable;

public enum WaterTowerMode implements StringRepresentable {
    IDLE("idle"),
    ONS("ons"),
    FRESH("fresh"),
    SALT("salt"),
    SPENT("spent");

    private final String name;

    WaterTowerMode(String name) { this.name = name; }

    @Override public String getSerializedName() { return name; }
}