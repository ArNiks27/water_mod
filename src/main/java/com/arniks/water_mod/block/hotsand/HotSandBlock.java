package com.arniks.water_mod.block.hotsand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class HotSandBlock extends FallingBlock {
    public static final BooleanProperty HEATING = BooleanProperty.create("heating");
    public static final BooleanProperty HAS_FIRE = BooleanProperty.create("has_fire");

    public HotSandBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.SAND));
        registerDefaultState(stateDefinition.any().setValue(HEATING, false).setValue(HAS_FIRE, false));
    }

    public static boolean isHot(BlockState state) {
        return state.getBlock() instanceof HotSandBlock && !state.getValue(HEATING);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEATING, HAS_FIRE);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HEATING)) {
            removeHeatingFire(level, pos, state);
            level.setBlock(pos, state.setValue(HEATING, false).setValue(HAS_FIRE, false), Block.UPDATE_ALL);
            return;
        }
        super.tick(state, level, pos, random);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(this) && state.getValue(HEATING)) removeHeatingFire(level, pos, state);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private static void removeHeatingFire(Level level, BlockPos pos, BlockState state) {
        BlockPos firePos = pos.above();
        if (state.getValue(HAS_FIRE) && level.getBlockState(firePos).is(Blocks.FIRE)) {
            level.removeBlock(firePos, false);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(HEATING)) return;
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextDouble(), pos.getY() + 1.0,
                    pos.getZ() + random.nextDouble(), 0, 0, 0);
        }
        if (random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextDouble(), pos.getY() + 1.0,
                    pos.getZ() + random.nextDouble(), 0, 0.02, 0);
        }
    }
}
