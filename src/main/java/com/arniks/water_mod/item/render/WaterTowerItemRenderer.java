package com.arniks.water_mod.item.render;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.item.WaterTowerItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WaterTowerItemRenderer extends GeoItemRenderer<WaterTowerItem> {
    public WaterTowerItemRenderer() {
        super(new GeoModel<WaterTowerItem>() {
            @Override public ResourceLocation getModelResource(WaterTowerItem item) {
                return new ResourceLocation(Water_mod.MODID, "geo/water_tower.geo.json");
            }
            @Override public ResourceLocation getTextureResource(WaterTowerItem item) {
                return new ResourceLocation(Water_mod.MODID, "textures/block/water_tower.png");
            }
            @Override public ResourceLocation getAnimationResource(WaterTowerItem item) {
                return new ResourceLocation(Water_mod.MODID, "animations/water_tower.animation.json");
            }
        });
    }

    @Override
    public RenderType getRenderType(WaterTowerItem item, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}