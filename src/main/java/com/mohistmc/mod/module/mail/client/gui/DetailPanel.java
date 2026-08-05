package com.mohistmc.mod.module.mail.client.gui;

import com.mohistmc.mod.api.gui.PositionedWidget;
import com.mohistmc.mod.module.mail.common.MailEntry;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 邮箱右侧详情面板：发送者 + 时间（顶部）+ 正文（自动换行，可滚轮滚动）
 * + 底部附件区（物品横向滚动条由 {@link AttachmentScrollRow} 提供，本面板只画分隔线与无附件提示）。
 * <p>无选中邮件时居中显示「点击左侧邮件查看详情」。
 * 正文滚轮由 {@link MailScreen#mouseScrolled} 转发给 {@link #handleScroll}；
 * 附件区滚轮/拖拽由 AttachmentScrollRow（ScrollableWidget 基类）自动处理。
 * 附件区同行靠右的「领取」按钮由 MailScreen 负责（不绘制在面板内）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class DetailPanel extends PositionedWidget {

    private static final int PAD = 10;
    private static final int HEADER_H = 30;      // 发送者 + 时间 区域高度
    private static final int ATTACH_H = 30;      // 底部附件区高度（含分隔线，与 AttachmentScrollRow 等高）
    private static final int LINE_GAP = 2;       // 正文行间距
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private MailEntry mail;
    private List<String> lines = List.of();
    private int scrollY;

    public DetailPanel(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
    }

    /** 切换详情目标（null = 无选中），重置滚动与换行缓存 */
    public void setMail(MailEntry mail) {
        this.mail = mail;
        this.lines = List.of();
        this.scrollY = 0;
    }

    /** 正文滚轮滚动（由 MailScreen 转发）；附件区不消费，归 AttachmentScrollRow 处理 */
    public boolean handleScroll(double mouseX, double mouseY, double delta) {
        if (mail == null || !isMouseOver(mouseX, mouseY)) return false;
        if (mouseY >= getAbsoluteY() + height - ATTACH_H) return false; // 附件区
        int max = getMaxScroll();
        if (max <= 0) return false;
        scrollY = Math.max(0, Math.min(max, scrollY - (int) (delta * 18)));
        return true;
    }

    /** 正文视口高度（面板高度 - 头部 - 底部附件区 - 下边距） */
    private int textViewH() {
        return height - HEADER_H - ATTACH_H - PAD;
    }

    private int getMaxScroll() {
        return Math.max(0, lines.size() * (font().lineHeight + LINE_GAP) - textViewH());
    }

    private static Font font() {
        return Minecraft.getInstance().font;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();

        // 背景 + 1px 边框（与列表一致观感）
        g.fill(x, y, x + width, y + height, 0x66000000);
        g.fill(x, y, x + width, y + 1, 0xFF333344);
        g.fill(x, y + height - 1, x + width, y + height, 0xFF333344);
        g.fill(x, y, x + 1, y + height, 0xFF333344);
        g.fill(x + width - 1, y, x + width, y + height, 0xFF333344);

        if (mail == null) {
            Component hint = Component.translatable("gui.mohistmc.mail.select_hint");
            g.text(font(), hint, x + (width - font().width(hint)) / 2,
                    y + (height - font().lineHeight) / 2, 0xFF888888);
            return;
        }

        // —— 头部：发送者（左）+ 时间（右）——
        Component sender = Component.translatable("gui.mohistmc.mail.sender", mail.getSenderName());
        g.text(font(), sender, x + PAD, y + 8, 0xFFFFFFFF);
        String time = TIME_FORMAT.format(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(mail.getTimestamp()), ZoneId.systemDefault()));
        Component timeText = Component.translatable("gui.mohistmc.mail.time", time);
        g.text(font(), timeText, x + width - font().width(timeText.getString()) - PAD, y + 8, 0xFF888888);
        g.fill(x + PAD, y + HEADER_H - 1, x + width - PAD, y + HEADER_H, 0x33FFFFFF); // 分隔线

        // —— 正文：自动换行 + 滚动 ——
        int textY = y + HEADER_H;
        int textH = textViewH();
        int lineH = font().lineHeight + LINE_GAP;
        if (textH > 0) {
            if (lines.isEmpty() && mail.getText().isBlank()) {
                g.text(font(), Component.translatable("gui.mohistmc.mail.detail.empty"), x + PAD, textY, 0xFF888888);
            } else {
                if (lines.isEmpty()) {
                    lines = wrapText(mail.getText(), width - PAD * 2);
                }
                for (int i = 0; i < lines.size(); i++) {
                    int rowY = textY - scrollY + i * lineH;
                    if (rowY + font().lineHeight <= textY) continue;      // 上溢跳过
                    if (rowY >= textY + textH) break;                     // 下溢终止
                    g.text(font(), Component.literal(lines.get(i)), x + PAD, rowY, 0xFFCCCCCC);
                }
            }

            // 正文超出时画右侧滚动条（3px，避开底部附件区）
            int max = getMaxScroll();
            if (max > 0 && !lines.isEmpty()) {
                float ratio = (float) textH / (lines.size() * lineH);
                int sbH = Math.max(8, (int) (textH * ratio));
                int track = textH - sbH;
                int sbY = textY + (scrollY > 0 ? (int) ((float) scrollY / max * track) : 0);
                g.fill(x + width - 4, sbY, x + width - 1, sbY + sbH, 0x55FFFFFF);
            }
        }

        // —— 底部：附件区（物品由 AttachmentScrollRow 绘制，本面板只画分隔线与无附件提示）——
        int attachY = y + height - ATTACH_H;
        g.fill(x + PAD, attachY, x + width - PAD, attachY + 1, 0x33FFFFFF); // 分隔线
        if (mail.getAttachments().isEmpty()) {
            Component none = Component.translatable("gui.mohistmc.mail.no_attachment");
            g.text(font(), none, x + PAD, attachY + (ATTACH_H - font().lineHeight) / 2 + 1, 0xFF888888);
        }
    }

    /** 按宽度逐行截断（plainSubstrByWidth 不会断词乱码） */
    private List<String> wrapText(String text, int maxWidth) {
        List<String> out = new ArrayList<>();
        String remaining = text;
        while (!remaining.isEmpty()) {
            String line = font().plainSubstrByWidth(remaining, maxWidth);
            if (line.isEmpty()) break; // 防御：极端窄宽下防死循环
            out.add(line);
            remaining = remaining.substring(line.length());
        }
        return out;
    }
}
