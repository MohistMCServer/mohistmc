package com.mohistmc.mod.module.shop.client.gui;

import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.GridScrollList;
import com.mohistmc.mod.api.gui.ImageWidget;
import com.mohistmc.mod.api.gui.Modal;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.api.gui.PositionedWidget;
import com.mohistmc.mod.api.gui.SimpleLabel;
import com.mohistmc.mod.module.shop.common.data.Currency;
import com.mohistmc.mod.module.shop.common.data.RestockCycle;
import com.mohistmc.mod.module.shop.common.data.RestockTimer;
import com.mohistmc.mod.module.shop.common.data.Shop;
import com.mohistmc.mod.module.shop.common.data.ShopCategory;
import com.mohistmc.mod.module.shop.common.data.ShopData;
import com.mohistmc.mod.module.shop.common.data.ShopProduct;
import com.mohistmc.mod.module.shop.common.network.payload.BuyPayload;
import com.mohistmc.mod.module.shop.common.network.payload.BuyResultPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 系统商店全屏界面（由服务端 OpenShopPayload 打开）
 * <p>布局：标题 + 余额 + 分类 Tab/搜索框（同行自适应）+ 左侧 8 列商品格子网格（高度占满）
 * + 右侧详情面板（数量/购买）+ 关闭按钮；
 * 购买走确认 Modal 后发 BuyPayload，结果由服务端 BuyResultPayload 回传后刷新。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class ShopScreen extends EnhancedScreen {

    /** 网格列数上限 */
    private static final int GRID_COLS = 14;
    /** 右侧详情面板宽度 */
    private static final int DETAIL_W = 100;
    /** 左栏（网格）与右栏（详情）间距 */
    private static final int SIDE_GAP = 6;

    /** 搜索框右侧余额卡片 */
    private SimpleLabel balanceLabel;
    /** 商店补货倒计时卡片（余额卡片正上方，秒级动态刷新） */
    private CenteredLabel restockLabel;
    private long lastRestockSeconds = -1;
    private int balance;
    /** 当前弹窗（新建前先隐藏旧的，避免 modals 无限增长） */
    private Modal currentModal;

    // —— 分类 / 搜索 ——
    private final List<CustomButton> categoryTabs = new ArrayList<>();
    private int currentCategoryId = ShopCategory.ALL_ID;
    private String searchText = "";
    private GridScrollList grid;

    // —— 选中商品 / 右侧详情面板 ——
    private ShopCard selectedCard;
    private int detailQty = 1;
    /** 详情面板高度（极矮窗口时面板被压缩，隐藏顶部信息区） */
    private int detailPanelH = 150;
    private DetailIconWidget detailIcon;
    private CenteredLabel detailHint;
    private CenteredLabel detailName;
    private CenteredLabel detailPrice;
    private CenteredLabel detailStock;
    private EditBox detailQtyEdit;
    private CenteredLabel detailTotal;
    private CustomButton detailDec;
    private CustomButton detailInc;
    private CustomButton detailBuy;
    /** 防止 setValue 触发 responder 的递归更新 */
    private boolean updatingQty;

    public ShopScreen(int balance) {
        super(Component.translatable("gui.mohistmc.shop.title"), 0xE0101010);
        this.balance = balance;
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();
        // 响应式内容区宽度：目标为窗口 55%（上限 560），但绝不超出窗口（左右各留 8px）
        int guiW = Math.min(Math.max(sw * 65 / 100, 320), Math.min(640, sw - 16));
        int guiH = sh - 20;
        int left = (sw - guiW) / 2;
        // 内容区垂直居中：上边距与下边距保持一致
        int top = Math.max(8, (sh - guiH) / 2);

        // 布局列：左侧分类 Tab 竖列 → 网格；顶部一行：商店名 | 搜索框 | 补货卡片；右侧列：补货/余额/操作区
        int tabColW = 44;
        int tabColGap = 4;
        int shopCardW = 100;
        int gridX = left + tabColW + 8;
        int balanceX = left + guiW - DETAIL_W; // 右侧列 X（与操作区同宽对齐）
        int gridW = balanceX - gridX - SIDE_GAP;

        // —— 分类 Tab（左侧竖列） ——
        int tabY = top + 42;
        var defaultShop = ShopData.getDefaultShop();
        var categories = defaultShop != null ? defaultShop.getCategories() : java.util.List.<ShopCategory>of();
        // 添加"全部"Tab
        var allTab = new CustomButton(left, tabY, tabColW, 18,
                Component.translatable("gui.mohistmc.shop.cat.all"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderRadius(0)
                .onClick(() -> selectCategory(ShopCategory.ALL_ID));
        categoryTabs.add(allTab);
        addWidget(allTab);
        tabY += 18 + tabColGap;

        for (var category : categories) {
            var tab = new CustomButton(left, tabY, tabColW, 18,
                    Component.translatable(category.getLangKey()), 0xFF333344)
                    .setTextColor(0xFFFFFFFF).setBorderRadius(0)
                    .onClick(() -> selectCategory(category.getId()));
            categoryTabs.add(tab);
            addWidget(tab);
            tabY += 18 + tabColGap;
        }
        updateTabStyles();

        // 商店名字卡片（左上角）
        var shopCard = new Panel(left, top + 18, shopCardW, 18, 0x66000000);
        var shopNameLabel = new CenteredLabel(0, (18 - 9) / 2, shopCardW, 0xFFFFFFFF);
        shopNameLabel.setText(Component.translatable("gui.mohistmc.shop.title"));
        shopCard.addChild(shopNameLabel);
        addWidget(shopCard);

        // 搜索框（商店名卡片与补货卡片之间，填补横向空白）
        int searchX = left + shopCardW + 8;
        var searchBox = new EditBox(minecraft.font, searchX, top + 18, balanceX - searchX - 8, 18,
                Component.translatable("gui.mohistmc.shop.search"));
        searchBox.setMaxLength(32);
        searchBox.setResponder(text -> {
            searchText = text;
            refreshGrid();
        });
        addWidget(searchBox);

        // 补货时间卡片（右上，与商店名/搜索框同行水平对齐，秒级动态刷新）
        var restockCard = new Panel(balanceX, top + 18, DETAIL_W, 18, 0x66000000);
        restockLabel = new CenteredLabel(0, (18 - 9) / 2, DETAIL_W, 0xFFFFD700);
        updateRestockLabel();
        restockCard.addChild(restockLabel);
        addWidget(restockCard);

        // 余额卡片（补货卡片正下方；图标与文字按卡片高度垂直居中）
        var balanceCard = new Panel(balanceX, top + 42, DETAIL_W, 18, 0x66000000);
        balanceCard.addChild(new ImageWidget(6, (18 - 8) / 2, 8, 8)
                .setTexture(Currency.iconTexture())
                .setTextureSrcSize(Currency.iconSize()));
        balanceLabel = new SimpleLabel(16, (18 - Math.round(9 * 1.3f)) / 2,
                Component.literal(String.valueOf(balance)), 0xFFFFD700)
                .setTextScale(1.3f);
        balanceCard.addChild(balanceLabel);
        addWidget(balanceCard);

        // —— 右侧详情面板（选中商品后操作数量/购买；从余额卡片下方开始，避免与其重叠） ——
        int gridTop = top + 42;
        int detailTop = top + 66;
        // 操作区高度：130 下限保证内容完整，屏幕底部约束防溢出
        int detailH = Math.max(130, guiH - (detailTop - top) - 40);
        detailH = Math.min(detailH, sh - detailTop - 8);
        buildDetailPanel(balanceX, detailTop, detailH);

        // —— 商品格子网格（Tab 竖列右侧；列数随可用宽度自适应：每格最小 ~32px，最多 12 列） ——
        // 高度与右侧列（余额卡片 + 操作区）底部对齐，任意窗口高度下两侧齐平
        int gridH = (detailTop + detailH) - gridTop;
        gridH = Math.min(gridH, sh - gridTop - 8);
        int cols = Math.max(2, Math.min(GRID_COLS, gridW / 28));
        grid = new GridScrollList(gridX, gridTop, gridW, gridH, 0x66000000);
        grid.setColumns(cols).setGap(3, 4).setSquareCells(true).setCellExtraHeight(13)
                .setPadding(2);
        addWidget(grid);

        // 重建（resize/重开）后按商品引用恢复选中，避免详情清空
        ShopProduct prevSelected = selectedCard != null ? selectedCard.getProduct() : null;
        refreshGrid();
        if (prevSelected != null) {
            for (var item : grid.getItems()) {
                if (item instanceof ShopCard card && card.getProduct() == prevSelected) {
                    onCardSelected(card);
                    break;
                }
            }
        }
        refreshDetail();
    }

    // ======== 右侧详情面板 ========

    private void buildDetailPanel(int panelX, int panelY, int panelH) {
        detailPanelH = panelH;
        // 压缩模式：信息区紧凑显示；阈值 166 保证按钮区（数量+总价+购买）完整容纳不重叠
        boolean compact = panelH < 166;
        var panel = new Panel(panelX, panelY, DETAIL_W, panelH, 0x66000000);

        // —— 上部：商品信息（压缩模式下紧凑显示，不隐藏） ——
        int iconSize = compact ? 16 : 32;
        int iconY = compact ? 6 : 12;
        int nameY = compact ? 24 : 52;
        int priceY = compact ? 34 : 64;
        int stockY = compact ? 44 : 76;
        detailIcon = new DetailIconWidget((DETAIL_W - iconSize) / 2, iconY, iconSize);
        panel.addChild(detailIcon);

        detailName = new CenteredLabel(0, nameY, DETAIL_W, 0xFFFFFFFF);
        panel.addChild(detailName);

        detailPrice = new CenteredLabel(0, priceY, DETAIL_W, 0xFFFFD700);
        panel.addChild(detailPrice);

        // 库存行（剩余数量；售罄红色）
        detailStock = new CenteredLabel(0, stockY, DETAIL_W, 0xFFFFFFFF);
        panel.addChild(detailStock);

        // 未选中提示（复用名称位置，与详情信息不会同时显示）
        detailHint = new CenteredLabel(0, nameY, DETAIL_W, 0xFF888888);
        panel.addChild(detailHint);

        // —— 下部：操作区（从购买按钮向上排：总价贴按钮上方 5px、数量行贴总价上方 5px，最小不低于信息区） ——
        int qtyH = 14;                          // 数量行组件高度（[-] 输入框 [+]）
        int buyY = panelH - 12 - 20;           // 购买按钮（距底 12）
        int totalY = Math.max(compact ? 60 : 105, buyY - 14); // 总价（购买按钮上方 5px，不低于信息区）
        int qtyY = Math.max(compact ? 53 : 90, totalY - 5 - qtyH); // 数量行（总价上方 5px，不低于信息区）

        // 数量行（居中）：[-] [输入框] [+]
        int qtyEditW = 26;
        int qtyRowW = 20 + 3 + qtyEditW + 3 + 20; // 72
        int qtyStartX = (DETAIL_W - qtyRowW) / 2;
        detailDec = new CustomButton(qtyStartX, qtyY, 20, qtyH, Component.literal("-"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> {
                    detailQty = Math.max(1, detailQty - 1);
                    syncQtyInput();
                    refreshDetail();
                });
        panel.addChild(detailDec);

        // 数量输入框（Screen 级组件，便于键盘输入；与按钮行对齐）
        detailQtyEdit = new EditBox(minecraft.font,
                panelX + qtyStartX + 20 + 3, panelY + qtyY, qtyEditW, qtyH,
                Component.literal(""));
        detailQtyEdit.setMaxLength(4);
        detailQtyEdit.setResponder(this::onQtyTyped);
        addWidget(detailQtyEdit);

        detailInc = new CustomButton(qtyStartX + 20 + 3 + qtyEditW + 3, qtyY, 20, qtyH, Component.literal("+"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> {
                    detailQty = Math.min(maxQtyFor(selectedCard.getProduct()), detailQty + 1);
                    syncQtyInput();
                    refreshDetail();
                });
        panel.addChild(detailInc);

        detailTotal = new CenteredLabel(0, totalY, DETAIL_W, 0xFFFFFFFF);
        panel.addChild(detailTotal);

        detailBuy = new CustomButton(8, buyY, DETAIL_W - 16, 20,
                Component.translatable("gui.mohistmc.shop.buy"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4)
                .onClick(() -> {
                    if (selectedCard != null) confirmBuy(selectedCard.getProduct(), detailQty);
                });
        panel.addChild(detailBuy);

        addWidget(panel);
    }

    /** 刷新详情面板：无选中显示提示，有选中显示商品信息与数量/总价（压缩模式信息区紧凑显示，不隐藏） */
    private void refreshDetail() {
        boolean has = selectedCard != null;
        detailIcon.setStack(has ? selectedCard.getProduct().stack() : ItemStack.EMPTY);
        detailHint.setText(!has ? Component.translatable("gui.mohistmc.shop.detail.hint") : null);
        detailName.setText(has ? selectedCard.getProduct().stack().getHoverName() : null);
        detailPrice.setText(has ? Component.literal("单价：" + selectedCard.getProduct().price()) : null);
        // 库存行：无限/剩余 N/已售罄（售罄红色）
        if (has) {
            int left = selectedCard.getRemaining();
            if (left < 0) {
                detailStock.setText(Component.translatable("gui.mohistmc.shop.stock.unlimited"));
                detailStock.setColor(0xFFFFFFFF);
            } else if (left == 0) {
                detailStock.setText(Component.translatable("gui.mohistmc.shop.stock.none"));
                detailStock.setColor(0xFFFF5555);
            } else if (selectedCard.getProduct().restockCycle() != RestockCycle.NONE) {
                detailStock.setText(Component.translatable(selectedCard.getProduct().restockCycle() == RestockCycle.DAILY
                        ? "gui.mohistmc.shop.stock.restock" : "gui.mohistmc.shop.stock.restock.weekly", left));
                detailStock.setColor(0xFFFFFFFF);
            } else {
                detailStock.setText(Component.translatable("gui.mohistmc.shop.stock.remaining", left));
                detailStock.setColor(0xFFFFFFFF);
            }
        } else {
            detailStock.setText(null);
        }
        // 数量/按钮区：售罄或无选中时数量锁定 0、加减与购买禁用
        boolean soldOut = isDetailSoldOut();
        if (soldOut || !has) {
            detailQty = 0;
            updatingQty = true;
            syncQtyInput();
            updatingQty = false;
            detailTotal.setText(Component.literal("总价：0"));
            detailDec.setEnabled(false);
            detailInc.setEnabled(false);
            detailBuy.setEnabled(false);
        } else {
            if (detailQty <= 0) detailQty = 1;
            // 切换商品后钳制到新上限（背包格数 × 物品最大堆叠）
            detailQty = Math.min(detailQty, maxQtyFor(selectedCard.getProduct()));
            syncQtyInput();
            detailTotal.setText(has
                    ? Component.literal("总价：" + (long) selectedCard.getProduct().price() * detailQty)
                    : null);
            detailDec.setEnabled(has);
            detailInc.setEnabled(has);
            detailBuy.setEnabled(has);
        }
    }

    /** 刷新搜索框右侧余额卡片 */
    private void refreshBalance() {
        if (balanceLabel != null) {
            balanceLabel.setText(Component.literal(String.valueOf(balance)));
        }
    }

    /** 刷新补货倒计时（说明前缀 + 精准到秒） */
    private void updateRestockLabel() {
        if (restockLabel != null) {
            var duration = RestockTimer.remaining(RestockCycle.WEEKLY);
            restockLabel.setText(Component.translatable("gui.mohistmc.shop.restock.countdown",
                    RestockTimer.formatPrecise(duration)));
            lastRestockSeconds = duration.getSeconds();
        }
    }

    /** 每 tick 刷新倒计时（秒级动态更新） */
    @Override
    public void tick() {
        super.tick();
        if (restockLabel != null) {
            long seconds = RestockTimer.remaining(RestockCycle.WEEKLY).getSeconds();
            if (seconds != lastRestockSeconds) {
                updateRestockLabel();
            }
        }
    }

    // ======== 悬停提示 ========

    /** 悬停商品格子时显示物品完整 tooltip（名称 + 属性等，与原版一致） */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        if (grid != null) {
            int idx = grid.getHoveredIndex();
            if (idx >= 0 && idx < grid.getItems().size()) {
                var item = grid.getItems().get(idx);
                if (item instanceof ShopCard card) {
                    graphics.setTooltipForNextFrame(minecraft.font, card.getProduct().stack(), mouseX, mouseY);
                }
            }
        }
    }

    // ======== 数量输入 ========

    /** 手动输入数量：解析并钳制 1~64，同步总价（非法输入忽略；售罄时锁定为 0） */
    private void onQtyTyped(String text) {
        if (updatingQty) return;
        if (isDetailSoldOut()) {
            // 售罄商品数量锁定 0：忽略一切输入
            updatingQty = true;
            syncQtyInput();
            updatingQty = false;
            return;
        }
        int parsed;
        try {
            parsed = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return;
        }
        detailQty = Math.max(1, Math.min(maxQtyFor(selectedCard.getProduct()), parsed));
        updatingQty = true;
        syncQtyInput();
        updatingQty = false;
        refreshDetail();
    }

    /** 输入框文本与 detailQty 同步（值相同不 setValue，避免触发 responder 递归） */
    private void syncQtyInput() {
        if (detailQtyEdit != null) {
            String current = detailQtyEdit.getValue();
            String next = String.valueOf(detailQty);
            if (!current.equals(next)) {
                detailQtyEdit.setValue(next);
            }
        }
    }

    /** 当前选中商品是否售罄（售罄时数量锁定为 0、禁止操作） */
    private boolean isDetailSoldOut() {
        return selectedCard != null && selectedCard.isSoldOut();
    }

    /** 数量上限：主背包格数（36）× 物品最大堆叠（随选中商品变化） */
    private int maxQtyFor(ShopProduct product) {
        return 36 * Math.max(1, product.stack().getMaxStackSize());
    }

    // ======== 分类 / 搜索 ========

    private void selectCategory(int categoryId) {
        this.currentCategoryId = categoryId;
        updateTabStyles();
        refreshGrid();
    }

    /** 选中 Tab 绿色高亮，其余恢复深灰（categoryTabs[0] 为"全部"，其余对应 categories 列表顺序） */
    private void updateTabStyles() {
        int selectedIndex = 0;
        var defaultShop = ShopData.getDefaultShop();
        var categories = defaultShop != null ? defaultShop.getCategories() : java.util.List.<ShopCategory>of();
        if (currentCategoryId != ShopCategory.ALL_ID) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == currentCategoryId) {
                    selectedIndex = i + 1;
                    break;
                }
            }
        }
        for (int i = 0; i < categoryTabs.size(); i++) {
            var tab = categoryTabs.get(i);
            if (i == selectedIndex) {
                tab.setNormalColor(0xFF4CAF50);
                tab.setHoverColor(0xFF66BB6A);
            } else {
                tab.setNormalColor(0xFF333344);
                tab.setHoverColor(0xFF555566);
            }
        }
    }

    /** 按 类别 + 搜索词 过滤并重建网格；清空选中与数量 */
    private void refreshGrid() {
        grid.clearItems();
        if (selectedCard != null) {
            selectedCard.setSelected(false);
            selectedCard = null;
        }
        detailQty = 1;
        String query = searchText.toLowerCase(Locale.ROOT).trim();
        var defaultShop = ShopData.getDefaultShop();
        var products = defaultShop != null ? defaultShop.getProducts() : java.util.List.<ShopProduct>of();
        for (var product : products) {
            if (currentCategoryId != ShopCategory.ALL_ID && product.categoryId() != currentCategoryId) {
                continue;
            }
            if (!query.isEmpty() && !matchesSearch(product, query)) {
                continue;
            }
            var card = new ShopCard(product, this::onCardSelected);
            card.setRemaining(product.stock());
            grid.addItem(card);
        }
        refreshDetail();
    }

    /** 点击格子：记录选中并刷新详情面板 */
    private void onCardSelected(ShopCard card) {
        if (selectedCard != null && selectedCard != card) {
            selectedCard.setSelected(false);
        }
        selectedCard = card;
        card.setSelected(true);
        refreshDetail();
    }

    /** 搜索匹配：显示名或注册名包含关键词 */
    private boolean matchesSearch(ShopProduct product, String query) {
        String name = product.stack().getHoverName().getString().toLowerCase(Locale.ROOT);
        if (name.contains(query)) return true;
        String id = BuiltInRegistries.ITEM.getKey(product.stack().getItem()).getPath().toLowerCase(Locale.ROOT);
        return id.contains(query);
    }

    // ======== 购买 ========

    /** 点击「购买」：弹确认 Modal，确认后发购买请求 */
    private void confirmBuy(ShopProduct product, int qty) {
        long total = (long) product.price() * qty;
        var modal = new Modal(
                Component.translatable("gui.mohistmc.shop.confirm_title"),
                Component.translatable("gui.mohistmc.shop.confirm_message", product.stack().getHoverName(), qty, total))
                .setDialogWidth(140);
        modal.addConfirmButton(() -> ClientPacketDistributor.sendToServer(new BuyPayload(product.id(), qty)));
        modal.addCancelButton(() -> {});
        showModal(modal);
    }

    /** 服务端购买结果回传：刷新余额与库存，失败弹提示 */
    public void handleBuyResult(BuyResultPayload payload) {
        this.balance = payload.newBalance();
        refreshBalance();
        // 刷新选中商品的剩余库存（售罄显示随之更新）
        if (selectedCard != null) {
            selectedCard.setRemaining(payload.stock());
        }
        if (!payload.success()) {
            var modal = new Modal(
                    Component.translatable("gui.mohistmc.shop.fail"),
                    Component.translatable(payload.message()))
                    .setDialogWidth(140)
                    .setMessageColor(0xFFFF5555); // 失败原因红色提示
            modal.addConfirmButton(() -> {});
            showModal(modal);
        }
    }

    private void showModal(Modal modal) {
        if (currentModal != null) {
            currentModal.hide();
        }
        currentModal = modal;
        addModal(modal);
        modal.show();
    }

    /** 详情面板商品图标（按尺寸缩放 16×16 源图；常规 32px、紧凑 16px） */
    private static class DetailIconWidget extends PositionedWidget {
        private final float scale;
        private ItemStack stack = ItemStack.EMPTY;

        DetailIconWidget(int x, int y, int size) {
            super(x, y, size, size);
            this.scale = size / 16f;
        }

        void setStack(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
            if (stack.isEmpty()) return;
            int ax = getAbsoluteX();
            int ay = getAbsoluteY();
            var pose = g.pose();
            pose.pushMatrix();
            pose.translate(ax + width / 2f, ay + height / 2f);
            pose.scale(scale, scale);
            pose.translate(-(ax + width / 2f), -(ay + height / 2f));
            g.item(stack, ax + (width - 16) / 2, ay + (height - 16) / 2);
            pose.popMatrix();
        }
    }

    /** 固定宽度居中对齐文本（可选前置货币贴图图标，文本变化后自动重新居中） */
    private static class CenteredLabel extends PositionedWidget {
        private int color;
        private Component text;
        private Identifier texture;

        CenteredLabel(int x, int y, int width, int color) {
            super(x, y, width, 9);
            this.color = color;
        }

        void setText(Component text) {
            this.text = text;
        }

        void setColor(int color) {
            this.color = color;
        }

        void setTexture(Identifier texture) {
            this.texture = texture;
        }

        @Override
        public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
            if (text == null) return;
            int ax = getAbsoluteX();
            var font = Minecraft.getInstance().font;
            int iconW = texture == null ? 0 : 16 + 4;
            int startX = ax + (width - (font.width(text) + iconW)) / 2;
            if (texture != null) {
                // 完整贴图缩放显示为 16px（pose 缩放：平移到目标原点 → 缩放 → 回退）
                int srcSize = Currency.iconSize();
                var pose = g.pose();
                pose.pushMatrix();
                pose.translate(startX, getAbsoluteY() - 3);
                pose.scale(16f / srcSize, 16f / srcSize);
                pose.translate(-startX, -(getAbsoluteY() - 3));
                g.blit(RenderPipelines.GUI_TEXTURED, texture, startX, getAbsoluteY() - 3,
                        0, 0, srcSize, srcSize, srcSize, srcSize);
                pose.popMatrix();
                startX += iconW;
            }
            g.text(font, text, startX, getAbsoluteY(), color);
        }
    }
}
