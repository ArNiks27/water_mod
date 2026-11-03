package com.arniks.water_mod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class ClothItem extends Item {
    public ClothItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }
}
