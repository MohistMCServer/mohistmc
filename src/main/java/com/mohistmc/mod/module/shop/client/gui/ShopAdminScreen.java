package com.mohistmc.mod.module.shop.client.gui;

import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.DropdownMenu;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.ItemPickerModal;
import com.mohistmc.mod.api.gui.Modal;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.api.gui.ScrollList;
import com.mohistmc.mod.api.gui.ScrollListItem;
import com.mohistmc.mod.api.gui.SimpleLabel;
import com.mohistmc.mod.module.shop.common.data.RestockCycle;
import com.mohistmc.mod.module.shop.common.data.Shop;
import com.mohistmc.mod.module.shop.common.data.ShopCategory;
import com.mohistmc.mod.module.shop.common.data.ShopData;
import com.mohistmc.mod.module.shop.common.data.ShopProduct;
import com.mohistmc.mod.module.shop.common.network.payload.ShopEditPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 商店管理后台界面（管理员编辑商品/类别/商店）
 * <p>布局：标题栏 + 商店选择器 + 操作按钮 + 内容区（商品列表/类别列表/表单）
 * <p>响应式：窄屏时表单自动堆叠在商品列表下方
 *
 * @author Mgazul
 * @date 2026/8/10
 */
@OnlyIn(Dist.CLIENT)
public class ShopAdminScreen extends EnhancedScreen {

    private static final int FORM_W = 160;
    /** 表单堆叠模式临界宽度：低于此值时表单在商品列表下方而非右侧 */
    private static final int STACK_BREAKPOINT = 500;

    /** 当前页面 */
    private AdminPage currentPage = AdminPage.PRODUCTS;
    /** 当前管理的商店 ID */
    private String currentShopId = ShopData.DEFAULT_SHOP_ID;
    /** 当前视图模式 */
    private ViewMode viewMode = ViewMode.LIST;
    /** 正在编辑的商品（ADD 模式为 null） */
    private ShopProduct editingProduct;
    /** 正在编辑的类别 ID（CATEGORY_EDIT 模式） */
    private int editingCategoryId = -1;

    // —— 列表 ——
    private ScrollList itemList;

    // —— 表单 ——
    private EditBox priceEdit;
    private EditBox stockEdit;
    private EditBox categoryNameEdit;
    private EditBox categoryLangKeyEdit;
    private EditBox shopNameEdit;
    private DropdownMenu<Shop> shopDropdown;
    private DropdownMenu<Integer> categoryDropdown;
    private DropdownMenu<RestockCycle> restockDropdown;

    // —— 物品选择器 ——
    private ItemPickerModal itemPicker;
    /** 当前选中的物品 ID（新增商品模式） */
    private String selectedItemId;
    /** 当前选中的物品图标（用于显示） */
    private ItemStack selectedItemStack = ItemStack.EMPTY;

