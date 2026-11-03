package com.arniks.water_mod.block.desalinator;

import com.arniks.water_mod.ModBlockEntities;
import com.arniks.water_mod.block.waterskin.WaterskinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
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

public class DesalinatorBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long lastDay = -1;
    private long fillAt = -1;

    public DesalinatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DESALINATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DesalinatorBlockEntity be) {
        long time = level.getDayTime();

        if (be.fillAt >= 0) {
            if (time < be.fillAt - 176) { be.fillAt = -1; be.setChanged(); }
            else {
                if (time >= be.fillAt) {
                    be.fillAt = -1;
                    if (be.canFill(level, pos)) WaterskinBlock.addLevel(level, pos.below(), 1, false);
                    be.setChanged();
                }
                return;
            }
        }
        long timeOfDay = Math.floorMod(time, 24000L);
        boolean dawn = timeOfDay >= 23000L;
        if (!dawn) return;
        long dawnDay = Math.floorDiv(time + 1000L, 24000L);
        if (be.lastDay == dawnDay) return;
        if (!be.canFill(level, pos)) return;

        be.lastDay = dawnDay;
        be.fillAt = time + 176;
        be.setChanged();
        be.triggerAnim("dew", "work");
    }

    private boolean canFill(Level level, BlockPos pos) {
        BlockState s = level.getBlockState(pos.below());
        return s.getBlock() instanceof WaterskinBlock
                && WaterskinBlock.getLevel(s) < WaterskinBlock.MAX_LEVEL
                && !WaterskinBlock.isSalty(s)
                && level.canSeeSky(pos.above());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "dew", 0, state -> PlayState.STOP)
                .triggerableAnim("work", RawAnimation.begin().thenPlay("still")));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, -2, -2), worldPosition.offset(3, 2, 3));
    }

    @Override protected void saveAdditional(CompoundTag tag) { super.saveAdditional(tag); tag.putLong("LastDay", lastDay); tag.putLong("FillAt", fillAt); }
    @Override public void load(CompoundTag tag) { super.load(tag); lastDay = tag.contains("LastDay") ? tag.getLong("LastDay") : -1; fillAt = tag.contains("FillAt") ? tag.getLong("FillAt") : -1; }
}
