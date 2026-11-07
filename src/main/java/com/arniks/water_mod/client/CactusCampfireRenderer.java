package com.arniks.water_mod.client;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.block.cactuscampfire.CactusCampfireBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CactusCampfireRenderer extends GeoBlockRenderer<CactusCampfireBlockEntity> {
    public CactusCampfireRenderer() {
        super(new Model());
    }

    @Override
    public void renderFinal(PoseStack poseStack, CactusCampfireBlockEntity campfire, BakedGeoModel model,
                            MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                            int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(poseStack, campfire, model, bufferSource, buffer, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);

        Direction facing = campfire.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> items = campfire.getItems();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int seed = (int) campfire.getBlockPos().asLong();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();
            poseStack.translate(0.5F, 0.33F, 0.5F);
            Direction itemDirection = Direction.from2DDataValue((i + facing.get2DDataValue()) % 4);
            poseStack.mulPose(Axis.YP.rotationDegrees(-itemDirection.toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(-0.136F, -0.136F, 0.0F);
            poseStack.scale(0.375F, 0.375F, 0.375F);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, bufferSource, campfire.getLevel(), seed + i);
            poseStack.popPose();
        }
    }

    public static class Model extends GeoModel<CactusCampfireBlockEntity> {
        @Override
        public ResourceLocation getModelResource(CactusCampfireBlockEntity entity) {
            return new ResourceLocation(Water_mod.MODID, "geo/cactus_campfire.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(CactusCampfireBlockEntity entity) {
            return new ResourceLocation(Water_mod.MODID, "textures/block/cactus_campfire.png");
        }

        @Override
        public ResourceLocation getAnimationResource(CactusCampfireBlockEntity entity) {
            return new ResourceLocation(Water_mod.MODID, "animations/cactus_campfire.animation.json");
        }
    }
    @Override
    public int getViewDistance() {
        return 512;
    }
}
