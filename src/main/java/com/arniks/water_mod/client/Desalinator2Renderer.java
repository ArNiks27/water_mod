package com.arniks.water_mod.client;

import com.arniks.water_mod.Water_mod;
import com.arniks.water_mod.block.desalinator2.Desalinator2Block;
import com.arniks.water_mod.block.desalinator2.Desalinator2BlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class Desalinator2Renderer extends GeoBlockRenderer<Desalinator2BlockEntity> {
    public Desalinator2Renderer() { super(new Model()); }

    @Override
    public RenderType getRenderType(Desalinator2BlockEntity be, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    public static class Model extends GeoModel<Desalinator2BlockEntity> {
        @Override public ResourceLocation getModelResource(Desalinator2BlockEntity be) {
            String geo = be.getBlockState().getValue(Desalinator2Block.CONTENT).geoName;
            return new ResourceLocation(Water_mod.MODID, "geo/" + geo + ".geo.json");
        }
        @Override public ResourceLocation getTextureResource(Desalinator2BlockEntity be) {
            return new ResourceLocation(Water_mod.MODID, "textures/block/opresnitel_vip.png");
        }
        @Override public ResourceLocation getAnimationResource(Desalinator2BlockEntity be) {
            return new ResourceLocation(Water_mod.MODID, "animations/opresnitel_vip.animation.json");
        }
    }
}