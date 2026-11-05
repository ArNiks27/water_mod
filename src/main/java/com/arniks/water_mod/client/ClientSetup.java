package com.arniks.water_mod.client;

import com.arniks.water_mod.ModBlockEntities;
import com.arniks.water_mod.ModItems;
import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.client.CactusCampfireRenderer;
import com.arniks.water_mod.block.waterskin.WaterskinItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Water_mod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ResourceLocation content = new ResourceLocation(Water_mod.MODID, "content");
            ItemProperties.register(ModItems.WATERSKIN.get(), content,
                    (stack, level, entity, seed) -> itemContentPredicate(stack));
            ItemProperties.register(ModItems.WATERSKIN_IMPROVED.get(), content,
                    (stack, level, entity, seed) -> itemContentPredicate(stack));
        });
    }

    private static float itemContentPredicate(net.minecraft.world.item.ItemStack stack) {
        if (WaterskinItem.getLevel(stack) <= 0) return 0f;
        return WaterskinItem.isSalty(stack) ? 2f : 1f;
    }

    @SubscribeEvent
    public static void onRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DESALINATOR.get(), ctx -> new DesalinatorRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.DESALINATOR_VIP.get(), ctx -> new Desalinator2Renderer());
        event.registerBlockEntityRenderer(ModBlockEntities.WATER_TOWER.get(), ctx -> new WaterTowerRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_CAMPFIRE.get(), ctx -> new CactusCampfireRenderer());
    }

    @SubscribeEvent
    public static void onOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "thirst", ThirstHud.OVERLAY);
    }
}
