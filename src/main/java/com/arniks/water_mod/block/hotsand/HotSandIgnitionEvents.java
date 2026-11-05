package com.arniks.water_mod.block.hotsand;

import com.arniks.water_mod.ModBlocks;
import com.arniks.water_mod.Water_mod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Water_mod.MODID)
public class HotSandIgnitionEvents {

    @SubscribeEvent
    public static void onRightClickSand(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(Items.FLINT_AND_STEEL) || !event.getLevel().getBlockState(event.getPos()).is(Blocks.SAND)) return;
        if (event.getLevel().isClientSide || !(event.getLevel() instanceof ServerLevel level)) return;
        if (!event.getEntity().mayInteract(event.getLevel(), event.getPos())) return;

        event.setCanceled(true);

        boolean canShowFire = level.getBlockState(event.getPos().above()).isAir();
        if (canShowFire) {
            level.setBlock(event.getPos().above(), Blocks.FIRE.defaultBlockState(), 3);
        }
        level.scheduleTick(event.getPos(), ModBlocks.HOT_SAND.get(), 80);
        level.setBlock(event.getPos(), ModBlocks.HOT_SAND.get().defaultBlockState()
                .setValue(HotSandBlock.HEATING, true)
                .setValue(HotSandBlock.HAS_FIRE, canShowFire), 3);
        level.playSound(null, event.getPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                1.0F, level.random.nextFloat() * 0.4F + 0.8F);

        ItemStack lighter = event.getItemStack();
        if (!event.getEntity().getAbilities().instabuild) {
            lighter.hurtAndBreak(1, event.getEntity(), player -> player.broadcastBreakEvent(event.getHand()));
        }
    }
}
