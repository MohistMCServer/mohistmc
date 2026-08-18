package com.mohistmc.mod.api.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * 物品选择器模态对话框 — 支持两种模式：
 * <ul>
 *   <li>全部物品：搜索所有注册物品（仅 ID）</li>
 *   <li>玩家背包：直接选择背包内物品（支持 NBT）</li>
 * </ul>
 * <p>继承 {@link Modal} 确保渲染在最顶层并阻挡底层交互。
 */
@OnlyIn(Dist.CLIENT)
public class ItemPickerModal extends Modal {

    private static final int DIALOG_W = 380;
    private static final int DIALOG_H = 340;
    private static final int TITLE_H = 20;
    private static final int MODE_H = 18;
    private static final int SEARCH_H = 18;
    private static final int PADDING = 8;
    private static final int GRID_COLS = 16;

    private enum Mode { ALL_ITEMS, INVENTORY }

    private final List<ItemEntry> allItems = new ArrayList<>();
    private final List<ItemEntry> filteredItems = new ArrayList<>();
    private final BiConsumer<String, ItemStack> onSelect;
    private final Runnable onClose;

    private Mode mode = Mode.ALL_ITEMS;
    private EditBox searchBox;
    private GridScrollList itemGrid;

    /** 模式切换按钮位置 */
    private int modeAllBtnX, modeInvBtnX, modeBtnY, modeBtnW;

    private int dialogX, dialogY;

    public ItemPickerModal(BiConsumer<String, ItemStack> onSelect, Runnable onClose) {
        super(Component.empty(), Component.empty());
        this.onSelect = onSelect;
        this.onClose = onClose;
        hide();

        // 加载所有注册物品
        for (var item : BuiltInRegistries.ITEM) {
            if (item == null) continue;
            var id = BuiltInRegistries.ITEM.getKey(item).toString();
            var stack = new ItemStack(item, 1);
            allItems.add(new ItemEntry(id, stack));
        }
        allItems.sort(Comparator.comparing(e -> e.id));
        filteredItems.addAll(allItems);
    }

    // ======== 显示控制 ========

    @Override
    public void show() {
        super.show();
        if (searchBox != null) {
            searchBox.setValue("");
            searchBox.setFocused(true);
        }
        loadItemsForMode();
        filterItems("");
        rebuildGrid();
    }

    @Override
    public void hide() {
        super.hide();
        if (searchBox != null) {
            searchBox.setFocused(false);
        }
    }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public ItemPickerModal setBackdropColor(int color) { this.backdropColor = color; return this; }
    @Override
    public ItemPickerModal setCloseOnBackdrop(boolean close) { this.closeOnBackdrop = close; return this; }

    // ======== 模式切换 ========

    private void switchMode(Mode newMode) {
        if (this.mode == newMode) return;
        this.mode = newMode;
        if (searchBox != null) {
            searchBox.setValue("");
        }
        loadItemsForMode();
        filterItems("");
        rebuildGrid();
    }

