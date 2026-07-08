package com.mohistmc.mod.api.gui;

import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

/**
 * 字体工具 — 创建 Font 实例 / 用 Style 指定字体
 */
public class FontHelper {

    private static FontManager fontManager;
    private static Method getFontSetRaw;

    private static FontManager fontManager() {
        if (fontManager == null) {
            try {
                var f = Minecraft.class.getDeclaredField("fontManager");
                f.setAccessible(true);
                fontManager = (FontManager) f.get(Minecraft.getInstance());
                getFontSetRaw = FontManager.class.getDeclaredMethod("getFontSetRaw", Identifier.class);
                getFontSetRaw.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access FontManager", e);
            }
        }
        return fontManager;
    }

    private static FontSet getFontSet(Identifier fontId) {
        try {
            return (FontSet) getFontSetRaw.invoke(fontManager(), fontId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get font set: " + fontId, e);
        }
    }

    /**
     * 从字体标识符创建 Font 实例，可用于 setFont / setDefaultFont
     */
    public static Font createFont(Identifier fontId) {
        FontSet fontSet = getFontSet(fontId);
        return new Font(new SingleFontProvider(fontSet));
    }

    private record SingleFontProvider(FontSet fontSet) implements Font.Provider {
        @Override
        public GlyphSource glyphs(FontDescription description) {
            return fontSet.source(false);
        }

        @Override
        public EffectGlyph effect() {
            return fontSet.whiteGlyph();
        }
    }

    /** 给文字绑定字体（用 Style，不依赖 Font 实例） */
    public static MutableComponent withFont(Component text, Identifier fontId) {
        return text.copy().withStyle(Style.EMPTY.withFont(new FontDescription.Resource(fontId)));
    }

    public static MutableComponent literal(String text, Identifier fontId) {
        return Component.literal(text).withStyle(Style.EMPTY.withFont(new FontDescription.Resource(fontId)));
    }

    public static MutableComponent translatable(String key, Identifier fontId) {
        return Component.translatable(key).withStyle(Style.EMPTY.withFont(new FontDescription.Resource(fontId)));
    }
}
