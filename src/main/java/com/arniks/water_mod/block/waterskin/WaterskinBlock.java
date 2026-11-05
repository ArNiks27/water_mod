package com.arniks.water_mod.block.waterskin;

import com.arniks.water_mod.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class WaterskinBlock extends Block {
    public static final int MAX_LEVEL = 3;
    public static final int IMPROVED_MAX_LEVEL = 10;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, IMPROVED_MAX_LEVEL);
    public static final BooleanProperty SALTY = BooleanProperty.create("salty");
    public static final BooleanProperty IMPROVED = BooleanProperty.create("improved");

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 5, 13),
            Block.box(4, 5, 4, 12, 8, 12));

    public WaterskinBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f)
                .sound(SoundType.WOOL).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 0).setValue(SALTY, false).setValue(IMPROVED, false));
    }
    public static BlockState withContent(BlockState state, int level, boolean salty) {
        int l = Math.max(0, Math.min(getCapacity(state), level));
        return state.setValue(LEVEL, l).setValue(SALTY, l > 0 && salty);
    }

    // добавить левел к бурдюку +1
    public static int addLevel(Level world, BlockPos pos, int amount, boolean salty) {
        BlockState s = world.getBlockState(pos);
        if (!(s.getBlock() instanceof WaterskinBlock)) return 0;
        int old = s.getValue(LEVEL);
        int now = Math.max(0, Math.min(getCapacity(s), old + amount));
        boolean type = old == 0 ? salty : s.getValue(SALTY);
        world.setBlock(pos, withContent(s, now, type), Block.UPDATE_ALL);
        return now - old;
    }

    public static int getLevel(BlockState s) {
        return s.getValue(LEVEL);
    }
    public static boolean isSalty(BlockState s) {
        return s.getValue(SALTY);
    }
    public static int getCapacity(BlockState s) {
        return s.getValue(IMPROVED) ? IMPROVED_MAX_LEVEL : MAX_LEVEL;
    }

    private static ItemStack stackOf(BlockState s) {
        return WaterskinItem.create(s.getValue(IMPROVED)
                        ? ModItems.WATERSKIN_IMPROVED.get()
                        : ModItems.WATERSKIN.get(),
                s.getValue(LEVEL), s.getValue(SALTY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, SALTY, IMPROVED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        return withContent(defaultBlockState().setValue(IMPROVED, WaterskinItem.isImproved(stack)),
                WaterskinItem.getLevel(stack), WaterskinItem.isSalty(stack));
    }

    @Override
    public VoxelShape getShape(BlockState s, BlockGetter w, BlockPos p, CollisionContext c) { return SHAPE; }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState nb, LevelAccessor level, BlockPos pos, BlockPos nbPos) {
        if (dir == Direction.DOWN && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, dir, nb, level, pos, nbPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(stackOf(state));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return stackOf(state);
    }
}
