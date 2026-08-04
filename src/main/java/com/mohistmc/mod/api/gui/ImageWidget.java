package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * 图片组件 — 仅显示 PNG 纹理，无按钮/点击/悬停/tooltip 功能。
 *
 * <pre>
 *   // 基本用法
 *   new ImageWidget(0, 0, 64, 64, Identifier.of("mohistmc", "textures/ui/icon.png"));
 *
 *   // 链式
 *   new ImageWidget(0, 0, 64, 64).setTexture(Identifier.of("..."));
 * </pre>
 */
public class ImageWidget extends PositionedWidget {

    @Nullable
    private Identifier texture;
    private int tintColor = 0xFFFFFFFF; // ARGB 染色，默认白色（原样显示）
    /** 贴图源边长（>0 时按组件尺寸等比缩放显示完整贴图；<=0 时采样=输出） */
    private int srcSize;

    public ImageWidget(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
    }

    public ImageWidget(int relX, int relY, int width, int height, Identifier texture) {
        super(relX, relY, width, height);
        this.texture = texture;
    }

    /** 设置要显示的纹理 */
    public ImageWidget setTexture(@Nullable Identifier tex) {
        this.texture = tex;
        return this;
    }

    /**
     * 设置染色颜色（ARGB），默认 0xFFFFFFFF 原样显示。
     * 可用来做透明度叠加或颜色滤镜。
     */
    public ImageWidget setTint(int argb) {
        this.tintColor = argb;
        return this;
    }

    /**
     * 设置贴图源边长（方形贴图）：>0 时完整贴图按组件尺寸等比缩放显示（如 32px 图标缩小到 16px 组件），
     * 否则默认采样=输出（显示贴图左上角对应区域）。
     */
    public ImageWidget setTextureSrcSize(int srcSize) {
        this.srcSize = Math.max(0, srcSize);
        return this;
    }

    @Override
    public ImageWidget setAlpha(int alpha) {
        super.setAlpha(alpha);
        return this;
    }

    @Override
    public ImageWidget setAlpha(float alpha) {
        super.setAlpha(alpha);
        return this;
    }

    @Override
    public ImageWidget setRightAnchored(boolean anchored) {
        super.setRightAnchored(anchored);
        return this;
    }

    @Override
    public ImageWidget setBottomAnchored(boolean anchored) {
        super.setBottomAnchored(anchored);
        return this;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (texture == null) return;

        int x = getAbsoluteX();
        int y = getAbsoluteY();

        // 应用透明度染色：用纯色垫底模拟整体透明度
        int finalTint = applyAlpha(tintColor);
        int a = (finalTint >>> 24) & 0xFF;
        if (a == 0) return; // 完全透明，跳过渲染

        if (a < 255) {
            // 半透明时用透明黑垫底，近似整体 fade 效果
            graphics.fill(x, y, x + width, y + height, (a << 24) | 0x000000);
        }

        // 渲染纹理（Minecraft blit 不支持直接 alpha 混合，靠 blend mode 处理）
        if (srcSize > 0 && srcSize != width) {
            // 完整贴图按组件尺寸等比缩放（pose 缩放：平移到目标原点 → 缩放 → 回退）
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate(x, y);
            pose.scale(width / (float) srcSize, height / (float) srcSize);
            pose.translate(-x, -y);
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, srcSize, srcSize, srcSize, srcSize);
            pose.popMatrix();
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture,
                    x, y, 0, 0, width, height, width, height);
        }
    }
}
