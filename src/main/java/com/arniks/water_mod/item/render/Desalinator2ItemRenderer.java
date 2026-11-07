package com.arniks.water_mod.item.render;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.item.Desalinator2Item;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class Desalinator2ItemRenderer extends GeoItemRenderer<Desalinator2Item> {
    public Desalinator2ItemRenderer() {
        super(new GeoModel<Desalinator2Item>() {
            @Override public ResourceLocation getModelResource(Desalinator2Item item) {
                return new ResourceLocation(Water_mod.MODID, "geo/opresnitel_vip.geo.json");
            }
            @Override public ResourceLocation getTextureResource(Desalinator2Item item) {
                return new ResourceLocation(Water_mod.MODID, "textures/block/opresnitel_vip.png");
            }
            @Override public ResourceLocation getAnimationResource(Desalinator2Item item) {
                return new ResourceLocation(Water_mod.MODID, "animations/opresnitel_vip.animation.json");
            }

        });
    }

    @Override
    public RenderType getRenderType(Desalinator2Item item, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}