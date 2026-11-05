package com.arniks.water_mod.block.cactuscampfire;

import com.arniks.water_mod.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CactusCampfireBlock extends CampfireBlock {
    public CactusCampfireBlock() {
        super(true, 1, BlockBehaviour.Properties.copy(Blocks.CAMPFIRE));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CactusCampfireBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) {
            return state.getValue(LIT)
                    ? createTickerHelper(type, ModBlockEntities.CACTUS_CAMPFIRE.get(), CactusCampfireBlockEntity::clientTick)
                    : null;
        }
        return createTickerHelper(type, ModBlockEntities.CACTUS_CAMPFIRE.get(), CactusCampfireBlockEntity::serverTick);
    }
}
