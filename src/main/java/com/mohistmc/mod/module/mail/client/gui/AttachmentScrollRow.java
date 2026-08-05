package com.mohistmc.mod.module.mail.client.gui;

import com.mohistmc.mod.api.gui.ScrollableWidget;
import com.mohistmc.mod.module.mail.common.MailEntry;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 附件物品横向滚动条 — 继承 {@link ScrollableWidget}（水平方向）：
 * 滚轮横滚、滚动条点击跳转、长按拖拽、平滑滚动全部由基类提供，零手写滚动代码。
 * <p>本类只负责图标布局：物品图标（描边）+ xN 计数 + 已领取对勾；
 * 视口宽度对齐整步（22px），滚动任意位置起点不漂移。
 * <p>组件背景透明（bgColor=0），由 MailScreen 平级放置于详情面板底部附件区；
 * 「无附件」提示与分隔线由 DetailPanel 绘制。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class AttachmentScrollRow extends ScrollableWidget {

    private static final int ICON = 16;
    private static final int STEP = 22;
    private static final int PAD = 10;

    private MailEntry mail;

    public AttachmentScrollRow(int relX, int relY, int width, int height) {
        super(relX, relY, width, height);
        this.bgColor = 0x00000000; // 透明背景，盖在详情面板上
        setDirection(Direction.HORIZONTAL);
        setContentPadding(PAD);
        setScrollStep(STEP); // 滚轮每格滚动一个整步，保持图标对齐
    }

    /** 切换目标邮件（null = 无选中），滚动归零 */
    public void setMail(MailEntry mail) {
        this.mail = mail;
        scrollOffset = 0;
        targetScrollOffset = 0;
    }

    @Override
    public int getContentSize() {
        List<ItemStack> attachments = mail == null ? List.of() : mail.getAttachments();
        return attachments.size() * STEP;
    }

    /** 视口宽度对齐整步（内容步进与视口同模，滚动位置恒为整步倍数） */
    @Override
    protected int getScrollViewportSize() {
        return super.getScrollViewportSize() / STEP * STEP;
    }

    /** 最大偏移基于对齐后的视口，滚到底时起点仍对齐视口左缘（左边距恒定） */
    @Override
    public int getMaxScroll() {
        return Math.max(0, getContentSize() - getScrollViewportSize());
    }

    @Override
    protected boolean handleContentClick(MouseButtonEvent event, int bx, int by, int vw, int vh) {
        return false; // 附件区无点击行为
    }

    private static Font font() {
        return Minecraft.getInstance().font;
    }

    @Override
    protected void renderItems(GuiGraphicsExtractor g, int x, int y, int vw, int vh, int alpha) {
        int a = alpha & 0xFF000000;
        List<ItemStack> attachments = mail == null ? List.of() : mail.getAttachments();
        int iconY = y + (vh - ICON) / 2;
        int left = x + getContentPadding();
        int right = x + vw - getContentPadding();
        int curX = left - scrollOffset;
        for (int i = 0; i < attachments.size(); i++) {
            if (curX + ICON < left) { // 图标右缘仍在视口左外
                curX += STEP;
                continue;
            }
            if (curX + ICON > right) break; // 图标右缘越过视口右缘，整图标完整显示
            ItemStack stack = attachments.get(i);
            // 物品边框（1px 描边，不填充背景）
            int bx = curX - 1, by = iconY - 1;
            g.fill(bx, by, bx + 18, by + 1, a | 0xFF555566);
            g.fill(bx, by + 17, bx + 18, by + 18, a | 0xFF555566);
            g.fill(bx, by, bx + 1, by + 18, a | 0xFF555566);
            g.fill(bx + 17, by, bx + 18, by + 18, a | 0xFF555566);
            g.item(stack, curX, iconY);
            if (stack.getCount() > 1) {
                g.text(font(), Component.literal("x" + stack.getCount()), curX + 11, iconY + 9, a | 0xFFAAAAAA);
            }
            // 已领取：图标右上角绿色对勾（物品保留展示）
            if (mail.isClaimed()) {
                g.text(font(), Component.literal("✓"), curX + 10, iconY - 2, a | 0xFF4CAF50);
            }
            curX += STEP;
        }
    }
}
