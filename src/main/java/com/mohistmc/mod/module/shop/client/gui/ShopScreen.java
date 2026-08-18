package com.mohistmc.mod.module.shop.client.gui;

import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.GridScrollList;
import com.mohistmc.mod.api.gui.GuiCoord;
import com.mohistmc.mod.api.gui.ImageWidget;
import com.mohistmc.mod.api.gui.Modal;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.api.gui.PositionedWidget;
import com.mohistmc.mod.api.gui.SimpleLabel;
import com.mohistmc.mod.api.gui.TextInputWidget;
import com.mohistmc.mod.module.shop.common.data.Currency;
import com.mohistmc.mod.module.shop.common.data.RestockCycle;
import com.mohistmc.mod.module.shop.common.data.RestockTimer;
import com.mohistmc.mod.module.shop.common.data.ShopCategory;
import com.mohistmc.mod.module.shop.common.data.ShopData;
import com.mohistmc.mod.module.shop.common.data.ShopProduct;
import com.mohistmc.mod.module.shop.common.network.payload.BuyPayload;
import com.mohistmc.mod.module.shop.common.network.payload.BuyResultPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * 系统商店全屏界面（由服务端 OpenShopPayload 打开）
 * <p>布局：标题 + 余额 + 分类 Tab/搜索框（同行自适应）+ 左侧商品格子网格（高度占满）
 * + 右侧详情面板（数量/购买）+ 关闭按钮；
 * 购买走确认 Modal 后发 BuyPayload，结果由服务端 BuyResultPayload 回传后刷新。
 * 输入框使用自定义 {@link TextInputWidget}（适配画布缩放），替代原版 EditBox。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
@OnlyIn(Dist.CLIENT)
public class ShopScreen extends EnhancedScreen {

    /** 网格列数上限 */
    private static final int GRID_COLS = 13;
    /** 右侧详情面板宽度（与背包栏对齐，整体放大） */
    private static final int DETAIL_W = 180;
    /** 左栏（网格）与右栏（详情）间距 */
    private static final int SIDE_GAP = 10;

    // ======== 布局常量 ========
    /** 分类 Tab 竖列宽度/高度 */
    private static final int TAB_W = 72;
    private static final int TAB_H = 26;
    private static final int TAB_GAP = 6;
    /** 商店名卡片宽度 */
    private static final int SHOP_CARD_W = 130;
    /** 顶部行高度（商店名/搜索/补货） */
    private static final int TOP_H = 24;
    /** 搜索框宽度 */
    private static final int SEARCH_W = 220;

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
    /** 详情面板高度（重建时记录，供操作区布局使用） */
    private int detailPanelH = 150;
    private DetailIconWidget detailIcon;
    private CenteredLabel detailHint;
    private CenteredLabel detailName;
    private CenteredLabel detailPrice;
    private CenteredLabel detailStock;
    private TextInputWidget detailQtyEdit;
    private CenteredLabel detailTotal;
    private CustomButton detailDec;
    private CustomButton detailInc;
    private CustomButton detailBuy;
    /** 防止 setText 触发 onChange 的递归更新 */
    private boolean updatingQty;

