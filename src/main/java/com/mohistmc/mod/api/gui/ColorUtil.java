package com.mohistmc.mod.api.gui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 颜色工具类，提供 hex → ARGB int 转换
 */
@OnlyIn(Dist.CLIENT)
public final class ColorUtil {

    private ColorUtil() {}

    /**
     * 将 hex 颜色字符串转为 ARGB int
     * <pre>
     *   "#9d2933"   → 0xFF9D2933  (不透明)
     *   "#CC9d2933" → 0xCC9D2933  (带 alpha)
     *   "9d2933"    → 0xFF9D2933  (无 # 前缀)
     *   "0xFF9D2933"→ 0xFF9D2933  (0x 前缀)
     * </pre>
     *
     * @param hex 颜色字符串，支持 #RRGGBB / #AARRGGBB / RRGGBB / AARRGGBB
     * @return ARGB int
     * @throws IllegalArgumentException 格式不合法时
     */
    public static int fromHex(String hex) {
        if (hex == null || hex.isBlank()) {
            throw new IllegalArgumentException("hex must not be null or blank");
        }
        String raw = hex.strip();
        // 去掉 # 或 0x 前缀
        if (raw.startsWith("#")) {
            raw = raw.substring(1);
        } else if (raw.toLowerCase().startsWith("0x")) {
            raw = raw.substring(2);
        }

        int len = raw.length();
        if (len != 6 && len != 8) {
            throw new IllegalArgumentException("hex must be 6 (RRGGBB) or 8 (AARRGGBB) digits, got: " + hex);
        }

        int value;
        try {
            value = Integer.parseUnsignedInt(raw, 16);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid hex color: " + hex, e);
        }

        // RRGGBB → 补全 alpha 为 0xFF
        if (len == 6) {
            value = 0xFF_00_00_00 | value;
        }
        return value;
    }

    /**
     * 从 RGB 分量创建不透明 ARGB int
     * @param r 红 (0-255)
     * @param g 绿 (0-255)
     * @param b 蓝 (0-255)
     */
    public static int rgb(int r, int g, int b) {
        return argb(255, r, g, b);
    }

    /**
     * 从 ARGB 分量创建 ARGB int
     * @param a 透明度 (0-255)
     * @param r 红 (0-255)
     * @param g 绿 (0-255)
     * @param b 蓝 (0-255)
     */
    public static int argb(int a, int r, int g, int b) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    /**
     * 提取 alpha 通道 (0-255)
     */
    public static int alpha(int color) {
        return color >>> 24;
    }

    /**
     * 替换颜色的 alpha 通道
     * @param color 原 ARGB 颜色
     * @param alpha 新 alpha 值 (0-255)
     * @return 新 ARGB 颜色
     */
    public static int withAlpha(int color, int alpha) {
        return (alpha & 0xFF) << 24 | (color & 0xFFFFFF);
    }

    /**
     * 将颜色的 alpha 乘以一个系数
     * @param color 原 ARGB 颜色
     * @param factor 乘系数 (0.0 ~ 1.0)
     * @return 新 ARGB 颜色
     */
    public static int mulAlpha(int color, float factor) {
        int a = (int) ((color >>> 24) * factor);
        return (a << 24) | (color & 0xFFFFFF);
    }
}
