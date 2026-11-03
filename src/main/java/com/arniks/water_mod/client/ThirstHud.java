package com.arniks.water_mod.client;

import com.arniks.water_mod.Water_mod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ThirstHud {
    private static final ResourceLocation ICONS = new ResourceLocation(Water_mod.MODID, "textures/gui/thirst_icons.png");
    private static final int TEX_W = 25, TEX_H = 9;

    public static final IGuiOverlay OVERLAY = (ForgeGui gui, GuiGraphics g, float partialTick, int width, int height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) return;

        int units = Mth.ceil(ClientThirstData.get());
        int y = height - gui.rightHeight;
        gui.rightHeight += 10;

        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        for (int i = 0; i < 10; i++) {
            int x = left - (i + 1) * 8;
            int yy = y;
            if (units <= 4 && mc.player.tickCount % (units * 3 + 1) == 0) yy += mc.player.getRandom().nextInt(3) - 1;
            int u;
            if (units >= i * 2 + 2) u = 16;
            else if (units == i * 2 + 1) u = 8;
            else u = 0;
            g.blit(ICONS, x, yy, (float) u, 0f, 8, 9, TEX_W, TEX_H);
        }
        RenderSystem.disableBlend();
    };
}
