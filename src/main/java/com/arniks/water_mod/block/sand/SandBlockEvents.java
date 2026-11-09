package com.arniks.water_mod.block.sand;

import com.arniks.water_mod.ModItems;
import com.arniks.water_mod.Water_mod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Water_mod.MODID)
public final class SandBlockEvents {
    private static final String SAND_COMMAND =
            "summon item ~ ~ ~ {Item:{id:\"blocklayering:layer_sand\",Count:1b}}";

    private SandBlockEvents() { }

    @SubscribeEvent
    public static void onSandBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SHOVELS)) {
            if (!event.getState().is(Blocks.SAND)) return;

            event.setCanceled(true);
            if (event.getLevel() instanceof ServerLevel level) {
                executeSandCommand(level, event.getPos());
            }
        } else {
            return;}
    }
    private static void executeSandCommand(ServerLevel level, net.minecraft.core.BlockPos pos) {
        CommandSourceStack source = level.getServer().createCommandSourceStack()
                .withLevel(level)
                .withPosition(Vec3.atCenterOf(pos))
                .withPermission(2)
                .withSuppressedOutput();
        level.getServer().getCommands().performPrefixedCommand(source, SAND_COMMAND);
    }
}
