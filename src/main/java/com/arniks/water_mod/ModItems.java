package com.arniks.water_mod;

import com.arniks.water_mod.item.ClothItem;
import com.arniks.water_mod.item.Desalinator2Item;
import com.arniks.water_mod.item.DryCactusItem;
import com.arniks.water_mod.item.displayDesalinatorItem;
import com.arniks.water_mod.block.waterskin.ImprovedWaterskinItem;
import com.arniks.water_mod.item.WaterTowerItem;
import com.arniks.water_mod.block.waterskin.WaterskinItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Water_mod.MODID);
    public static final RegistryObject<Item> WATERSKIN = ITEMS.register("waterskin",
            () -> new WaterskinItem(ModBlocks.WATERSKIN.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WATERSKIN_IMPROVED = ITEMS.register("waterskin_improved",
            () -> new ImprovedWaterskinItem(ModBlocks.WATERSKIN.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DESALINATOR = ITEMS.register("desalinator",
            () -> new displayDesalinatorItem(ModBlocks.DESALINATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> DESALINATOR_VIP = ITEMS.register("desalinator_vip",
            () -> new Desalinator2Item(ModBlocks.DESALINATOR_VIP.get(), new Item.Properties()));
    public static final RegistryObject<Item> CLOTH_ITEM = ITEMS.register("cloth",
            () -> new ClothItem());
    public static final RegistryObject<Item> DRY_CACTUS = ITEMS.register("dry_cactus",
            () -> new DryCactusItem());
    public static final RegistryObject<Item> WATER_TOWER = ITEMS.register("water_tower",
            () -> new WaterTowerItem(ModBlocks.WATER_TOWER.get(), new Item.Properties()));
    public static final RegistryObject<Item> WET_SAND = ITEMS.register("wet_sand",
            () -> new BlockItem(ModBlocks.WET_SAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> HOT_SAND = ITEMS.register("hot_sand",
            () -> new BlockItem(ModBlocks.HOT_SAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> CACTUS_CAMPFIRE = ITEMS.register("cactus_campfire",
            () -> new BlockItem(ModBlocks.CACTUS_CAMPFIRE.get(), new Item.Properties()));
}
