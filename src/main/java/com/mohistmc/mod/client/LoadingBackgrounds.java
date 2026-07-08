package com.mohistmc.mod.client;

import com.mohistmc.mod.MohistMC;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class LoadingBackgrounds {

    private static final List<Identifier> ICONS = new ArrayList<Identifier>();
    public static Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/gui/backgrounds/image.png");

    static {
        final Collection<Identifier> newIcons = Minecraft.getInstance().getResourceManager().listResources("textures/ui/regions", location -> true).keySet();
        ICONS.clear();
        ICONS.addAll(newIcons);
    }

    public static Identifier randomIcon() {
        return ICONS.get(new Random().nextInt(ICONS.size()));
    }

    public static void draw(final GuiGraphicsExtractor context, final Screen screen) {
        drawBackgroundTexture(context, screen, BACKGROUND, 1.0f, 1.0f);
    }

    private static void drawBackgroundTexture(final GuiGraphicsExtractor context, final Screen screen, final Identifier texture, final float brightness, final float opacity) {
        final float textureWidth = 1917f;
        final float textureHeight = 1008f;
        final float screenWidth = (float) screen.width;
        final float screenHeight = (float) screen.height;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float scaleX;
        float scaleY;
        scaleX = screenWidth / textureWidth;
        scaleY = screenHeight / textureHeight;
        if (scaleX < scaleY) {
            scaleX = scaleY;
            offsetX = 0.0f - (screenWidth - textureWidth * scaleX) * 0.5f;
        } else {
            scaleY = scaleX;
            offsetY = 0.0f - (screenHeight - textureHeight * scaleY) * 0.5f;
        }
        context.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, offsetX, offsetY, (int) screenWidth, (int) screenHeight, (int) (textureWidth * scaleX), (int) (textureHeight * scaleY));
    }

    public void renderProgress(final float progress, final GuiGraphicsExtractor graphics) {
        final int size = 24;
        final int gap = size / 3;
        final int halfSize = size / 2;
        final int yOffset = (graphics.guiHeight() - size - halfSize);
        this.renderProgressPyro(progress, graphics, size, graphics.guiWidth() / 2 - halfSize - gap - size - gap - size - gap - size, yOffset);
        this.renderProgressHydro(progress, graphics, size, graphics.guiWidth() / 2 - halfSize - gap - size - gap - size, yOffset);
        this.renderProgressAnemo(progress, graphics, size, graphics.guiWidth() / 2 - halfSize - gap - size, yOffset);
        this.renderProgressElectro(progress, graphics, size, graphics.guiWidth() / 2 - halfSize, yOffset);
        this.renderProgressDendro(progress, graphics, size, graphics.guiWidth() / 2 + halfSize + gap, yOffset);
        this.renderProgressCryo(progress, graphics, size, graphics.guiWidth() / 2 + halfSize + gap + size + gap, yOffset);
        this.renderProgressGeo(progress, graphics, size, graphics.guiWidth() / 2 + halfSize + gap + size + gap + size + gap, yOffset);
        final Font font = Minecraft.getInstance().font;
        final int lineColor = 0xFFE3E3E3;
        final int dimensionColor = 0xFF8699A9;
        //graphics.blit(LoadingBackgrounds.randomIcon(), graphics.guiWidth() / 2 - 48, graphics.guiHeight() / 2 - 72, 96, 96, 0, 0, 512, 512, 512, 512);
        final String title = "江湖指南";
        final List<String> desc = List.of("满堂花醉三千客，一剑霜寒十四州");
        final int titleOffset = graphics.guiHeight() - 35 - 18 * desc.size();
        graphics.text(font, title, graphics.guiWidth() / 2 - font.width(title) / 2, titleOffset, dimensionColor);
        for (int i = 0; i < desc.size(); ++i) {
            final String line = desc.get(i);
            graphics.text(font, line, graphics.guiWidth() / 2 - font.width(line) / 2, titleOffset + 15 + 6 * i + (i - 1) * 2, dimensionColor);
        }
        graphics.fill(0, yOffset + halfSize - 2, graphics.guiWidth() / 2 - halfSize - gap - size - gap - size - gap - size - size, yOffset + halfSize - 1, lineColor);
        graphics.fill(graphics.guiWidth() / 2 + halfSize + gap + size + gap + size + gap + size + size, yOffset + halfSize - 2, graphics.guiWidth(), yOffset + halfSize - 1, lineColor);
    }

    private void renderProgressPyro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/pyro" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/pyro.png"), x, y, 0, 0, (int) (size * Mth.clamp(progress / 0.14f, 0.0f, 1.0f)), size, size, size);
    }

    private void renderProgressHydro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/hydro" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/hydro.png"), x, y, 0, 0, (int) (size * Mth.clamp((progress - 0.14f) / 0.14f, 0.0f, 1.0f)), size, size, size);
    }

    private void renderProgressAnemo(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/anemo" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/anemo.png"), x, y, 0, 0, (int) (size * Mth.clamp((progress - 0.24f) / 0.14f, 0.0f, 1.0f)), size, size, size);
    }

    private void renderProgressElectro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/electro" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/electro.png"), x, y, 0, 0, (int) (size * Mth.clamp((progress - 0.42f) / 0.14f, 0.0f, 1.0f)), size, size, size);
    }

    private void renderProgressDendro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/dendro" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/dendro.png"), x, y, 0, 0, (int) (size * Mth.clamp((progress - 0.56f) / 0.14f, 0.0f, 1.0f)), size, size, size);
    }

    private void renderProgressCryo(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/cryo" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/cryo.png"), x, y, 0, 0, (int) (size * Mth.clamp((progress - 0.7f) / 0.14f, 0.0f, 1.0f)), size, size, size);
    }

    private void renderProgressGeo(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/geo" + ("_empty") + ".png"), x, y, 0, 0, size, size, size, size);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/geo.png"), x, y, 0, 0, (int) (size * Mth.clamp((progress - 0.84f) / 0.14f, 0.0f, 1.0f)), size, size, size);
    }
}
