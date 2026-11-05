package com.arniks.water_mod.block.wetsand;

import com.arniks.water_mod.ModBlocks;
import com.arniks.water_mod.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class WetSandBlock extends FallingBlock {

    @Override
    public List<ItemStack> getDrops(BlockState p_287732_, LootParams.Builder p_287596_) {
        return Collections.emptyList();
    }

    public WetSandBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.6f).sound(SoundType.SAND));
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SHOVELS)) {
            if (!level.isClientSide && level.getBlockState(pos).isAir()) {
                Block temporaryWater = ModBlocks.WET_SAND_WATER.get();
                if (level.setBlock(pos, temporaryWater.defaultBlockState(), Block.UPDATE_ALL)) {
                    level.scheduleTick(pos, temporaryWater, 20);
                }
            }
        } else {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.WET_SAND.get()));
        }
    }
}
