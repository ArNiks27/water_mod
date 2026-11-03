package com.arniks.water_mod;


import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockOutLineClient {
    static final Set<ResourceLocation> ENABLED = new HashSet<>();
    static {
        ENABLED.add(BuiltInRegistries.BLOCK.getKey(ModBlocks.DESALINATOR.get()));
        ENABLED.add(BuiltInRegistries.BLOCK.getKey(ModBlocks.DESALINATOR_VIP.get()));
        ENABLED.add(BuiltInRegistries.BLOCK.getKey(ModBlocks.WATER_TOWER.get()));
    }
    public static boolean isOutlined(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        return ENABLED.contains(key);
    }
    public static void add(ResourceLocation blockId) {
        ENABLED.add(blockId);
    }
    public static void remove(ResourceLocation blockId) {
        ENABLED.remove(blockId);
    }
    public static Set<ResourceLocation> all() {
        return Collections.unmodifiableSet(ENABLED);
    }
}

@Mod.EventBusSubscriber(modid = Water_mod.MODID, value = Dist.CLIENT)
class BlockOutlineHighlightSuppressor {
    @SubscribeEvent
    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        var target = event.getTarget();
        var level = Minecraft.getInstance().level;
        if (level == null || target == null) return;
        BlockState state = level.getBlockState(target.getBlockPos());
        if (BlockOutLineClient.isOutlined(state.getBlock())) {
            event.setCanceled(true);
        }
    }
}


