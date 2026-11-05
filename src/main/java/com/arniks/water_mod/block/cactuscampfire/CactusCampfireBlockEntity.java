package com.arniks.water_mod.block.cactuscampfire;

import com.arniks.water_mod.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CactusCampfireBlockEntity extends CampfireBlockEntity implements GeoBlockEntity {
    public static final int LIFETIME_TICKS = 20 * 40;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long expiresAt = -1;

    public CactusCampfireBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.CACTUS_CAMPFIRE.get();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CactusCampfireBlockEntity blockEntity) {
        long gameTime = level.getGameTime();
        if (blockEntity.expiresAt < 0) {
            blockEntity.expiresAt = gameTime + LIFETIME_TICKS;
            blockEntity.setChanged();
        }

        if (gameTime >= blockEntity.expiresAt) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        if (state.getValue(CampfireBlock.LIT)) {
            CampfireBlockEntity.cookTick(level, pos, state, blockEntity);
        } else {
            CampfireBlockEntity.cooldownTick(level, pos, state, blockEntity);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CactusCampfireBlockEntity blockEntity) {
        CampfireBlockEntity.particleTick(level, pos, state, blockEntity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "cactus_campfire", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong("ExpiresAt", expiresAt);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        expiresAt = tag.contains("ExpiresAt") ? tag.getLong("ExpiresAt") : -1;
    }
}
