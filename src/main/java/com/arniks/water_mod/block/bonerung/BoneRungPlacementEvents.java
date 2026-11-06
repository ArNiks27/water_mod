package com.arniks.water_mod.block.bonerung;

import com.arniks.water_mod.ModBlocks;
import com.arniks.water_mod.Water_mod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Water_mod.MODID)
public class BoneRungPlacementEvents {

    @SubscribeEvent
    public static void onBoneUsedOnCorner(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(Items.BONE) || event.getFace().getAxis() == Direction.Axis.Y) return;
        if (event.getLevel().isClientSide || !(event.getLevel() instanceof ServerLevel level)) return;

        Direction clickedFace = event.getFace();
        BlockPos target = event.getPos().relative(clickedFace);
        if (!event.getEntity().mayInteract(level, target) || !level.getBlockState(target).canBeReplaced()) return;

        BlockPos clickedSupport = event.getPos();
        if (!level.getBlockState(clickedSupport).isFaceSturdy(level, clickedSupport, clickedFace)) return;

        Direction supportTowardClicked = clickedFace.getOpposite();
        for (Direction secondSupportDirection : Direction.Plane.HORIZONTAL) {
            if (secondSupportDirection.getAxis() == supportTowardClicked.getAxis()) continue;

            BlockPos secondSupport = target.relative(secondSupportDirection);
            if (!level.getBlockState(secondSupport).isFaceSturdy(level, secondSupport,
                    secondSupportDirection.getOpposite())) continue;

            boolean positiveDiagonal = supportTowardClicked.getStepX() * secondSupportDirection.getStepZ()
                    + supportTowardClicked.getStepZ() * secondSupportDirection.getStepX() < 0;
            BlockState rung = ModBlocks.BONE_RUNG.get().defaultBlockState()
                    .setValue(BoneRungBlock.DIAGONAL, positiveDiagonal);
            if (!rung.canSurvive(level, target)) continue;

            event.setCanceled(true);
            Player player = event.getEntity();
            if (level.isClientSide()) {
                player.swing(InteractionHand.MAIN_HAND);
            } else if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.swing(InteractionHand.MAIN_HAND, true);
                }
            if (level.setBlock(target, rung, 3) && !event.getEntity().getAbilities().instabuild) {
                event.getItemStack().shrink(1);
            }
            return;
        }
    }
}
