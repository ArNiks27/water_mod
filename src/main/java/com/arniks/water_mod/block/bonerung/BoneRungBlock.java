package com.arniks.water_mod.block.bonerung;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BoneRungBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape POSITIVE_DIAGONAL = makeDiagonalShape(true);
    private static final VoxelShape NEGATIVE_DIAGONAL = makeDiagonalShape(false);

    public BoneRungBlock() {
        super(BlockBehaviour.Properties.of().strength(0.2F).sound(SoundType.BONE_BLOCK).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? POSITIVE_DIAGONAL : NEGATIVE_DIAGONAL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level,
                                        BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction firstSupport = switch (facing) {
            case NORTH, EAST -> Direction.NORTH;
            case SOUTH, WEST -> Direction.SOUTH;
            default -> null;
        };
        Direction secondSupport = switch (facing) {
            case NORTH, WEST -> Direction.WEST;
            case EAST, SOUTH -> Direction.EAST;
            default -> null;
        };
        if (firstSupport == null || secondSupport == null) return false;

        BlockPos firstPos = pos.relative(firstSupport);
        BlockPos secondPos = pos.relative(secondSupport);
        return level.getBlockState(firstPos).isFaceSturdy(level, firstPos, firstSupport.getOpposite())
                && level.getBlockState(secondPos).isFaceSturdy(level, secondPos, secondSupport.getOpposite());
    }

    public static Direction facingForCorner(Direction firstSupport, Direction secondSupport) {
        if (areDirections(firstSupport, secondSupport, Direction.NORTH, Direction.WEST)) return Direction.NORTH;
        if (areDirections(firstSupport, secondSupport, Direction.NORTH, Direction.EAST)) return Direction.EAST;
        if (areDirections(firstSupport, secondSupport, Direction.SOUTH, Direction.EAST)) return Direction.SOUTH;
        if (areDirections(firstSupport, secondSupport, Direction.SOUTH, Direction.WEST)) return Direction.WEST;
        return null;
    }

    private static boolean areDirections(Direction first, Direction second, Direction expectedFirst,
                                         Direction expectedSecond) {
        return first == expectedFirst && second == expectedSecond
                || first == expectedSecond && second == expectedFirst;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!level.isClientSide() && !canSurvive(state, level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    private static VoxelShape makeDiagonalShape(boolean positiveDiagonal) {
        VoxelShape shape = Shapes.empty();
        for (int segment = 0; segment < 8; segment++) {
            int x1 = segment * 2;
            int x2 = x1 + 2;
            int z1 = positiveDiagonal ? segment * 2 : 14 - segment * 2;
            int z2 = z1 + 2;
            shape = Shapes.or(shape, Block.box(x1, 6.5, z1, x2, 9.5, z2));
        }
        return shape;
    }
}
