package com.mohistmc.mod.module.mail.client.gui;

import com.mohistmc.mod.api.gui.ScrollListItem;
import com.mohistmc.mod.module.mail.common.MailEntry;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 邮箱左侧标题列表项（ScrollList 单列条目，高 26px 含 2px 视距间隔，支持选中高亮）：
 * 第一行未读/已读标识 + 标题（正文首行，截断），第二行时间（左对齐）。
 * <p>布局（w 为条目渲染宽度，背景只画上 24px，底部 2px 留作卡片间距）：
 * <pre>
 * 左侧 2px  : 未读红 / 已读灰状态条（整列）
 * y+3       : [未读|已读] 标识 …… 标题（未读白 / 已读灰，截断）
 * y+14      : 时间（灰，左对齐）
 * </pre>
 * 选中时整行高亮；点击整行 → selectAction（右侧详情切换）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class MailCard extends ScrollListItem {

    /** 条目总高（含底部视距间隔） */
    private static final int CARD_HEIGHT = 26;
    /** 背景可视区高度，底部余量作为与下一卡片的间隔 */
    private static final int CONTENT_H = 24;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MailEntry mail;
    /** 点击条目回调（切换右侧详情） */
    private final Consumer<MailEntry> selectAction;
    private boolean selected;

    public MailCard(MailEntry mail, Consumer<MailEntry> selectAction) {
        this.mail = mail;
        this.selectAction = selectAction;
        setHeight(CARD_HEIGHT);
    }

    public MailEntry getMail() {
        return mail;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** 自绘悬停高亮（仅内容区），关闭 ScrollList 全高覆盖层以免加深底部间距区 */
    @Override
    public boolean renderHoverOverlay() {
        return false;
    }

    @Override
    public boolean handleClick(int rx, int ry, int w) {
        selectAction.accept(mail);
        return true;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int a = alpha & 0xFF000000; // 取 alpha 通道

        // 背景（仅上 CONTENT_H，底部 4px 间距区一律不加深）：选中高亮 > 悬停提亮 > 默认
        int bg = selected ? 0xFF3A3A55 : (hovered ? 0xFF2E2E40 : 0xFF242430);
        g.fill(x, y, x + w, y + CONTENT_H, a | bg);
        // 左侧状态条：未读红 / 已读灰（同样止于间距区）
        g.fill(x, y, x + 2, y + CONTENT_H, a | (mail.isRead() ? 0xFF555566 : 0xFFFF5555));

        // 第一行：已读/未读标识 + 附件符号（📎）+ 标题（不再给时间预留宽度，可显示更多标题）
        Component tag = Component.translatable(mail.isRead() ? "gui.mohistmc.mail.read" : "gui.mohistmc.mail.unread");
        int tagX = x + 10;
        g.text(font(), tag, tagX, y + 3, a | (mail.isRead() ? 0xFF666677 : 0xFFFF5555));
        int titleX = tagX + font().width(tag) + 6;
        if (!mail.getAttachments().isEmpty()) { // 有附件：标题前显示回形针符号
            Component attach = Component.literal("📎"); // 📎
            g.text(font(), attach, titleX, y + 3, a | 0xFFAAAAAA);
            titleX += font().width(attach) + 4;
        }
        String title = firstLine(mail.getText());
        String trimmed = font().plainSubstrByWidth(title, Math.max(20, w - (titleX - x) - 12));
        g.text(font(), Component.literal(trimmed), titleX, y + 3,
                a | (mail.isRead() ? 0xFFCCCCCC : 0xFFFFFFFF));

        // 第二行：时间（左对齐，灰字）
        String time = TIME_FORMAT.format(
                LocalDateTime.ofInstant(Instant.ofEpochMilli(mail.getTimestamp()), ZoneId.systemDefault()));
        g.text(font(), Component.literal(time), x + 10, y + 14, a | 0xFF777777);
    }

    /** 标题取正文第一行，空白时回退为「（无正文）」 */
    private static String firstLine(String text) {
        if (text == null || text.isBlank()) {
            return Component.translatable("gui.mohistmc.mail.detail.empty").getString();
        }
        int nl = text.indexOf('\n');
        return nl < 0 ? text : text.substring(0, nl);
    }
}
