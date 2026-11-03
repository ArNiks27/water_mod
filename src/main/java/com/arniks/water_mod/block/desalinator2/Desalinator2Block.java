package com.arniks.water_mod.block.desalinator2;

import com.arniks.water_mod.ModBlockEntities;
import com.arniks.water_mod.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class Desalinator2Block extends BaseEntityBlock {
    public static final EnumProperty<Desalinator2Content> CONTENT = EnumProperty.create("content", Desalinator2Content.class);
    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public Desalinator2Block() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5f).sound(SoundType.WOOD).noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(CONTENT, Desalinator2Content.EMPTY).setValue(WORKING, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENT, WORKING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos support = pos.below();
        return level.getBlockState(support).isFaceSturdy(level, support, Direction.UP);
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Desalinator2BlockEntity(pos, state);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.DESALINATOR_VIP.get(), Desalinator2BlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof Desalinator2BlockEntity be)) return InteractionResult.PASS;
        Desalinator2BlockEntity.Action action = be.decide(player.getItemInHand(hand));
        if (action == Desalinator2BlockEntity.Action.NONE) return InteractionResult.PASS;
        if (!level.isClientSide) be.perform(action, player, hand);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof Desalinator2BlockEntity be) {
            ItemStack legacySkin = be.takePendingLegacySkin();
            if (!legacySkin.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, legacySkin);
            }
            switch (state.getValue(CONTENT)) {
                case CACTUS -> Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                        new ItemStack(net.minecraft.world.item.Items.CACTUS, Desalinator2BlockEntity.CACTUS_COUNT));
                case DRY_CACTUS -> Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                        new ItemStack(ModItems.DRY_CACTUS.get(), Desalinator2BlockEntity.CACTUS_COUNT));
                default -> { }
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}