    private void loadItemsForMode() {
        allItems.clear();
        if (mode == Mode.ALL_ITEMS) {
            for (var item : BuiltInRegistries.ITEM) {
                if (item == null) continue;
                var id = BuiltInRegistries.ITEM.getKey(item).toString();
                var stack = new ItemStack(item, 1);
                allItems.add(new ItemEntry(id, stack));
            }
            allItems.sort(Comparator.comparing(e -> e.id));
        } else {
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                Inventory inv = mc.player.getInventory();
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    ItemStack stack = inv.getItem(i);
                    if (!stack.isEmpty()) {
                        var id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                        allItems.add(new ItemEntry(id, stack.copy()));
                    }
                }
            }
        }
    }

    // ======== 滚动/拖拽 ========

    @Override
    public boolean handleScroll(double mouseX, double mouseY, double delta) {
        if (itemGrid == null) return false;
        return itemGrid.handleScroll(mouseX, mouseY, delta);
    }

    @Override
    public void handleDrag(double mouseX, double mouseY) {
        if (itemGrid != null) itemGrid.handleDrag((int) mouseX, (int) mouseY);
    }

    @Override
    public void handleRelease() {
        if (itemGrid != null) itemGrid.handleRelease();
    }

    public EditBox getSearchBox() { return searchBox; }

    // ======== 筛选 & 重建网格 ========

    private void filterItems(String query) {
        filteredItems.clear();
        String q = query.toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            filteredItems.addAll(allItems);
        } else {
            for (var entry : allItems) {
                if (entry.id.contains(q) ||
                        entry.stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(q)) {
                    filteredItems.add(entry);
                }
            }
        }
    }

    private void rebuildGrid() {
        if (itemGrid == null) return;
        itemGrid.clearItems();
        for (var entry : filteredItems) {
            itemGrid.addItem(new ItemGridItem(entry, () -> {
                if (onSelect != null) onSelect.accept(entry.id, entry.stack.copy());
                hide();
            }));
        }
    }

    // ======== 位置更新 ========

    @Override
    public void setScreenPos(int left, int top, int contentWidth, int contentHeight) {
        super.setScreenPos(left, top, contentWidth, contentHeight);
        dialogX = left + (contentWidth - DIALOG_W) / 2;
        dialogY = top + Math.max(20, (contentHeight - DIALOG_H) / 2);

        // 模式切换按钮
        int modeY = dialogY + TITLE_H + 2;
        modeBtnY = modeY;
        modeBtnW = (DIALOG_W - PADDING * 2 - 2) / 2;
        modeAllBtnX = dialogX + PADDING;
        modeInvBtnX = modeAllBtnX + modeBtnW + 2;

        // 搜索框
        int searchX = dialogX + PADDING;
        int searchY = modeY + MODE_H + 4;
        int searchW = DIALOG_W - PADDING * 2;
        if (searchBox == null) {
            var mc = Minecraft.getInstance();
            searchBox = new EditBox(mc.font, searchX, searchY, searchW, SEARCH_H, Component.literal(""));
            searchBox.setMaxLength(64);
            searchBox.setHint(Component.translatable("gui.mohistmc.shop.admin.search_item"));
            searchBox.setBordered(true);
            searchBox.setResponder(text -> {
                filterItems(text);
                rebuildGrid();
            });
        } else {
            searchBox.setX(searchX);
            searchBox.setY(searchY);
            searchBox.setWidth(searchW);
        }

        // 物品网格
        int gridX = dialogX + PADDING;
        int gridY = searchY + SEARCH_H + 6;
        int gridW = DIALOG_W - PADDING * 2;
        int gridH = dialogY + DIALOG_H - PADDING - gridY;
        if (itemGrid == null) {
            itemGrid = new GridScrollList(gridX, gridY, gridW, gridH, 0x44000000);
        } else {
            itemGrid.updateLayout(gridX, gridY, gridW, gridH);
        }
        itemGrid.setColumns(GRID_COLS).setGap(2, 2).setPadding(4).setSquareCells(true).setScrollStep(22);
        rebuildGrid();
    }

    // ======== 点击处理 ========

    @Override
    public boolean handleClick(MouseButtonEvent event, boolean doubleClick) {
        if (!visible) return false;
        int mx = logicalX(event);
        int my = logicalY(event);

        // 点击对话框内部
        if (mx >= dialogX && mx < dialogX + DIALOG_W && my >= dialogY && my < dialogY + DIALOG_H) {
            // 模式切换按钮
            if (my >= modeBtnY && my < modeBtnY + MODE_H) {
                if (mx >= modeAllBtnX && mx < modeAllBtnX + modeBtnW) {
                    switchMode(Mode.ALL_ITEMS);
                    return true;
                }
                if (mx >= modeInvBtnX && mx < modeInvBtnX + modeBtnW) {
                    switchMode(Mode.INVENTORY);
                    return true;
                }
            }
            // 搜索框
            if (searchBox != null) {
                searchBox.mouseClicked(event, doubleClick);
            }
            // 物品网格
            if (itemGrid != null) {
                itemGrid.handleClick(event, doubleClick);
            }
            return true;
        }

        // 点击遮罩 → 关闭
        if (closeOnBackdrop) {
            hide();
            if (onClose != null) onClose.run();
            return true;
        }
        return false;
    }

    // ======== 渲染 ========

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        // 遮罩层
        graphics.fill(screenLeft, screenTop, screenLeft + contentWidth, screenTop + contentHeight, applyAlpha(backdropColor));

        // 对话框背景
        int dbg = applyAlpha(0xFF2D2D2D);
        graphics.fill(dialogX, dialogY, dialogX + DIALOG_W, dialogY + DIALOG_H, dbg);
        // 边框
        int bc = applyAlpha(0xFF888888);
        graphics.fill(dialogX, dialogY, dialogX + DIALOG_W, dialogY + 1, bc);
        graphics.fill(dialogX, dialogY + DIALOG_H - 1, dialogX + DIALOG_W, dialogY + DIALOG_H, bc);
        graphics.fill(dialogX, dialogY, dialogX + 1, dialogY + DIALOG_H, bc);
        graphics.fill(dialogX + DIALOG_W - 1, dialogY, dialogX + DIALOG_W, dialogY + DIALOG_H, bc);

        var font = Minecraft.getInstance().font;

        // 标题
        var title = Component.translatable("gui.mohistmc.shop.admin.select_item");
        graphics.text(font, title, dialogX + (DIALOG_W - font.width(title)) / 2, dialogY + 6, applyAlpha(0xFFFFFFFF));

        // 模式切换按钮
        renderModeButton(graphics, modeAllBtnX, modeBtnY, modeBtnW, "全部物品", mode == Mode.ALL_ITEMS, font);
        renderModeButton(graphics, modeInvBtnX, modeBtnY, modeBtnW, "玩家背包", mode == Mode.INVENTORY, font);

        // 搜索框
        if (searchBox != null) {
            searchBox.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        // 物品网格
        if (itemGrid != null) {
            itemGrid.render(graphics, mouseX, mouseY, partialTick);
            int idx = itemGrid.getHoveredIndex();
            if (idx >= 0 && idx < itemGrid.getItems().size()) {
                var item = itemGrid.getItems().get(idx);
                if (item instanceof ItemGridItem gridItem) {
                    graphics.setTooltipForNextFrame(font, gridItem.entry.stack, mouseX, mouseY);
                }
            }
        }
    }

    private void renderModeButton(GuiGraphicsExtractor g, int x, int y, int w, String text, boolean active, net.minecraft.client.gui.Font font) {
        int bgColor = active ? 0xFF4CAF50 : 0xFF333344;
        int textColor = active ? 0xFFFFFFFF : 0xFFCCCCCC;
        g.fill(x, y, x + w, y + MODE_H, applyAlpha(bgColor));
        g.text(font, Component.literal(text), x + (w - font.width(text)) / 2, y + (MODE_H - font.lineHeight) / 2, applyAlpha(textColor));
    }

    // ======== 内部数据结构 ========

    private static class ItemEntry {
        final String id;
        final ItemStack stack;

        ItemEntry(String id, ItemStack stack) {
            this.id = id;
            this.stack = stack;
        }
    }

    // ======== 网格卡片项 ========

    private static class ItemGridItem extends ScrollListItem {
        private static final int CELL = 20;

        private final ItemEntry entry;
        private final Runnable onClick;

        ItemGridItem(ItemEntry entry, Runnable onClick) {
            this.entry = entry;
            this.onClick = onClick;
            setHeight(CELL);
        }

        @Override
        public boolean handleClick(int rx, int ry, int w) {
            onClick.run();
            return true;
        }

        @Override
        public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
            int a = alpha & 0xFF000000;
            g.fill(x, y, x + w, y + CELL, a | (hovered ? 0xFF555577 : 0xFF333344));
            if (hovered) {
                g.fill(x, y, x + w, y + CELL, a | 0x22FFFFFF);
            }
            g.item(entry.stack, x + (w - 16) / 2, y + (CELL - 16) / 2);
        }
    }
}
