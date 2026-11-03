package com.arniks.water_mod;

import com.arniks.water_mod.block.desalinator.DesalinatorBlockEntity;
import com.arniks.water_mod.block.desalinator2.Desalinator2BlockEntity;
import com.arniks.water_mod.block.watertower.WaterTowerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Water_mod.MODID);

    public static final RegistryObject<BlockEntityType<DesalinatorBlockEntity>> DESALINATOR =
            BLOCK_ENTITIES.register("desalinator", () ->
                    BlockEntityType.Builder.of(DesalinatorBlockEntity::new, ModBlocks.DESALINATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<Desalinator2BlockEntity>> DESALINATOR_VIP =
            BLOCK_ENTITIES.register("desalinator_2", () ->
                    BlockEntityType.Builder.of(Desalinator2BlockEntity::new, ModBlocks.DESALINATOR_VIP.get()).build(null));
    public static final RegistryObject<BlockEntityType<WaterTowerBlockEntity>> WATER_TOWER =
            BLOCK_ENTITIES.register("water_tower", () ->
                    BlockEntityType.Builder.of(WaterTowerBlockEntity::new, ModBlocks.WATER_TOWER.get()).build(null));
}
