package com.mohistmc.mod.module.mail.client.gui;

import com.mohistmc.mod.api.gui.PositionedWidget;
import com.mohistmc.mod.module.mail.common.network.payload.AttachmentSelection;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 发信附件选择的背包网格（9×4 主背包槽）：
 * 点击物品整堆加入附件（再次点击移除），选中槽位绿色高亮，悬停显示物品名。
 * <p>点击由 {@link MailComposeScreen#mouseClicked} 转发给 {@link #handleClick}。
 * 附件只允许选主背包 0-35 槽（服务端同步校验）。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class ComposeGrid extends PositionedWidget {

    private static final int MARGIN = 4;
    private static final int COLS = 9;
    private static final int ROWS = 4;

    private final int slot;
    private final List<AttachmentSelection> selections = new ArrayList<>();
    /** 选中变化回调（刷新已选统计等） */
    private Runnable onChange = () -> {};

    public ComposeGrid(int relX, int relY) {
        this(relX, relY, 22);
    }

    /** @param slot 槽尺寸（窄窗口时缩小，保证网格不超出内容区） */
    public ComposeGrid(int relX, int relY, int slot) {
        super(relX, relY, COLS * slot + MARGIN * 2, ROWS * slot + MARGIN * 2);
        this.slot = Math.max(16, slot);
    }

    public int getSlot() {
        return slot;
    }

    public ComposeGrid setOnChange(Runnable onChange) {
        this.onChange = onChange;
        return this;
    }

    public List<AttachmentSelection> getSelections() {
        return selections;
    }

    public void clear() {
        selections.clear();
        onChange.run();
    }

    private boolean isSelected(int slot) {
        for (AttachmentSelection sel : selections) {
            if (sel.slot() == slot) return true;
        }
        return false;
    }

    /** 点击处理：槽内物品非空时整堆加入/移除，返回是否消费点击 */
    public boolean handleClick(int mx, int my) {
        int x0 = getAbsoluteX();
        int y0 = getAbsoluteY();
        if (mx < x0 || mx >= x0 + width || my < y0 || my >= y0 + height) return false;
        int col = (mx - x0 - MARGIN) / slot;
        int row = (my - y0 - MARGIN) / slot;
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return false;
        int slot = row * COLS + col;
        if (items().get(slot).isEmpty()) return true; // 空槽：消费点击但无可选
        toggle(slot);
        return true;
    }

    private void toggle(int slot) {
        for (int i = 0; i < selections.size(); i++) {
            if (selections.get(i).slot() == slot) {
                selections.remove(i);
                onChange.run();
                return;
            }
        }
        selections.add(new AttachmentSelection(slot, items().get(slot).copy()));
        onChange.run();
    }

    @Override
    public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x0 = getAbsoluteX();
        int y0 = getAbsoluteY();
        g.fill(x0, y0, x0 + width, y0 + height, 0x66000000);

        int iconOff = (slot - 16) / 2;
        for (int i = 0; i < items().size(); i++) {
            int sx = x0 + MARGIN + (i % COLS) * slot;
            int sy = y0 + MARGIN + (i / COLS) * slot;
            boolean hover = mouseX >= sx && mouseX < sx + slot && mouseY >= sy && mouseY < sy + slot;
            boolean sel = isSelected(i);

            // 槽底色：选中绿 > 悬停提亮 > 默认
            int bg = sel ? 0x664CAF50 : (hover ? 0x33FFFFFF : 0x44222222);
            g.fill(sx, sy, sx + slot, sy + slot, bg);

            ItemStack stack = items().get(i);
            if (stack.isEmpty()) continue;
            // 物品边框（1px 描边，不填充背景）
            int bx = sx + iconOff - 1, by = sy + iconOff - 1;
            g.fill(bx, by, bx + 18, by + 1, 0xFF555566);
            g.fill(bx, by + 17, bx + 18, by + 18, 0xFF555566);
            g.fill(bx, by, bx + 1, by + 18, 0xFF555566);
            g.fill(bx + 17, by, bx + 18, by + 18, 0xFF555566);
            g.item(stack, sx + iconOff, sy + iconOff);
            if (stack.getCount() > 1) {
                g.text(font(), Component.literal("x" + stack.getCount()),
                        sx + slot - 11, sy + slot - 11, 0xFFAAAAAA);
            }
            if (hover) {
                g.setTooltipForNextFrame(stack.getHoverName(), mouseX, mouseY);
            }
        }
    }

    private static List<ItemStack> items() {
        var player = Minecraft.getInstance().player;
        return player == null ? List.of() : player.getInventory().getNonEquipmentItems();
    }

    private static Font font() {
        return Minecraft.getInstance().font;
    }
}
