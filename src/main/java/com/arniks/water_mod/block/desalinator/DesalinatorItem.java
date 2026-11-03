package com.arniks.water_mod.block.desalinator;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class DesalinatorItem extends BlockItem {
    public DesalinatorItem(Block block, Properties props) { super(block, props); }

    @Nullable @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext ctx) {
        BlockPlaceContext up = BlockPlaceContext.at(ctx, ctx.getClickedPos().above(), Direction.UP);
        return up.canPlace() ? up : null;
    }
}
