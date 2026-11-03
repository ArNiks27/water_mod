package com.arniks.water_mod.block.watertower;

import com.arniks.water_mod.ModBlockEntities;
import com.arniks.water_mod.block.waterskin.WaterskinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterTowerBlockEntity extends BlockEntity implements GeoBlockEntity {
    public static final int REQUIRED_STROKES = 20;
    public static final int RUN_TICKS = 20 * 30;
    public static final int FRESH_DROP_TICKS = 20 * 10;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int strokes;
    private long activeUntil = -1;
    private long nextFreshDrop = -1;
    private int sandParticleTick;

    public WaterTowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WATER_TOWER.get(), pos, state);
    }

    public void pressLever(Player player) {
        if (level == null || level.isClientSide) return;
        WaterTowerMode mode = getBlockState().getValue(WaterTowerBlock.MODE);
        if (mode == WaterTowerMode.SPENT) return;
        if (mode != WaterTowerMode.IDLE && mode != WaterTowerMode.ONS) {
            return;
        }

        if (!player.getAbilities().instabuild) {
            FoodData food = player.getFoodData();
            int newFood = Math.max(0, food.getFoodLevel() - 1);
            food.setFoodLevel(newFood);
            food.setSaturation(Math.min(food.getSaturationLevel(), newFood));
        }

        strokes++;
        level.playSound(null, worldPosition, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.7f,
                level.getRandom().nextBoolean() ? 0.9f : 1.1f);

        if (mode == WaterTowerMode.ONS){
            setMode(WaterTowerMode.IDLE);
        } else if (mode == WaterTowerMode.IDLE) {
            setMode(WaterTowerMode.ONS);
        }

        if (strokes < REQUIRED_STROKES) {
            sync();
            return;
        }

        boolean salt = level.getRandom().nextBoolean();
        long now = level.getGameTime();
        activeUntil = now + RUN_TICKS;
        nextFreshDrop = now + FRESH_DROP_TICKS;
        setMode(salt ? WaterTowerMode.SALT : WaterTowerMode.FRESH);
        if (salt) fillSaltWaterskin(level);
        sync();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterTowerBlockEntity blockEntity) {
        WaterTowerMode mode = state.getValue(WaterTowerBlock.MODE);
        long now = level.getGameTime();

        if (mode == WaterTowerMode.SPENT) {
            if (++blockEntity.sandParticleTick >= 20) {
                blockEntity.sandParticleTick = 0;
                blockEntity.spawnSand(level, 4);
            }
            return;
        }
        if (mode != WaterTowerMode.SALT && mode != WaterTowerMode.FRESH) return;

        if (mode == WaterTowerMode.SALT) {
            blockEntity.fillSaltWaterskin(level);
        } else {
            while (blockEntity.nextFreshDrop >= 0
                    && now >= blockEntity.nextFreshDrop
                    && blockEntity.nextFreshDrop <= blockEntity.activeUntil) {
                blockEntity.fillFreshWaterskin(level);
                blockEntity.nextFreshDrop += FRESH_DROP_TICKS;
                blockEntity.setChanged();
            }
        }

        if (blockEntity.activeUntil >= 0 && now >= blockEntity.activeUntil) {
            blockEntity.activeUntil = -1;
            blockEntity.nextFreshDrop = -1;
            blockEntity.setMode(WaterTowerMode.SPENT);
            blockEntity.spawnSand(level, 24);
            blockEntity.sync();
        }
    }

    private void fillSaltWaterskin(Level level) {
        BlockPos skinPos = worldPosition.offset(0, 0, -1);
        BlockState skin = level.getBlockState(skinPos);
        if (!(skin.getBlock() instanceof WaterskinBlock)) return;
        int oldLevel = WaterskinBlock.getLevel(skin);
        if (oldLevel >= WaterskinBlock.MAX_LEVEL || (oldLevel > 0 && !WaterskinBlock.isSalty(skin))) return;
        WaterskinBlock.addLevel(level, skinPos, WaterskinBlock.MAX_LEVEL - oldLevel, true);
    }

    private void fillFreshWaterskin(Level level) {
        BlockPos skinPos = worldPosition.offset(0, 0, -1);
        BlockState skin = level.getBlockState(skinPos);
        if (!(skin.getBlock() instanceof WaterskinBlock)) return;
        if (WaterskinBlock.getLevel(skin) >= WaterskinBlock.MAX_LEVEL || WaterskinBlock.isSalty(skin)) return;
        WaterskinBlock.addLevel(level, skinPos, 1, false);
    }

    private void setMode(WaterTowerMode mode) {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        if (state.getValue(WaterTowerBlock.MODE) != mode) {
            level.setBlock(worldPosition, state.setValue(WaterTowerBlock.MODE, mode), Block.UPDATE_ALL);
        }
        setChanged();
    }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    private void spawnSand(Level level, int count) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        BlockParticleOption sand = new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState());
        serverLevel.sendParticles(sand, worldPosition.getX() + 0.5, worldPosition.getY() + 0.55,
                worldPosition.getZ() - 0.5, count, 0.16, 0.18, 0.16, 0.02);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<WaterTowerBlockEntity>(this, "pump", 0, state -> {
            WaterTowerMode mode = getBlockState().getValue(WaterTowerBlock.MODE);
            return mode == WaterTowerMode.FRESH || mode == WaterTowerMode.SALT
                    ? state.setAndContinue(RawAnimation.begin().thenLoop("still"))
                    : PlayState.STOP;
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, -1, -2), worldPosition.offset(3, 3, 3));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Strokes", strokes);
        tag.putLong("ActiveUntil", activeUntil);
        tag.putLong("NextFreshDrop", nextFreshDrop);
        tag.putInt("SandParticleTick", sandParticleTick);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        strokes = tag.getInt("Strokes");
        activeUntil = tag.contains("ActiveUntil") ? tag.getLong("ActiveUntil") : -1;
        nextFreshDrop = tag.contains("NextFreshDrop") ? tag.getLong("NextFreshDrop") : -1;
        sandParticleTick = tag.getInt("SandParticleTick");
    }
}