package com.arniks.water_mod.mixin;

import com.arniks.water_mod.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void water_mod$wetSandHydratesFarmland(LevelReader level, BlockPos farmlandPos,
                                                          CallbackInfoReturnable<Boolean> cir) {
        for (BlockPos pos : BlockPos.betweenClosed(
                farmlandPos.offset(-4, 0, -4), farmlandPos.offset(4, 1, 4))) {
            if (level.getBlockState(pos).is(ModBlocks.WET_SAND.get())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
