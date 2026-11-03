package com.arniks.water_mod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class DryCactusItem extends Item {
    public DryCactusItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }
}
