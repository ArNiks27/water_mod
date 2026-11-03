package com.arniks.water_mod.block.waterskin;

import com.arniks.water_mod.Config;
import com.arniks.water_mod.ModItems;
import com.arniks.water_mod.block.wetsand.WetSandWaterBlock;
import com.arniks.water_mod.thirst.PlayerThirst;
import com.arniks.water_mod.thirst.ThirstCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterskinItem extends BlockItem {
    public static final String TAG_LEVEL = "Level";
    public static final String TAG_SALTY = "Salty";

    public WaterskinItem(Block block, Properties props) { super(block, props); }


    public static ItemStack create(int level, boolean salty) {
        ItemStack s = new ItemStack(ModItems.WATERSKIN.get());
        setContent(s, level, salty);
        return s;
    }

    public static int getLevel(ItemStack stack) {
        return stack.hasTag() ? Math.max(0, Math.min(WaterskinBlock.MAX_LEVEL, stack.getTag().getInt(TAG_LEVEL))) : 0;
    }

    public static boolean isSalty(ItemStack s) {
        return getLevel(s) > 0 && s.getTag().getBoolean(TAG_SALTY);
    }

    public static void setContent(ItemStack stack, int level, boolean salty) {
        int l = Math.max(0, Math.min(WaterskinBlock.MAX_LEVEL, level));
        if (l == 0) {
            if (stack.hasTag()) { stack.getTag().remove(TAG_LEVEL); stack.getTag().remove(TAG_SALTY); if (stack.getTag().isEmpty()) stack.setTag(null); }
            return;
        }
        stack.getOrCreateTag().putInt(TAG_LEVEL, l);
        stack.getOrCreateTag().putBoolean(TAG_SALTY, salty);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player != null && player.isSecondaryUseActive()) return super.useOn(ctx);
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (Config.FILL_FROM_WATER.get() && getLevel(stack) < WaterskinBlock.MAX_LEVEL) {
            BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = hit.getBlockPos();
                if (level.getFluidState(pos).is(FluidTags.WATER) && level.getFluidState(pos).isSource() && player.mayInteract(level, pos)) {
                    boolean salty = level.getBiome(pos).is(BiomeTags.IS_OCEAN);
                    int cur = getLevel(stack);
                    if (cur > 0 && isSalty(stack) != salty) {
                        if (!level.isClientSide) player.displayClientMessage(Component.translatable("message.water_mod.waterskin.mixed"), true);
                        return InteractionResultHolder.fail(stack);
                    }
                    if (!level.isClientSide) {
                        setContent(stack, cur + 3, salty);
                        if (level.getBlockState(pos).getBlock() instanceof WetSandWaterBlock) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1f, 1f);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
                }
            }
        }

        if (getLevel(stack) <= 0) return InteractionResultHolder.pass(stack);
        // пресную воду не тратим, если шкала полная; солёную пить можно всегда
        if (!isSalty(stack) && ThirstCapability.getCurrent(player) >= PlayerThirst.MAX) return InteractionResultHolder.fail(stack);

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
    @Override public int getUseDuration(ItemStack stack) { return 32; }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        int lvl = getLevel(stack);
        if (lvl <= 0) return stack;
        boolean salty = isSalty(stack);

        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            if (salty) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, Config.SALT_POISON_SECONDS.get() * 20, 0));
                ThirstCapability.get(player).ifPresent(thirst -> thirst.add(Config.DRINK_RESTORE.get().floatValue()));
            } else {
                ThirstCapability.get(player).ifPresent(thirst -> thirst.add(Config.DRINK_RESTORE.get().floatValue()));
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!(entity instanceof Player p && p.getAbilities().instabuild)) {
            setContent(stack, lvl - 1, salty);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        int l = getLevel(stack);
        if (l == 0) tip.add(Component.translatable("tooltip.water_mod.waterskin.empty"));
        else tip.add(Component.translatable(isSalty(stack) ? "tooltip.water_mod.waterskin.salt" : "tooltip.water_mod.waterskin.fresh", l));
    }
}
