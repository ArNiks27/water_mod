package com.arniks.water_mod.block.bonesand;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BoneSandBlock extends FallingBlock {
    public BoneSandBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.SAND));
    }
}
