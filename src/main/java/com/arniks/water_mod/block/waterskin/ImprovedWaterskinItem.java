package com.arniks.water_mod.block.waterskin;

import net.minecraft.world.level.block.Block;

public class ImprovedWaterskinItem extends WaterskinItem {
    public ImprovedWaterskinItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public String getDescriptionId() {
        return "item.water_mod.waterskin_improved";
    }
}
