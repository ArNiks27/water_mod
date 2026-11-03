package com.arniks.water_mod.block.desalinator2;

import net.minecraft.util.StringRepresentable;

public enum Desalinator2Content implements StringRepresentable {
    EMPTY("empty", "opresnitel_vip"),
    SALT_WATER("salt_water", "opresnitel_vip_salt_water"),
    CACTUS("cactus", "opresnitel_vip_cactus"),
    DRY_CACTUS("dry_cactus", "opresnitel_vip_black_cactus");

    private final String name;
    public final String geoName;

    Desalinator2Content(String name, String geoName) { this.name = name; this.geoName = geoName; }

    @Override public String getSerializedName() { return name; }
}
