package com.arniks.water_mod.block.desalinator2;

import com.arniks.water_mod.ModBlockEntities;
import com.arniks.water_mod.ModItems;
import com.arniks.water_mod.block.hotsand.HotSandBlock;
import com.arniks.water_mod.block.waterskin.WaterskinBlock;
import com.arniks.water_mod.block.waterskin.WaterskinItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Desalinator2BlockEntity extends BlockEntity implements GeoBlockEntity {
    public static final int TICKS_PER_LEVEL = 800;
    public static final int HEATED_TICKS_PER_LEVEL = 176;
    public static final int CACTUS_COUNT = 3;
    public static final int OUTPUT_PER_RUN = 3;

    public enum Action { NONE, POUR_SALT, INSERT_CACTUS, TAKE_DRY }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int progress;
    private int producedThisRun;
    private ItemStack pendingLegacySkin = ItemStack.EMPTY;

    public Desalinator2BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DESALINATOR_VIP.get(), pos, state);
    }

    public ItemStack takePendingLegacySkin() {
        ItemStack stack = pendingLegacySkin;
        pendingLegacySkin = ItemStack.EMPTY;
        return stack;
    }

    public Action decide(ItemStack held) {
        Desalinator2Content content = getBlockState().getValue(Desalinator2Block.CONTENT);
        boolean isWaterskinItem = held.getItem() instanceof WaterskinItem;
        if (content == Desalinator2Content.EMPTY) {
            if (isWaterskinItem && WaterskinItem.isSalty(held)
                    && WaterskinItem.getLevel(held) == WaterskinItem.getCapacity(held)) return Action.POUR_SALT;
            if (held.is(Items.CACTUS) && held.getCount() >= CACTUS_COUNT) return Action.INSERT_CACTUS;
        }
        if (held.isEmpty() && content == Desalinator2Content.DRY_CACTUS) return Action.TAKE_DRY;
        return Action.NONE;
    }

    public void perform(Action action, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean creative = player.getAbilities().instabuild;
        switch (action) {
            case POUR_SALT -> {
                if (!creative) WaterskinItem.setContent(held, 0, true);
                setContent(Desalinator2Content.SALT_WATER);
                level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
            }
            case INSERT_CACTUS -> {
                if (!creative) held.shrink(CACTUS_COUNT);
                setContent(Desalinator2Content.CACTUS);
            }
            case TAKE_DRY -> {
                give(player, new ItemStack(ModItems.DRY_CACTUS.get(), CACTUS_COUNT));
                setContent(Desalinator2Content.EMPTY);
            }
            default -> { }
        }
        sync();
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    private void setContent(Desalinator2Content content) {
        if (getBlockState().getValue(Desalinator2Block.CONTENT) != content) producedThisRun = 0;
        level.setBlock(worldPosition, getBlockState()
                .setValue(Desalinator2Block.CONTENT, content)
                .setValue(Desalinator2Block.WORKING, false), 3);
    }

    private boolean canWork(Level level, BlockState state) {
        Desalinator2Content content = state.getValue(Desalinator2Block.CONTENT);
        BlockState waterskin = level.getBlockState(worldPosition.west());
        boolean heated = HotSandBlock.isHot(level.getBlockState(worldPosition.below()));
        return (content == Desalinator2Content.SALT_WATER || content == Desalinator2Content.CACTUS)
                && waterskin.getBlock() instanceof WaterskinBlock
                && WaterskinBlock.getLevel(waterskin) < WaterskinBlock.getCapacity(waterskin)
                && !WaterskinBlock.isSalty(waterskin)
                && (level.isDay() || heated);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, Desalinator2BlockEntity be) {
        ItemStack legacySkin = be.takePendingLegacySkin();
        if (!legacySkin.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, legacySkin);
            be.setChanged();
        }

        boolean active = be.canWork(level, state);
        if (active != state.getValue(Desalinator2Block.WORKING)) {
            state = state.setValue(Desalinator2Block.WORKING, active);
            level.setBlock(pos, state, 3);
        }
        if (!active) return;
        int interval = HotSandBlock.isHot(level.getBlockState(pos.below()))
                ? HEATED_TICKS_PER_LEVEL : TICKS_PER_LEVEL;
        if (++be.progress < interval) return;
        be.progress = 0;

        BlockPos waterskinPos = pos.west();
        WaterskinBlock.addLevel(level, waterskinPos, 1, false);
        be.producedThisRun++;
        BlockState filledSkin = level.getBlockState(waterskinPos);
        if (WaterskinBlock.getLevel(filledSkin) >= WaterskinBlock.getCapacity(filledSkin)
                || be.producedThisRun >= OUTPUT_PER_RUN) {
            Desalinator2Content next = state.getValue(Desalinator2Block.CONTENT) == Desalinator2Content.SALT_WATER
                    ? Desalinator2Content.EMPTY : Desalinator2Content.DRY_CACTUS;
            be.setContent(next);
        }
        be.sync();
    }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<Desalinator2BlockEntity>(this, "work", 0, state ->
                getBlockState().getValue(Desalinator2Block.WORKING)
                        ? state.setAndContinue(RawAnimation.begin().thenLoop("still"))
                        : PlayState.STOP));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, 0, -2), worldPosition.offset(3, 2, 3));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Progress", progress);
        tag.putInt("ProducedThisRun", producedThisRun);
        if (!pendingLegacySkin.isEmpty()) tag.put("Skin", pendingLegacySkin.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("Progress");
        producedThisRun = tag.getInt("ProducedThisRun");
        pendingLegacySkin = tag.contains("Skin") ? ItemStack.of(tag.getCompound("Skin")) : ItemStack.EMPTY;
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
