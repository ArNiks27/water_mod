package com.arniks.water_mod.client;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.block.desalinator.DesalinatorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DesalinatorRenderer extends GeoBlockRenderer<DesalinatorBlockEntity> {
    public DesalinatorRenderer() { super(new Model()); }

    public static class Model extends GeoModel<DesalinatorBlockEntity> {
        @Override public ResourceLocation getModelResource(DesalinatorBlockEntity a) { return new ResourceLocation(Water_mod.MODID, "geo/opresnitel.geo.json"); }
        @Override public ResourceLocation getTextureResource(DesalinatorBlockEntity a) { return new ResourceLocation(Water_mod.MODID, "textures/block/opresnitel.png"); }
        @Override public ResourceLocation getAnimationResource(DesalinatorBlockEntity a) { return new ResourceLocation(Water_mod.MODID, "animations/opresnitel.animation.json"); }
    }
    @Override
    public int getViewDistance() {
        return 512;
    }
}
