package com.mohistmc.mod.client.gui;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * 自定义加载背景 — 继承 EnhancedScreen，可直接作为 Screen 使用，也可通过静态方法在混入中渲染
 */
public class LoadingBackgrounds extends EnhancedScreen {

    /** 全局实例（懒加载），供 mixin 调用 renderProgress — 当前 mixin 直接 new，暂不使用 */
    private static LoadingBackgrounds instance;
    public static LoadingBackgrounds getInstance() {
        LoadingBackgrounds i = instance;
        if (i == null) {
            synchronized (LoadingBackgrounds.class) {
                i = instance;
                if (i == null) {
                    i = new LoadingBackgrounds();
                    instance = i;
                }
            }
        }
        return i;
    }

    private static final List<Identifier> ICONS = new ArrayList<>();
    public static Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/gui/backgrounds/image.png");

    static {
        try {
            var resources = Minecraft.getInstance().getResourceManager();
            if (resources != null) {
                ICONS.addAll(resources.listResources("textures/ui/regions", location -> true).keySet());
            }
        } catch (Exception ignored) {
        }
    }

    public LoadingBackgrounds() {
        super(Component.translatable("narrator.screen.title"), BACKGROUND);
    }

    @Override
    protected void buildWidgets() {
        // 可通过 addWidget 添加自定义组件，当前使用 renderProgress 直接渲染
    }

    // ======== 静态工具 ========

    public static Identifier randomIcon() {
        if (ICONS.isEmpty()) return BACKGROUND;
        return ICONS.get(new Random().nextInt(ICONS.size()));
    }

    /** 绘制背景纹理（给其它 Screen 使用） */
    public static void draw(final GuiGraphicsExtractor context, final Screen screen) {
        drawBackgroundTexture(context, screen, BACKGROUND, 1.0f, 1.0f);
    }

    public static void drawBackgroundTexture(final GuiGraphicsExtractor context, final Screen screen,
                                              final Identifier texture, final float brightness, final float opacity) {
        final float textureWidth = 1917f;
        final float textureHeight = 1008f;
        final float screenWidth = (float) screen.width;
        final float screenHeight = (float) screen.height;
        float offsetX = 0.0f;
        float offsetY = 0.0f;
        float scaleX = screenWidth / textureWidth;
        float scaleY = screenHeight / textureHeight;
        if (scaleX < scaleY) {
            scaleX = scaleY;
            offsetX = 0.0f - (screenWidth - textureWidth * scaleX) * 0.5f;
        } else {
            scaleY = scaleX;
            offsetY = 0.0f - (screenHeight - textureHeight * scaleY) * 0.5f;
        }
        context.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, offsetX, offsetY,
                (int) screenWidth, (int) screenHeight,
                (int) (textureWidth * scaleX), (int) (textureHeight * scaleY));
    }

    // ======== 进度渲染（供 mixin 调用） ========

    public void renderProgress(final float progress, final GuiGraphicsExtractor graphics) {
        final int size = 24;
        final int gap = size / 3;
        final int halfSize = size / 2;
        final int yOffset = (graphics.guiHeight() - size - halfSize);

        renderProgressPyro(progress, graphics, size, centerX(graphics, halfSize, gap, size, 3), yOffset);
        renderProgressHydro(progress, graphics, size, centerX(graphics, halfSize, gap, size, 2), yOffset);
        renderProgressAnemo(progress, graphics, size, centerX(graphics, halfSize, gap, size, 1), yOffset);
        renderProgressElectro(progress, graphics, size, centerX(graphics, halfSize, gap, size, 0), yOffset);
        renderProgressDendro(progress, graphics, size, centerX(graphics, halfSize, gap, size, -1), yOffset);
        renderProgressCryo(progress, graphics, size, centerX(graphics, halfSize, gap, size, -2), yOffset);
        renderProgressGeo(progress, graphics, size, centerX(graphics, halfSize, gap, size, -3), yOffset);

        final Font font = Minecraft.getInstance().font;
        final int lineColor = 0xFFE3E3E3;
        final int dimensionColor = 0xFF8699A9;
        final String title = "江湖指南";
        final List<String> desc = List.of("满堂花醉三千客，一剑霜寒十四州");
        final int titleOffset = graphics.guiHeight() - 60 - 18 * desc.size();
        graphics.text(font, title, graphics.guiWidth() / 2 - font.width(title) / 2, titleOffset, dimensionColor);
        for (int i = 0; i < desc.size(); ++i) {
            final String line = desc.get(i);
            graphics.text(font, line, graphics.guiWidth() / 2 - font.width(line) / 2,
                    titleOffset + 15 + 6 * i + (i - 1) * 2, dimensionColor);
        }

        // 分隔线
        int leftEnd = centerX(graphics, halfSize, gap, size, 3) - gap;
        int rightStart = centerX(graphics, halfSize, gap, size, -3) + size + gap;
        graphics.fill(0, yOffset + halfSize - 2, leftEnd, yOffset + halfSize - 1, lineColor);
        graphics.fill(rightStart, yOffset + halfSize - 2, graphics.guiWidth(), yOffset + halfSize - 1, lineColor);
    }

    /** 计算第 index 个图标的 x 坐标（index: 0=中心, >0 向左, <0 向右） */
    private static int centerX(GuiGraphicsExtractor g, int halfSize, int gap, int size, int index) {
        if (index == 0) return g.guiWidth() / 2 - halfSize;
        boolean left = index > 0;
        int absIdx = Math.abs(index);
        return g.guiWidth() / 2 + (left ? -1 : 1) * (halfSize + gap * absIdx + size * (absIdx - 1) + size / 2) - halfSize;
    }

    // ======== 单个元素进度渲染 ========

    private void renderProgressPyro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "pyro", progress / 0.14f, size, x, y);
    }

    private void renderProgressHydro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "hydro", (progress - 0.14f) / 0.14f, size, x, y);
    }

    private void renderProgressAnemo(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "anemo", (progress - 0.24f) / 0.14f, size, x, y);
    }

    private void renderProgressElectro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "electro", (progress - 0.42f) / 0.14f, size, x, y);
    }

    private void renderProgressDendro(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "dendro", (progress - 0.56f) / 0.14f, size, x, y);
    }

    private void renderProgressCryo(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "cryo", (progress - 0.7f) / 0.14f, size, x, y);
    }

    private void renderProgressGeo(final float progress, final GuiGraphicsExtractor graphics, final int size, final int x, final int y) {
        blitProgress(graphics, "geo", (progress - 0.84f) / 0.14f, size, x, y);
    }

    private static void blitProgress(final GuiGraphicsExtractor graphics, final String name, final float fill, final int size, final int x, final int y) {
        var empty = Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/" + name + "_empty.png");
        var filled = Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/progress/" + name + ".png");
        graphics.blit(RenderPipelines.GUI_TEXTURED, empty, x, y, 0, 0, size, size, size, size);
        int fillW = (int) (size * Mth.clamp(fill, 0.0f, 1.0f));
        if (fillW > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, filled, x, y, 0, 0, fillW, size, size, size);
        }
    }
}
