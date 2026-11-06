package com.arniks.water_mod.block.bonesand;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class BoneSandBlock extends FallingBlock {
    public BoneSandBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.SAND));

    }
    @Override
    public void playerDestroy(Level p_49827_, Player p_49828_, BlockPos p_49829_, BlockState state, @Nullable BlockEntity p_49831_, ItemStack p_49832_) {
        super.playerDestroy(p_49827_, p_49828_, p_49829_, state, p_49831_, p_49832_);
        p_49827_.setBlock(p_49829_, Blocks.SAND.defaultBlockState(), Block.UPDATE_ALL);
    }
}
