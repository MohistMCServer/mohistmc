package com.mohistmc.mod.api.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SimpleLabel extends PositionedWidget {

    @Nullable private static Font defaultFont;
    @Nullable public static Font getDefaultFont() { return defaultFont; }
    public static void setDefaultFont(@Nullable Font font) { defaultFont = font; }

    private Component text;
    private final int color;
    private float textScale = 1.0f;
    @Nullable private Font customFont;
    private boolean enableItemIcons;
    private List<Segment> segments;

    public SimpleLabel(int relX, int relY, Component text, int color) {
        super(relX, relY, 0, 0);
        this.text = text;
        this.color = color;
        autoSize();
    }

    public SimpleLabel setFont(@Nullable Font f) { customFont = f; return this; }
    public float getTextScale() { return textScale; }
    public SimpleLabel setTextScale(float s) { textScale = Math.max(0.1f, s); return this; }
    public SimpleLabel setEnableItemIcons(boolean v) { enableItemIcons = v; segments = null; autoSize(); return this; }

    public SimpleLabel setText(Component t) {
        text = t; segments = null; autoSize();
        return this;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Font font = customFont != null ? customFont : defaultFont != null ? defaultFont : Minecraft.getInstance().font;
        Font mcFont = Minecraft.getInstance().font;

        if (!enableItemIcons) {
            if (Float.compare(textScale, 1.0f) == 0) {
                graphics.text(font, text, getAbsoluteX(), getAbsoluteY(), color);
            } else { renderScaled(font, graphics); }
            return;
        }

        if (segments == null) parseSegments();
        int curX = getAbsoluteX();
        int curY = getAbsoluteY();
        int iconSz = mcFont.lineHeight;

        for (var seg : segments) {
            if (seg.type == SegmentType.TEXT) {
                graphics.text(font, Component.literal(seg.content), curX, curY, color);
                curX += font.width(seg.content);
            } else {
                try { graphics.item(seg.stack, curX, curY + (iconSz - 16) / 2); } catch (Exception e) {}
                curX += iconSz + 2;
            }
        }
    }

    private void renderScaled(Font font, GuiGraphicsExtractor g) {
        int x = getAbsoluteX(), y = getAbsoluteY();
        var pose = g.pose(); pose.pushMatrix();
        pose.translate(x, y); pose.scale(textScale, textScale); pose.translate(-x, -y);
        g.text(font, text, x, y, color);
        pose.popMatrix();
    }

    private void autoSize() {
        if (!enableItemIcons) {
            try { var f = Minecraft.getInstance().font; this.width = f.width(text); this.height = f.lineHeight; }
            catch (Exception e) { this.width = 60; this.height = 10; }
            return;
        }
        if (segments == null) parseSegments();
        int totalW = 0;
        var f = Minecraft.getInstance().font;
        int iconSz = f.lineHeight;
        for (var seg : segments) totalW += seg.type == SegmentType.TEXT ? f.width(seg.content) : (iconSz + 2);
        this.width = Math.max(totalW, 10);
        this.height = Math.max(f != null ? f.lineHeight : 10, iconSz);
    }

    private void parseSegments() {
        segments = new ArrayList<>();
        String raw = text.getString();
        int i = 0;
        while (i < raw.length()) {
            int pct = raw.indexOf('%', i);
            if (pct < 0 || pct == raw.length() - 1) {
                segments.add(new Segment(SegmentType.TEXT, raw.substring(i)));
                break;
            }
            if (pct > i) segments.add(new Segment(SegmentType.TEXT, raw.substring(i, pct)));

            int end = raw.indexOf('%', pct + 1);
            if (end < 0) {
                segments.add(new Segment(SegmentType.TEXT, raw.substring(pct)));
                break;
            }

            String idStr = raw.substring(pct + 1, end);
            try {
                var id = Identifier.parse(idStr);
                var opt = BuiltInRegistries.ITEM.get(id);
                if (opt.isPresent()) {
                    var item = opt.get().value();
                    var stack = new ItemStack(Holder.direct(item), 1);
                    stack.set(DataComponents.ITEM_MODEL, id);
                    segments.add(new Segment(SegmentType.ITEM, stack));
                } else {
                    segments.add(new Segment(SegmentType.TEXT, "%" + idStr + "%"));
                }
            } catch (Exception e) {
                segments.add(new Segment(SegmentType.TEXT, "%" + idStr + "%"));
            }
            i = end + 1;
        }
    }

    private enum SegmentType { TEXT, ITEM }

    private static class Segment {
        final SegmentType type;
        final String content;
        final ItemStack stack;
        Segment(SegmentType type, String content) { this.type = type; this.content = content; this.stack = ItemStack.EMPTY; }
        Segment(SegmentType type, ItemStack stack) { this.type = type; this.content = null; this.stack = stack; }
    }
}
