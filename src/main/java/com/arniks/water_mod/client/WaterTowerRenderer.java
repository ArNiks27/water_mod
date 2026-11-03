package com.arniks.water_mod.client;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.block.watertower.WaterTowerBlock;
import com.arniks.water_mod.block.watertower.WaterTowerBlockEntity;
import com.arniks.water_mod.block.watertower.WaterTowerMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WaterTowerRenderer extends GeoBlockRenderer<WaterTowerBlockEntity> {
    public WaterTowerRenderer() { super(new Model()); }
    //ai
    @Override
    public RenderType getRenderType(WaterTowerBlockEntity entity, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    public static class Model extends GeoModel<WaterTowerBlockEntity> {
        private String modelName(WaterTowerBlockEntity entity) {
            return switch (entity.getBlockState().getValue(WaterTowerBlock.MODE)) {
                case ONS -> "water_tower_on";
                case FRESH -> "water_tower_on_water";
                case SALT -> "water_tower_on_salt";
                default -> "water_tower";
            };
        }

        @Override public ResourceLocation getModelResource(WaterTowerBlockEntity entity) {
            return new ResourceLocation(Water_mod.MODID, "geo/" + modelName(entity) + ".geo.json");
        }

        @Override public ResourceLocation getTextureResource(WaterTowerBlockEntity entity) {
            String texture = switch (entity.getBlockState().getValue(WaterTowerBlock.MODE)) {
                case ONS -> "water_tower.png";
                case FRESH -> "water_tower_water.png";
                case SALT -> "water_tower_salt.png";
                default -> "water_tower.png";
            };
            return new ResourceLocation(Water_mod.MODID, "textures/block/" + texture);
        }

        @Override public ResourceLocation getAnimationResource(WaterTowerBlockEntity entity) {
            WaterTowerMode mode = entity.getBlockState().getValue(WaterTowerBlock.MODE);
            String animation = switch (mode) {
                case ONS -> "water_tower.animation.json";
                case FRESH -> "water_tower_liquid_still.json";
                case SALT -> "water_tower_liquid_still_salt.json";
                default -> "water_tower.animation.json";
            };
            return new ResourceLocation(Water_mod.MODID, "animations/" + animation);
        }
    }
}