    public ShopAdminScreen() {
        super(Component.translatable("gui.mohistmc.shop.admin.title"), 0xE0101010);
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();
        int guiW = Math.min(Math.max(sw * 75 / 100, 380), Math.min(720, sw - 16));
        int guiH = sh - 20;
        int left = (sw - guiW) / 2;
        int top = Math.max(8, (sh - guiH) / 2);

        // 响应式标志：窄屏时表单堆叠
        boolean formBelow = guiW < STACK_BREAKPOINT;

        // —— 标题栏 ——
        int titleW = 80;
        var titlePanel = new Panel(left, top, titleW, 18, 0x66000000);
        titlePanel.addChild(new SimpleLabel(4, (18 - 9) / 2,
                Component.translatable("gui.mohistmc.shop.admin.title"), 0xFFFFFFFF));
        addWidget(titlePanel);

        // 按钮宽度常量
        int closeBtnW = 60;
        int addBtnW = 70;
        int catBtnW = 70;
        int shopBtnW = 60;
        boolean showAddProduct = (currentPage == AdminPage.PRODUCTS && viewMode == ViewMode.LIST);

        // 计算按钮行总宽度（含间隔 2px）
        int totalBtnW = closeBtnW + 2;
        if (showAddProduct) totalBtnW += addBtnW + 2;
        totalBtnW += catBtnW + 2;
        totalBtnW += shopBtnW;

        // 判断按钮是否换行：下拉框可用宽度 < 80px 时换到第二行
        int gapTitle = 4;
        int gapRight = 4;
        int dropdownAvailable = guiW - titleW - gapTitle - totalBtnW - gapRight;
        boolean buttonsOnRow2 = dropdownAvailable < 80;

        // 商店选择下拉（宽度自适应）
        int selectorW;
        if (buttonsOnRow2) {
            selectorW = guiW - titleW - gapTitle - gapRight;
        } else {
            selectorW = Math.min(200, dropdownAvailable);
        }
        selectorW = Math.max(80, selectorW);
        int selectorX = left + titleW + gapTitle;

        shopDropdown = new DropdownMenu<Shop>(selectorX, top, selectorW, 18,
                Component.translatable("gui.mohistmc.shop.admin.select_shop"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderColor(0xFF888888)
                .setHoverItemColor(0xFF555555).setDropdownBgColor(0xFF333333)
                .onSelect(shop -> {
                    currentShopId = shop.getId();
                    viewMode = ViewMode.LIST;
                    rebuildWidgets();
                });
        int shopIdx = 0;
        for (var shop : ShopData.getAllShops()) {
            shopDropdown.addOption(shop, Component.literal(shop.getName()));
            if (shop.getId().equals(currentShopId)) {
                shopDropdown.setSelectedIndex(shopIdx);
            }
            shopIdx++;
        }
        addWidget(shopDropdown);

        // 操作按钮行
        int btnRowY = buttonsOnRow2 ? top + 22 : top;
        int rightEdge = left + guiW - gapRight;

        // 关闭按钮（最右侧）
        addWidget(new CustomButton(rightEdge - closeBtnW, btnRowY, closeBtnW, 18,
                Component.translatable("gui.mohistmc.shop.admin.close"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> Minecraft.getInstance().gui.setScreen(null)));

        int btnX = rightEdge - closeBtnW - 2;

        // 新增商品按钮
        if (showAddProduct) {
            btnX -= addBtnW;
            addWidget(new CustomButton(btnX, btnRowY, addBtnW, 18,
                    Component.translatable("gui.mohistmc.shop.admin.add"), 0xFF4CAF50)
                    .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                    .onClick(this::switchToAddProduct));
            btnX -= 2;
        }

        // 管理类别按钮
        btnX -= catBtnW;
        addWidget(new CustomButton(btnX, btnRowY, catBtnW, 18,
                Component.translatable("gui.mohistmc.shop.admin.manage_cat"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(this::switchToCategoryManage));
        btnX -= 2;

        // 新增商店按钮
        btnX -= shopBtnW;
        addWidget(new CustomButton(btnX, btnRowY, shopBtnW, 18,
                Component.translatable("gui.mohistmc.shop.admin.add_shop"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(this::switchToAddShop));

        int listTop = btnRowY + 26;
        int listH = guiH - (listTop - top) - 10;

        var shop = ShopData.getShop(currentShopId);

        // —— 物品选择器 ——
        if (itemPicker == null) {
            itemPicker = new ItemPickerModal(this::onItemSelected, this::onItemPickerClosed);
        }
        itemPicker.setScreenPos(leftPos, topPos, getImageWidth(), getImageHeight());
        addModal(itemPicker);

        if (currentPage == AdminPage.PRODUCTS) {
            renderProductPage(left, listTop, guiW, listH, shop, formBelow);
        } else if (currentPage == AdminPage.CATEGORIES) {
            renderCategoryPage(left, listTop, guiW, listH, shop);
        } else if (currentPage == AdminPage.ADD_SHOP) {
            renderAddShopForm(left, listTop, guiW, listH);
        }
    }

    /** 物品选择回调（支持 NBT 的 ItemStack 直接使用，否则回退到 ID 解析） */
    private void onItemSelected(String itemId, ItemStack stack) {
        selectedItemId = itemId;
        if (stack != null && !stack.isEmpty()) {
            selectedItemStack = stack;
        } else {
            selectedItemStack = resolveItem(itemId);
        }
        itemPicker.hide();
        rebuildWidgets();
    }

    /** 物品选择器关闭回调 */
    private void onItemPickerClosed() {
        itemPicker.hide();
    }

    // ======== 商品页面 ========

    private void renderProductPage(int left, int listTop, int guiW, int listH, Shop shop, boolean formBelow) {
        if (shop == null) return;

        if (viewMode == ViewMode.LIST) {
            // 纯列表模式
            itemList = new ScrollList(left, listTop, guiW, listH, 0x66000000);
            for (var product : shop.getProducts()) {
                itemList.addItem(new ProductListItem(product, shop, this::onEdit, this::onDelete));
            }
            addWidget(itemList);
        } else if (formBelow) {
            // 窄屏堆叠模式：表单在商品列表下方
            int formH = Math.min(280, listH / 2);
            int listH2 = listH - formH - 4;
            itemList = new ScrollList(left, listTop, guiW, listH2, 0x66000000);
            for (var product : shop.getProducts()) {
                itemList.addItem(new ProductListItem(product, shop, this::onEdit, this::onDelete));
            }
            addWidget(itemList);
            buildProductForm(left, listTop + listH2 + 4, guiW, formH, shop);
        } else {
            // 宽屏并排模式：左列商品列表，右列表单
            int listW = guiW - FORM_W - 8;
            itemList = new ScrollList(left, listTop, listW, listH, 0x66000000);
            for (var product : shop.getProducts()) {
                itemList.addItem(new ProductListItem(product, shop, this::onEdit, this::onDelete));
            }
            addWidget(itemList);

            int formX = left + listW + 8;
            buildProductForm(formX, listTop, FORM_W, listH, shop);
        }
    }

    /** 构建商品编辑/新增表单 */
    private void buildProductForm(int formX, int formY, int formW, int formH, Shop shop) {
        boolean isAdd = viewMode == ViewMode.ADD;

        int fieldX = formX + 4;
        int fieldW = formW - 8;
        int fieldH = 16;
        int y = formY + 24;

        // —— 动态计算实际所需高度，避免 panel 过短导致重叠 ——
        int minFormH = 160; // 编辑模式最低
        if (isAdd) minFormH = 260; // 新增模式最低（含物品选择+补货周期）
        int actualFormH = Math.max(formH, minFormH);

        var formPanel = new Panel(formX, formY, formW, actualFormH, 0x88000000);
        addWidget(formPanel);

        Component titleText = isAdd
                ? Component.translatable("gui.mohistmc.shop.admin.add_title")
                : Component.translatable("gui.mohistmc.shop.admin.edit_title", editingProduct.stack().getHoverName());
        var titleLabel = new SimpleLabel(4, 4, titleText, 0xFFFFFFFF);
        formPanel.addChild(titleLabel);

        // —— 物品选择（仅新增模式） ——
        if (isAdd) {
            y += 4;
            var label = new SimpleLabel(4, y - formY,
                    Component.translatable("gui.mohistmc.shop.admin.item_id"), 0xFFCCCCCC);
            formPanel.addChild(label);
            y += 14;

            // 选择物品按钮
            int btnW2 = fieldW;
            addWidget(new CustomButton(fieldX, y, btnW2, fieldH + 4,
                    Component.literal(selectedItemId != null
                            ? "[" + selectedItemId + "]"
                            : Component.translatable("gui.mohistmc.shop.admin.select_item_btn").getString()),
                    0xFF333344)
                    .setTextColor(0xFFFFFFFF).setBorder(0xFF888888, 1).setBorderRadius(3)
                    .onClick(() -> {
                        itemPicker.show();
                        rebuildWidgets();
                    }));
            // 若已选物品，显示物品图标
            if (selectedItemStack != null && !selectedItemStack.isEmpty()) {
                var iconLabel = new SimpleLabel(4, y - formY + 2, Component.literal(""), 0xFFFFFFFF);
                iconLabel.setEnableItemIcons(true);
                iconLabel.setText(Component.literal("%" + selectedItemId + "%"));
                formPanel.addChild(iconLabel);
            }
            y += 28;
        }

        // —— 价格 ——
        var priceLabel = new SimpleLabel(4, y - formY,
                Component.translatable("gui.mohistmc.shop.admin.price"), 0xFFCCCCCC);
        formPanel.addChild(priceLabel);
        y += 14;
        priceEdit = new EditBox(minecraft.font, fieldX, y, fieldW, fieldH, Component.literal(""));
        priceEdit.setMaxLength(8);
        priceEdit.setValue(editingProduct != null ? String.valueOf(editingProduct.price()) : "1");
        addWidget(priceEdit);
        y += 24;

        // —— 库存 ——
        var stockLabel = new SimpleLabel(4, y - formY,
                Component.translatable("gui.mohistmc.shop.admin.stock"), 0xFFCCCCCC);
        formPanel.addChild(stockLabel);
        y += 14;
        stockEdit = new EditBox(minecraft.font, fieldX, y, fieldW, fieldH, Component.literal(""));
        stockEdit.setMaxLength(8);
        stockEdit.setValue(editingProduct != null ? String.valueOf(editingProduct.stock()) : "-1");
        stockEdit.setHint(Component.literal("-1 = ").append(Component.translatable("gui.mohistmc.shop.stock.unlimited")));
        addWidget(stockEdit);
        y += 24;

        // —— 分类 ——
        var catLabel = new SimpleLabel(4, y - formY,
                Component.translatable("gui.mohistmc.shop.admin.category"), 0xFFCCCCCC);
        formPanel.addChild(catLabel);
        y += 16;
        categoryDropdown = new DropdownMenu<Integer>(fieldX, y, fieldW, 16,
                Component.translatable("gui.mohistmc.shop.admin.select_category"), 0xFF333344)
                .setTextColor(0xFFFFFFFF).setBorderColor(0xFF888888)
                .setHoverItemColor(0xFF555555).setDropdownBgColor(0xFF333333);
        int catIdx = 0;
        for (var cat : shop.getCategories()) {
            categoryDropdown.addOption(cat.getId(), Component.translatable(cat.getLangKey()));
            if (editingProduct != null && editingProduct.categoryId() == cat.getId()) {
                categoryDropdown.setSelectedIndex(catIdx);
            }
            catIdx++;
        }
        addWidget(categoryDropdown);
        y += 24;

        // —— 补货周期（仅新增模式） ——
        if (isAdd) {
            var restockLabel = new SimpleLabel(4, y - formY,
                    Component.translatable("gui.mohistmc.shop.admin.restock"), 0xFFCCCCCC);
            formPanel.addChild(restockLabel);
            y += 16;
            restockDropdown = new DropdownMenu<RestockCycle>(fieldX, y, fieldW, 16,
                    Component.translatable("gui.mohistmc.shop.admin.select_restock"), 0xFF333344)
                    .setTextColor(0xFFFFFFFF).setBorderColor(0xFF888888)
                    .setHoverItemColor(0xFF555555).setDropdownBgColor(0xFF333333);
            restockDropdown.addOption(RestockCycle.NONE, Component.translatable("gui.mohistmc.shop.restock.none"));
            restockDropdown.addOption(RestockCycle.DAILY, Component.translatable("gui.mohistmc.shop.restock.daily"));
            restockDropdown.addOption(RestockCycle.WEEKLY, Component.translatable("gui.mohistmc.shop.restock.weekly"));
            addWidget(restockDropdown);
            y += 24;
        }

        // —— 按钮组（基于实际内容高度 + 8px 间距，避免与表单字段重叠） ——
        int btnY = Math.max(formY + actualFormH - 30, y + 8);
        int btnW = (formW - 12) / 2;

        addWidget(new CustomButton(fieldX, btnY, btnW, 20,
                Component.translatable("gui.mohistmc.shop.admin.save"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(this::onSaveProduct));

        addWidget(new CustomButton(fieldX + 8 + btnW, btnY, btnW, 20,
                Component.translatable("gui.mohistmc.shop.admin.cancel"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(this::switchToProductList));
    }

    // ======== 类别管理页面 ========

    private void renderCategoryPage(int left, int listTop, int guiW, int listH, Shop shop) {
        if (shop == null) return;

        // 返回商品列表按钮
        addWidget(new CustomButton(left, listTop, 80, 18,
                Component.translatable("gui.mohistmc.shop.admin.back"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> {
                    currentPage = AdminPage.PRODUCTS;
                    viewMode = ViewMode.LIST;
                    rebuildWidgets();
                }));

        if (viewMode == ViewMode.CATEGORY_ADD) {
            renderCategoryForm(left, listTop, guiW, listH, shop, null);
        } else if (viewMode == ViewMode.CATEGORY_EDIT) {
            renderCategoryForm(left, listTop, guiW, listH, shop,
                    shop.getCategory(editingCategoryId));
        } else {
            // 类别列表 + 新增按钮
            addWidget(new CustomButton(left + 90, listTop, 60, 18,
                    Component.translatable("gui.mohistmc.shop.admin.add_category"), 0xFF4CAF50)
                    .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                    .onClick(this::switchToAddCategory));

            int catListTop = listTop + 24;
            int catListH = listH - 24;
            itemList = new ScrollList(left, catListTop, guiW, catListH, 0x66000000);
            for (var cat : shop.getCategories()) {
                itemList.addItem(new CategoryListItem(cat, shop.getId(), this::onEditCategory, this::onDeleteCategory));
            }
            addWidget(itemList);
        }
    }

    private void renderCategoryForm(int left, int listTop, int guiW, int listH, Shop shop, ShopCategory editingCat) {
        boolean isAdd = editingCat == null;

        var formPanel = new Panel(left, listTop, guiW, listH, 0x88000000);
        addWidget(formPanel);

        Component titleText = isAdd
                ? Component.translatable("gui.mohistmc.shop.admin.add_cat_title")
                : Component.translatable("gui.mohistmc.shop.admin.edit_cat_title");
        var titleLabel = new SimpleLabel(4, 4, titleText, 0xFFFFFFFF);
        formPanel.addChild(titleLabel);

        int fieldX = left + 4;
        int fieldW = guiW - 8;
        int fieldH = 16;
        int y = listTop + 24;

        // 类别名称
        var nameLabel = new SimpleLabel(4, y - listTop,
                Component.translatable("gui.mohistmc.shop.admin.category_name"), 0xFFCCCCCC);
        formPanel.addChild(nameLabel);
        y += 14;
        categoryNameEdit = new EditBox(minecraft.font, fieldX, y, fieldW, fieldH, Component.literal(""));
        categoryNameEdit.setMaxLength(32);
        if (editingCat != null) {
            categoryNameEdit.setValue(editingCat.getName());
        }
        addWidget(categoryNameEdit);
        y += 24;

        // 语言键
        var langKeyLabel = new SimpleLabel(4, y - listTop,
                Component.translatable("gui.mohistmc.shop.admin.cat_lang_key"), 0xFFCCCCCC);
        formPanel.addChild(langKeyLabel);
        y += 14;
        categoryLangKeyEdit = new EditBox(minecraft.font, fieldX, y, fieldW, fieldH, Component.literal(""));
        categoryLangKeyEdit.setMaxLength(64);
        if (editingCat != null) {
            categoryLangKeyEdit.setValue(editingCat.getLangKey());
        } else {
            categoryLangKeyEdit.setHint(Component.literal("e.g. gui.mohistmc.shop.cat.custom"));
        }
        addWidget(categoryLangKeyEdit);
        y += 24;

        // 按钮组
        int btnY = listTop + listH - 30;
        int btnW = (guiW - 12) / 2;

        addWidget(new CustomButton(fieldX, btnY, btnW, 20,
                Component.translatable("gui.mohistmc.shop.admin.save"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> onSaveCategory(isAdd, shop)));

        addWidget(new CustomButton(fieldX + 8 + btnW, btnY, btnW, 20,
                Component.translatable("gui.mohistmc.shop.admin.cancel"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> {
                    viewMode = ViewMode.LIST;
                    rebuildWidgets();
                }));
    }

    // ======== 新增商店页面 ========

    private void renderAddShopForm(int left, int listTop, int guiW, int listH) {
        var formPanel = new Panel(left, listTop, guiW, listH, 0x88000000);
        addWidget(formPanel);

        var titleLabel = new SimpleLabel(4, 4,
                Component.translatable("gui.mohistmc.shop.admin.add_shop_title"), 0xFFFFFFFF);
        formPanel.addChild(titleLabel);

        int fieldX = left + 4;
        int fieldW = guiW - 8;
        int fieldH = 16;
        int y = listTop + 24;

        // 商店 ID
        var idLabel = new SimpleLabel(4, y - listTop,
                Component.translatable("gui.mohistmc.shop.admin.shop_id"), 0xFFCCCCCC);
        formPanel.addChild(idLabel);
        y += 14;
        var shopIdEdit = new EditBox(minecraft.font, fieldX, y, fieldW, fieldH, Component.literal(""));
        shopIdEdit.setMaxLength(32);
        shopIdEdit.setHint(Component.literal("e.g. my_shop"));
        addWidget(shopIdEdit);
        // 保存引用用于 onSaveShop
        shopNameEdit = shopIdEdit; // reuse shopNameEdit temporarily for shop ID
        y += 24;

        // 商店名称
        var nameLabel = new SimpleLabel(4, y - listTop,
                Component.translatable("gui.mohistmc.shop.admin.shop_name"), 0xFFCCCCCC);
        formPanel.addChild(nameLabel);
        y += 14;
        var shopNameEditBox = new EditBox(minecraft.font, fieldX, y, fieldW, fieldH, Component.literal(""));
        shopNameEditBox.setMaxLength(32);
        shopNameEditBox.setHint(Component.literal("e.g. My Shop"));
        addWidget(shopNameEditBox);
        // 保存引用用于 onSaveShop
        priceEdit = shopNameEditBox; // reuse priceEdit temporarily for shop name
        y += 24;

        // 按钮组
        int btnY = listTop + listH - 30;
        int btnW = (guiW - 12) / 2;

        addWidget(new CustomButton(fieldX, btnY, btnW, 20,
                Component.translatable("gui.mohistmc.shop.admin.save"), 0xFF4CAF50)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(this::onSaveShop));

        addWidget(new CustomButton(fieldX + 8 + btnW, btnY, btnW, 20,
                Component.translatable("gui.mohistmc.shop.admin.cancel"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3)
                .onClick(() -> {
                    currentPage = AdminPage.PRODUCTS;
                    viewMode = ViewMode.LIST;
                    rebuildWidgets();
                }));
    }

    // ======== 视图切换 ========

    private void switchToProductList() {
        viewMode = ViewMode.LIST;
        currentPage = AdminPage.PRODUCTS;
        editingProduct = null;
        editingCategoryId = -1;
        selectedItemId = null;
        selectedItemStack = ItemStack.EMPTY;
        priceEdit = null;
        stockEdit = null;
        categoryDropdown = null;
        restockDropdown = null;
        categoryNameEdit = null;
        categoryLangKeyEdit = null;
        shopNameEdit = null;
        if (itemPicker != null) itemPicker.hide();
        rebuildWidgets();
    }

    private void switchToEditProduct(ShopProduct product) {
        viewMode = ViewMode.EDIT;
        currentPage = AdminPage.PRODUCTS;
        editingProduct = product;
        rebuildWidgets();
    }

    private void switchToAddProduct() {
        viewMode = ViewMode.ADD;
        currentPage = AdminPage.PRODUCTS;
        editingProduct = null;
        selectedItemId = null;
        selectedItemStack = ItemStack.EMPTY;
        if (itemPicker != null) itemPicker.hide();
        rebuildWidgets();
    }

    private void switchToCategoryManage() {
        viewMode = ViewMode.LIST;
        currentPage = AdminPage.CATEGORIES;
        editingProduct = null;
        editingCategoryId = -1;
        selectedItemId = null;
        selectedItemStack = ItemStack.EMPTY;
        priceEdit = null;
        stockEdit = null;
        categoryDropdown = null;
        restockDropdown = null;
        categoryNameEdit = null;
        categoryLangKeyEdit = null;
        shopNameEdit = null;
        if (itemPicker != null) itemPicker.hide();
        rebuildWidgets();
    }

    private void switchToAddCategory() {
        viewMode = ViewMode.CATEGORY_ADD;
        editingCategoryId = -1;
        rebuildWidgets();
    }

    private void switchToEditCategory(int categoryId) {
        viewMode = ViewMode.CATEGORY_EDIT;
        editingCategoryId = categoryId;
        rebuildWidgets();
    }

    private void switchToAddShop() {
        currentPage = AdminPage.ADD_SHOP;
        viewMode = ViewMode.LIST;
        editingProduct = null;
        selectedItemId = null;
        selectedItemStack = ItemStack.EMPTY;
        priceEdit = null;
        stockEdit = null;
        categoryNameEdit = null;
        categoryLangKeyEdit = null;
        shopNameEdit = null;
        if (itemPicker != null) itemPicker.hide();
        rebuildWidgets();
    }

    // ======== 回调 ========

    private void onEdit(ShopProduct product) {
        switchToEditProduct(product);
    }

    private void onDelete(ShopProduct product) {
        ClientPacketDistributor.sendToServer(ShopEditPayload.delete(product.id()));
        // 乐观更新：客户端立即删除本地商品并刷新列表
        ShopData.removeProduct(product.shopId(), product.id());
        rebuildWidgets();
    }

    private void onEditCategory(ShopCategory cat, String shopId) {
        switchToEditCategory(cat.getId());
    }

    private void onDeleteCategory(ShopCategory cat, String shopId) {
        ClientPacketDistributor.sendToServer(ShopEditPayload.deleteCategory(shopId, cat.getId()));
        // 乐观更新：客户端立即删除本地类别并刷新列表
        ShopData.removeCategory(shopId, cat.getId());
        rebuildWidgets();
    }

    private void onSaveProduct() {
        var shop = ShopData.getShop(currentShopId);
        if (shop == null) return;

        if (viewMode == ViewMode.EDIT && editingProduct != null) {
            int price = parseIntSafe(priceEdit.getValue(), 1);
            int stock = parseIntSafe(stockEdit.getValue(), -1);
            var catId = categoryDropdown.getSelectedValue();
            if (catId == null) catId = editingProduct.categoryId();
            ClientPacketDistributor.sendToServer(ShopEditPayload.edit(currentShopId, editingProduct.id(), price, stock, catId));
            // 乐观更新：客户端立即修改本地商品
            ShopData.modifyProduct(currentShopId, editingProduct.id(), price, stock, catId);
        } else if (viewMode == ViewMode.ADD) {
            if (selectedItemId == null || selectedItemStack.isEmpty()) return;
            int price = parseIntSafe(priceEdit.getValue(), 1);
            int stock = parseIntSafe(stockEdit.getValue(), -1);
            var catId = categoryDropdown.getSelectedValue();
            if (catId == null && !shop.getCategories().isEmpty()) {
                catId = shop.getCategories().get(0).getId();
            }
            if (catId == null) catId = -1;
            var cycle = restockDropdown.getSelectedValue();
            if (cycle == null) cycle = RestockCycle.NONE;
            ClientPacketDistributor.sendToServer(ShopEditPayload.add(currentShopId, selectedItemStack, price, stock, catId, cycle.ordinal()));
            // 乐观更新：客户端立即添加本地商品
            ShopData.addProduct(currentShopId, selectedItemStack, price, catId, stock, cycle);
        }
        switchToProductList();
    }

    private void onSaveCategory(boolean isAdd, Shop shop) {
        String name = categoryNameEdit.getValue().trim();
        String langKey = categoryLangKeyEdit.getValue().trim();
        if (name.isEmpty() || langKey.isEmpty()) return;

        if (isAdd) {
            ClientPacketDistributor.sendToServer(ShopEditPayload.addCategory(shop.getId(), name, langKey));
            // 乐观更新：客户端立即添加本地类别
            ShopData.addCategory(shop.getId(), name, langKey);
        } else {
            ClientPacketDistributor.sendToServer(ShopEditPayload.editCategory(shop.getId(), editingCategoryId, name, langKey));
            // 乐观更新：客户端立即修改本地类别
            ShopData.modifyCategory(shop.getId(), editingCategoryId, name, langKey);
        }
        viewMode = ViewMode.LIST;
        rebuildWidgets();
    }

    private void onSaveShop() {
        // shopNameEdit was reused as shop ID edit, priceEdit was reused as shop name edit
        if (!(shopNameEdit instanceof EditBox)) return;
        if (!(priceEdit instanceof EditBox)) return;
        String shopId = shopNameEdit.getValue().trim().replaceAll("\\s+", "_");
        String shopName = priceEdit.getValue().trim();
        if (shopId.isEmpty() || shopName.isEmpty()) return;

        ClientPacketDistributor.sendToServer(ShopEditPayload.addShop(shopId, shopName));
        // 乐观更新：客户端立即创建本地商店，使 UI 立即可用（服务端同步稍后会覆盖确认）
        ShopData.createShop(shopId, shopName);
        currentShopId = shopId;
        currentPage = AdminPage.PRODUCTS;
        viewMode = ViewMode.LIST;
        rebuildWidgets();
    }

    // ======== 鼠标/键盘事件 ========

    // mouseClicked 由 EnhancedScreen 统一处理，modals 优先于 widgets

    @Override
    public boolean keyPressed(KeyEvent event) {
        // 物品选择器搜索框的键盘事件
        if (itemPicker != null && itemPicker.isVisible()) {
            var searchBox = itemPicker.getSearchBox();
            if (searchBox != null && searchBox.isFocused()) {
                // 让 searchBox 处理按键（NeoForge 使用 KeyEvent 对象）
                if (searchBox.keyPressed(event)) {
                    return true;
                }
                return true; // 消费所有按键，防止 ESC 关闭屏幕
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        // 物品选择器搜索框的字符输入
        if (itemPicker != null && itemPicker.isVisible()) {
            var searchBox = itemPicker.getSearchBox();
            if (searchBox != null && searchBox.isFocused()) {
                if (searchBox.charTyped(event)) {
                    return true;
                }
                return true;
            }
        }
        return super.charTyped(event);
    }

    // ======== 工具 ========

    private static int parseIntSafe(String text, int defaultVal) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static ItemStack resolveItem(String idStr) {
        try {
            var id = net.minecraft.resources.Identifier.parse(idStr.toLowerCase(java.util.Locale.ROOT));
            var opt = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(id);
            if (opt.isPresent()) {
                return new ItemStack(opt.get().value(), 1);
            }
        } catch (Exception ignored) {
        }
        return ItemStack.EMPTY;
    }

    // ======== 商品列表项 ========

    private static class ProductListItem extends ScrollListItem {
        private static final int ITEM_H = 22;
        private static final int BTN_W = 30;
        private static final int BTN_GAP = 3;

        private final ShopProduct product;
        private final Shop shop;
        private final Consumer<ShopProduct> editAction;
        private final Consumer<ShopProduct> deleteAction;

        ProductListItem(ShopProduct product, Shop shop,
                        Consumer<ShopProduct> editAction,
                        Consumer<ShopProduct> deleteAction) {
            this.product = product;
            this.shop = shop;
            this.editAction = editAction;
            this.deleteAction = deleteAction;
            setHeight(ITEM_H);
        }

        @Override
        public boolean handleClick(int rx, int ry, int w) {
            int editBtnX = w - (BTN_W + BTN_GAP) * 2;
            int delBtnX = w - BTN_W;
            if (ry >= 0 && ry < ITEM_H) {
                if (rx >= editBtnX && rx < editBtnX + BTN_W) {
                    editAction.accept(product);
                    return true;
                }
                if (rx >= delBtnX && rx < delBtnX + BTN_W) {
                    deleteAction.accept(product);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
            int a = alpha & 0xFF000000;
            g.fill(x, y, x + w, y + ITEM_H, a | (hovered ? 0xFF333344 : 0xFF2A2A3A));

            var stack = product.stack();
            g.item(stack, x + 3, y + (ITEM_H - 16) / 2);

            var font = font();
            var name = stack.getHoverName();

            // 响应式布局：4列（名称/价格/库存/分类），从图标后到按钮前均分
            int btnsArea = (BTN_W + BTN_GAP) * 2; // 编辑+删除按钮区域
            int textArea = w - 22 - btnsArea - 6; // 减去图标、按钮和边距
            int colW = Math.max(30, textArea / 4);

            int nameColX = x + 22;
            int priceColX = nameColX + colW;
            int stockColX = priceColX + colW;
            int catColX = stockColX + colW;

            // 物品名
            var displayName = font.width(name) > colW - 2
                    ? Component.literal(font.plainSubstrByWidth(name.getString(), colW - 4) + "..")
                    : name;
            g.text(font, displayName, nameColX, y + (ITEM_H - font.lineHeight) / 2, a | 0xFFFFFFFF);

            // 价格
            String priceStr = "¥" + product.price();
            g.text(font, priceStr, priceColX, y + (ITEM_H - font.lineHeight) / 2, a | 0xFFFFD700);

            // 库存
            String stockStr = product.stock() < 0 ? "∞" : String.valueOf(product.stock());
            g.text(font, stockStr, stockColX, y + (ITEM_H - font.lineHeight) / 2, a | 0xFFAAAAAA);

            // 分类名
            String catName = "";
            if (shop != null) {
                var cat = shop.getCategory(product.categoryId());
                if (cat != null) {
                    catName = Component.translatable(cat.getLangKey()).getString();
                }
            }
            g.text(font, catName, catColX, y + (ITEM_H - font.lineHeight) / 2, a | 0xFF888888);

            int editBtnX = x + w - (BTN_W + BTN_GAP) * 2;
            g.fill(editBtnX, y + 2, editBtnX + BTN_W, y + ITEM_H - 2, a | 0xFF333344);
            g.text(font, Component.literal("编辑"), editBtnX + 2, y + (ITEM_H - font.lineHeight) / 2, a | 0xFF4CAF50);

            int delBtnX = x + w - BTN_W;
            g.fill(delBtnX, y + 2, delBtnX + BTN_W, y + ITEM_H - 2, a | 0xFF333344);
            g.text(font, Component.literal("删除"), delBtnX + 2, y + (ITEM_H - font.lineHeight) / 2, a | 0xFFFF5555);
        }
    }

    // ======== 类别列表项 ========

    private static class CategoryListItem extends ScrollListItem {
        private static final int ITEM_H = 20;
        private static final int BTN_W = 30;
        private static final int BTN_GAP = 3;

        private final ShopCategory cat;
        private final String shopId;
        private final CategoryAction editAction;
        private final CategoryAction deleteAction;

        interface CategoryAction {
            void accept(ShopCategory cat, String shopId);
        }

        CategoryListItem(ShopCategory cat, String shopId,
                         CategoryAction editAction,
                         CategoryAction deleteAction) {
            this.cat = cat;
            this.shopId = shopId;
            this.editAction = editAction;
            this.deleteAction = deleteAction;
            setHeight(ITEM_H);
        }

        @Override
        public boolean handleClick(int rx, int ry, int w) {
            int editBtnX = w - (BTN_W + BTN_GAP) * 2;
            int delBtnX = w - BTN_W;
            if (ry >= 0 && ry < ITEM_H) {
                if (rx >= editBtnX && rx < editBtnX + BTN_W) {
                    editAction.accept(cat, shopId);
                    return true;
                }
                if (rx >= delBtnX && rx < delBtnX + BTN_W) {
                    deleteAction.accept(cat, shopId);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
            int a = alpha & 0xFF000000;
            g.fill(x, y, x + w, y + ITEM_H, a | (hovered ? 0xFF333344 : 0xFF2A2A3A));

            var font = font();
            String display = Component.translatable(cat.getLangKey()).getString();
            String info = "[" + cat.getId() + "] " + display + " (" + cat.getName() + ")";
            g.text(font, info, x + 4, y + (ITEM_H - font.lineHeight) / 2, a | 0xFFFFFFFF);

            int editBtnX = x + w - (BTN_W + BTN_GAP) * 2;
            g.fill(editBtnX, y + 2, editBtnX + BTN_W, y + ITEM_H - 2, a | 0xFF333344);
            g.text(font, Component.literal("编辑"), editBtnX + 2, y + (ITEM_H - font.lineHeight) / 2, a | 0xFF4CAF50);

            int delBtnX = x + w - BTN_W;
            g.fill(delBtnX, y + 2, delBtnX + BTN_W, y + ITEM_H - 2, a | 0xFF333344);
            g.text(font, Component.literal("删除"), delBtnX + 2, y + (ITEM_H - font.lineHeight) / 2, a | 0xFFFF5555);
        }
    }

    /** 页面类型 */
    private enum AdminPage {
        PRODUCTS, CATEGORIES, ADD_SHOP
    }

    /** 视图模式 */
    private enum ViewMode {
        LIST, EDIT, ADD, CATEGORY_ADD, CATEGORY_EDIT
    }
}