    public ShopScreen(int balance) {
        super(Component.translatable("gui.mohistmc.shop.title"), 0xE0101010);
        this.balance = balance;
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();
        // 更大的响应式内容区：宽占逻辑宽 72%（上限 880），高近全屏
        int guiW = Math.clamp(sw * 72 / 100, 480, Math.min(880, sw - 16));
        int guiH = sh - 12;
        int left = (sw - guiW) / 2;
        int top = 6;

        // 布局列：左侧分类 Tab 竖列 → 网格；顶部一行：商店名 | 搜索框 | 补货卡片；右侧列：补货/余额/操作区
        int gridX = left + TAB_W + 12;

        // 网格与详情面板位置均不动（DETAIL_W 右对齐）；背包从详情面板左边缘延伸到屏幕最右侧，平分9格
        int balanceX = left + guiW - DETAIL_W;
        int gridW = balanceX - gridX - SIDE_GAP;
        int cols = Math.max(2, Math.min(GRID_COLS, (gridW - 8) / 36));
        // 背包宽度 = 从详情面板左边缘到窗口最右侧，平分9格
        int invW = sw - balanceX;

        int topRowY = top + 28;
        // 顶部行与网格贴紧（仅留4px间距）
        int gridTop = topRowY + TOP_H + 4;

        // —— 分类 Tab（左侧竖列） ——
        int tabY = gridTop;
        var defaultShop = ShopData.getDefaultShop();
        var categories = defaultShop != null ? defaultShop.getCategories() : java.util.List.<ShopCategory>of();
        // 添加"全部"Tab
        var allTab = new CustomButton(left, tabY, TAB_W, TAB_H,
                Component.translatable("gui.mohistmc.shop.cat.all"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderRadius(0)
                .setAutoFit(false) // 固定竖列宽度，防止文字过长向右撑破网格
                .onClick(() -> selectCategory(ShopCategory.ALL_ID));
        categoryTabs.add(allTab);
        addWidget(allTab);
        tabY += TAB_H + TAB_GAP;

        for (var category : categories) {
            var tab = new CustomButton(left, tabY, TAB_W, TAB_H,
                    Component.translatable(category.getLangKey()), 0xFF333344)
                    .setTextColor(0xFFFFFFFF).setBorderRadius(0)
                    .setAutoFit(false)
                    .onClick(() -> selectCategory(category.getId()));
            categoryTabs.add(tab);
            addWidget(tab);
            tabY += TAB_H + TAB_GAP;
        }
        updateTabStyles();

        // 商店名字卡片（左上角）
        var shopCard = new Panel(left, topRowY, SHOP_CARD_W, TOP_H, 0x66000000);
        var shopNameLabel = new CenteredLabel(0, (TOP_H - 11) / 2, SHOP_CARD_W, 0xFFFFFFFF);
        shopNameLabel.setText(Component.translatable("gui.mohistmc.shop.title"));
        shopCard.addChild(shopNameLabel);
        addWidget(shopCard);

        // 搜索框（自定义输入框，宽度适中，不与补货卡片重叠）
        int searchX = left + SHOP_CARD_W + 10;
        var searchInput = new TextInputWidget(searchX, topRowY, SEARCH_W, TOP_H)
                .setPlaceholder(Component.translatable("gui.mohistmc.shop.search").getString())
                .setMaxLength(32)
                .setFontSize(14)
                .setOnChange(text -> {
                    searchText = text;
                    refreshGrid();
                });
        addWidget(searchInput);

        // 补货时间卡片（右上，与商店名/搜索框同行水平对齐，秒级动态刷新）
        var restockCard = new Panel(balanceX, topRowY, DETAIL_W, TOP_H, 0x66000000);
        restockLabel = new CenteredLabel(0, (TOP_H - Math.round(11 * 1.4f)) / 2, DETAIL_W, 0xFFFFD700)
                .setTextScale(1.4f);
        updateRestockLabel();
        restockCard.addChild(restockLabel);
        addWidget(restockCard);

        // —— 右侧列底部：玩家背包栏（热键栏）+ 物品栏，平分9格填满到屏幕最右侧 ——
        var invWidget = new PlayerInventoryWidget(balanceX, 0, invW);
        int invY = guiH - 8 - invWidget.height; // 底部对齐
        invWidget.setRelativeY(invY);
        addWidget(invWidget);

        // 余额卡片（背包正上方，无背景；图标与文字自动放大，保持4px内边距）
        int balanceY = invY - 4 - TOP_H;
        int iconSize = TOP_H - 8; // 4px上下内边距
        addWidget(new ImageWidget(balanceX + 4, balanceY + 4, iconSize, iconSize)
                .setTexture(Currency.iconTexture())
                .setTextureSrcSize(Currency.iconSize()));
        float textScale = (TOP_H - 8f) / 11f; // 文字高度填满内容区
        int textY = balanceY + (int)((TOP_H - 11 * textScale) / 2);
        balanceLabel = new SimpleLabel(balanceX + 4 + iconSize + 4, textY,
                Component.literal(String.valueOf(balance)), 0xFFFFD700)
                .setTextScale(textScale);
        addWidget(balanceLabel);

        // —— 商品格子网格（位置/高度保持原样占满到底部，不动） ——
        int gridH = Math.min(guiH - gridTop - 8, sh - gridTop - 8);

        // —— 右侧详情面板（压缩高度贴合内容，位于背包上方，宽度固定不变） ——
        int detailTop = gridTop;
        int detailH = Math.max(150, Math.min(guiH - 8 - detailTop, 230));
        buildDetailPanel(balanceX, detailTop, DETAIL_W, detailH);
        grid = new GridScrollList(gridX, gridTop, gridW, gridH, 0x66000000);
        grid.setColumns(cols).setGap(4, 5).setSquareCells(true).setCellExtraHeight(14)
                .setPadding(4);
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

    private void buildDetailPanel(int panelX, int panelY, int panelW, int panelH) {
        detailPanelH = panelH;
        // 压缩模式：信息区紧凑显示；阈值 210 保证操作区（数量+总价+购买）完整容纳不重叠
        boolean compact = panelH < 210;
        var panel = new Panel(panelX, panelY, panelW, panelH, 0x66000000);

        // —— 上部：商品信息（压缩模式下紧凑显示，文字仍放大） ——
        float labelScale = compact ? 1.3f : 1.6f; // 详情文字放大倍数
        int iconSize = compact ? 20 : 44;
        int iconY = compact ? 8 : 16;
        int nameY = compact ? 28 : 72;
        int priceY = compact ? 44 : 90;
        int stockY = compact ? 60 : 108;
        detailIcon = new DetailIconWidget((panelW - iconSize) / 2, iconY, iconSize);
        panel.addChild(detailIcon);

        detailName = new CenteredLabel(0, nameY, panelW, 0xFFFFFFFF).setTextScale(labelScale);
        panel.addChild(detailName);

        detailPrice = new CenteredLabel(0, priceY, panelW, 0xFFFFD700).setTextScale(labelScale);
        panel.addChild(detailPrice);

        // 库存行（剩余数量；售罄红色）
        detailStock = new CenteredLabel(0, stockY, panelW, 0xFFFFFFFF).setTextScale(labelScale);
        panel.addChild(detailStock);

        // 未选中提示（复用名称位置，与详情信息不会同时显示）
        detailHint = new CenteredLabel(0, nameY, panelW, 0xFF888888).setTextScale(labelScale);
        panel.addChild(detailHint);

        // —— 下部：操作区（从底部向上排：购买按钮贴底、总价在其上方、数量行再上方，确保不越界） ——
        int qtyH = 20;                       // 数量行组件高度（[-] 输入框 [+]）
        int buyH = 24;                       // 购买按钮高度
        int buyY = panelH - 12 - buyH;       // 购买按钮（距底 12）
        int totalH = (int) (11 * labelScale); // 总价文字高（随缩放）
        int totalY = buyY - 6 - totalH;      // 总价（购买按钮上方 6px）
        int qtyY = Math.max(stockY + 14, totalY - 6 - qtyH); // 数量行（总价上方 6px，且不低于信息区）

        // 数量行（居中）：[-] [输入框] [+]
        int qtyEditW = 34;
        int qtyBtnW = 24;
        int qtyRowW = qtyBtnW + 4 + qtyEditW + 4 + qtyBtnW;
        int qtyStartX = (panelW - qtyRowW) / 2;
        detailDec = new CustomButton(qtyStartX, qtyY, qtyBtnW, qtyH, Component.literal("-"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .setAutoFit(false).setFontSize(18) // 固定尺寸按钮，字号固定防止过小
                .onClick(() -> {
                    detailQty = Math.max(1, detailQty - 1);
                    syncQtyInput();
                    refreshDetail();
                });
        panel.addChild(detailDec);

        // 数量输入框（自定义，Panel 内定位）
        detailQtyEdit = new TextInputWidget(qtyStartX + qtyBtnW + 4, qtyY, qtyEditW, qtyH)
                .setFontSize(14)
                .setMaxLength(4)
                .setOnChange(this::onQtyTyped);
        panel.addChild(detailQtyEdit);

        detailInc = new CustomButton(qtyStartX + qtyBtnW + 4 + qtyEditW + 4, qtyY, qtyBtnW, qtyH, Component.literal("+"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .setAutoFit(false).setFontSize(18)
                .onClick(() -> {
                    detailQty = Math.min(maxQtyFor(selectedCard.getProduct()), detailQty + 1);
                    syncQtyInput();
                    refreshDetail();
                });
        panel.addChild(detailInc);

        detailTotal = new CenteredLabel(0, totalY, panelW, 0xFFFFFFFF).setTextScale(labelScale);
        panel.addChild(detailTotal);

        detailBuy = new CustomButton(10, buyY, panelW - 20, buyH,
                Component.translatable("gui.mohistmc.shop.buy"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4).setFontSize(18)
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

    /** 输入框文本与 detailQty 同步（值相同不 setText，避免触发 onChange 递归） */
    private void syncQtyInput() {
        if (detailQtyEdit != null) {
            String current = detailQtyEdit.getText();
            String next = String.valueOf(detailQty);
            if (!current.equals(next)) {
                detailQtyEdit.setText(next);
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
                .setDialogWidth(160);
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
                    .setDialogWidth(160)
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

    /** 右侧列底部玩家背包预览：物品栏 27 格（9×3）+ 热键栏 9 格，格子尺寸按宽度自适应 */
    private static class PlayerInventoryWidget extends PositionedWidget {
        private static final int GAP = 2;
        private static final int PAD = 4;
        private final Inventory inv;
        private final int cell; // 基础格子边长（随宽度自适应放大）
        private final int rem;  // 平分余数：前 rem 列宽 cell+1，使右边缘精确对齐组件右缘

        PlayerInventoryWidget(int x, int y, int width) {
            super(x, y, width, calcHeight(width));
            int w = width - PAD * 2 - GAP * 8;
            this.cell = Math.max(12, w / 9);
            this.rem = Math.max(0, w - cell * 9);
            var mc = Minecraft.getInstance();
            this.inv = mc.player != null ? mc.player.getInventory() : null;
        }

        private static int calcHeight(int width) {
            int cell = Math.max(12, (width - PAD * 2 - GAP * 8) / 9);
            return 3 * (cell + GAP) - GAP + 8 + cell + PAD * 2;
        }

        @Override
        public void render(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
            if (inv == null) return;
            int ax = getAbsoluteX();
            int ay = getAbsoluteY();
            int gridX = ax + PAD;
            int gridY = ay + PAD;

            // 物品栏（27 格 9×3：inv 槽位 9..35）
            renderSlots(g, inv, 9, 3, gridX, gridY, mouseX, mouseY);
            // 热键栏（9 格：inv 槽位 0..8），上方分隔亮条
            int barY = gridY + 3 * (cell + GAP) + 8;
            int rowW = 9 * cell + 8 * GAP + rem; // 9 列精确总宽
            g.fill(gridX, barY - 6, gridX + rowW, barY - 5, 0x44FFFFFF);
            renderSlots(g, inv, 0, 1, gridX, barY, mouseX, mouseY);
        }

        /** 渲染若干行格子（每行 9 格），hover 时显示物品名 */
        private void renderSlots(GuiGraphicsExtractor g, Inventory inv, int startIndex,
                                 int rows, int x, int y, int mx, int my) {
            var font = Minecraft.getInstance().font;
            int size = inv.getContainerSize();
            Component tip = null;
            int tipX = 0, tipY = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < 9; col++) {
                    int idx = startIndex + row * 9 + col;
                    int effCell = cell + (col < rem ? 1 : 0); // 前 rem 列略宽，精确填满
                    int sx = x + col * (cell + GAP) + Math.min(col, rem);
                    int sy = y + row * (cell + GAP);
                    int iconOff = (effCell - 16) / 2;
                    // 格子背景 + 边框
                    g.fill(sx, sy, sx + effCell, sy + effCell, 0x33222222);
                    g.fill(sx, sy, sx + effCell, sy + 1, 0x55444466);
                    g.fill(sx, sy + effCell - 1, sx + effCell, sy + effCell, 0x55444466);
                    g.fill(sx, sy, sx + 1, sy + effCell, 0x55444466);
                    g.fill(sx + effCell - 1, sy, sx + effCell, sy + effCell, 0x55444466);

                    if (idx < 0 || idx >= size) continue;
                    var stack = inv.getItem(idx);
                    if (stack.isEmpty()) continue;
                    g.item(stack, sx + iconOff, sy + iconOff);
                    // 数量
                    if (stack.getCount() > 1) {
                        String cnt = String.valueOf(stack.getCount());
                        g.text(font, cnt, sx + effCell - font.width(cnt) - 1, sy + effCell - font.lineHeight, 0xFFFFFFFF);
                    }
                    // hover 物品名
                    if (mx >= sx && mx < sx + effCell && my >= sy && my < sy + effCell) {
                        tip = stack.getHoverName();
                        tipX = mx;
                        tipY = my;
                    }
                }
            }
            if (tip != null) {
                g.setTooltipForNextFrame(tip, GuiCoord.toScreenX(tipX), GuiCoord.toScreenY(tipY));
            }
        }
    }

    /** 详情面板商品图标（按尺寸缩放 16×16 源图；常规 44px、紧凑 20px） */
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
        private float textScale = 1.0f;

        CenteredLabel(int x, int y, int width, int color) {
            super(x, y, width, 11);
            this.color = color;
        }

        CenteredLabel setTextScale(float scale) {
            this.textScale = Math.max(0.5f, scale);
            this.height = (int) (11 * this.textScale);
            return this;
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
            int ay = getAbsoluteY();
            var font = Minecraft.getInstance().font;
            int textW = (int) (font.width(text) * textScale);
            int iconSz = (int) (16 * textScale);
            int iconW = texture == null ? 0 : iconSz + 4;
            int startX = ax + (width - (textW + iconW)) / 2;
            if (texture != null) {
                // 完整贴图缩放显示（跟随文字缩放）
                int srcSize = Currency.iconSize();
                var pose = g.pose();
                pose.pushMatrix();
                pose.translate(startX, ay - 3);
                pose.scale(iconSz / (float) srcSize, iconSz / (float) srcSize);
                pose.translate(-startX, -(ay - 3));
                g.blit(RenderPipelines.GUI_TEXTURED, texture, startX, ay - 3,
                        0, 0, srcSize, srcSize, srcSize, srcSize);
                pose.popMatrix();
                startX += iconW;
            }
            // 文字缩放渲染
            var pose = g.pose();
            pose.pushMatrix();
            pose.translate(startX, ay);
            pose.scale(textScale, textScale);
            pose.translate(-startX, -ay);
            g.text(font, text, startX, ay, color);
            pose.popMatrix();
        }
    }
}
