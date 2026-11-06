package com.arniks.water_mod;

import com.arniks.water_mod.network.ModNetwork;
import com.arniks.water_mod.block.waterskin.WaterskinItem;
import com.arniks.water_mod.block.waterskin.WaterskinBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(Water_mod.MODID)
public class Water_mod {
    public static final String MODID = "water_mod";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("water_mod", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.water_mod"))
                    .icon(() -> WaterskinItem.create(3, false))
                    .displayItems((params, output) -> {
                        output.accept(WaterskinItem.create(0, false));
                        output.accept(WaterskinItem.create(ModItems.WATERSKIN_IMPROVED.get(), 0, false));
                        output.accept(ModBlocks.DESALINATOR.get());
                        output.accept(ModItems.DESALINATOR_VIP.get());
                        output.accept(ModItems.WATER_TOWER.get());
                        output.accept(ModItems.CLOTH_ITEM.get());
                        output.accept(ModItems.DRY_CACTUS.get());
                        output.accept(ModItems.WET_SAND.get());
                        output.accept(ModItems.HOT_SAND.get());
                        output.accept(ModItems.CACTUS_CAMPFIRE.get());
                        output.accept(ModItems.BONE_SAND.get());
                        for (int i = 1; i <= 3; i++) output.accept(WaterskinItem.create(i, false));
                        for (int i = 1; i <= 3; i++) output.accept(WaterskinItem.create(i, true));
                        output.accept(WaterskinItem.create(ModItems.WATERSKIN_IMPROVED.get(), WaterskinBlock.IMPROVED_MAX_LEVEL, false));
                        output.accept(WaterskinItem.create(ModItems.WATERSKIN_IMPROVED.get(), WaterskinBlock.IMPROVED_MAX_LEVEL, true));
                    })
                    .build());

    public Water_mod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        CREATIVE_MODE_TABS.register(modBus);
        modBus.addListener(this::commonSetup);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
