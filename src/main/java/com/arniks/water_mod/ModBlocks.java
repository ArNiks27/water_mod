package com.arniks.water_mod;

import com.arniks.water_mod.block.desalinator.DesalinatorBlock;
import com.arniks.water_mod.block.desalinator2.Desalinator2Block;
import com.arniks.water_mod.block.cactuscampfire.CactusCampfireBlock;
import com.arniks.water_mod.block.hotsand.HotSandBlock;
import com.arniks.water_mod.block.waterskin.WaterskinBlock;
import com.arniks.water_mod.block.watertower.WaterTowerBlock;
import com.arniks.water_mod.block.wetsand.WetSandBlock;
import com.arniks.water_mod.block.wetsand.WetSandWaterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Water_mod.MODID);
    public static final RegistryObject<Block> WATERSKIN = BLOCKS.register("waterskin", WaterskinBlock::new);
    public static final RegistryObject<Block> DESALINATOR = BLOCKS.register("desalinator", DesalinatorBlock::new);
    public static final RegistryObject<Block> DESALINATOR_VIP = BLOCKS.register("desalinator_vip", Desalinator2Block::new);
    public static final RegistryObject<Block> WATER_TOWER = BLOCKS.register("water_tower", WaterTowerBlock::new);
    public static final RegistryObject<Block> WET_SAND = BLOCKS.register("wet_sand", WetSandBlock::new);
    public static final RegistryObject<Block> WET_SAND_WATER = BLOCKS.register("wet_sand_water", WetSandWaterBlock::new);
    public static final RegistryObject<Block> HOT_SAND = BLOCKS.register("hot_sand", HotSandBlock::new);
    public static final RegistryObject<Block> CACTUS_CAMPFIRE = BLOCKS.register("cactus_campfire", CactusCampfireBlock::new);
}
