package com.arniks.water_mod.item.render;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.client.DesalinatorRenderer;
import com.arniks.water_mod.item.displayDesalinatorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class displayDesalinatorItemRender extends GeoItemRenderer<displayDesalinatorItem> {
    public displayDesalinatorItemRender() {
        super(new GeoModel<displayDesalinatorItem>() {
            @Override
            public ResourceLocation getModelResource(displayDesalinatorItem a) {
                return new ResourceLocation(Water_mod.MODID, "geo/opresnitel.geo.json");
            }
            @Override
            public ResourceLocation getTextureResource(displayDesalinatorItem a) {
                return new ResourceLocation(Water_mod.MODID, "textures/block/opresnitel.png");
            }
            @Override
            public ResourceLocation getAnimationResource(displayDesalinatorItem a) {
                return new ResourceLocation(Water_mod.MODID, "animations/opresnitel.animation.json");
            }
        });
    }

    @Override
    public RenderType getRenderType(displayDesalinatorItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
