package com.mohistmc.mod.client.gui;

import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class MyCustomScreen extends EnhancedScreen {

    public static Identifier BG_COLOR = Identifier.fromNamespaceAndPath("mohistmc", "textures/gui/bg.png");

    public MyCustomScreen() {
        super(Component.literal("我的界面"), BG_COLOR); // 全屏
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();

        // ======== 根面板：全屏 VERTICAL 布局，自动适配窗口 ========
        var root = new Panel(0, 0, sw, sh, 0)
                .setLayout(Panel.LayoutDirection.VERTICAL, 8);

        // ────────────────────────────────────
        // 顶部行：信息面板 + 弹性间隔 + 操作面板（1.5:1 比例 + 响应式内边距）
        // ────────────────────────────────────
        int topH = sw * 4 / 15; // 1.5:1 比例
        int pad = Math.max(8, topH / 8);
        var topRow = new Panel(0, 0, sw, topH, 0)
                .setLayout(Panel.LayoutDirection.HORIZONTAL, 8);

        var infoPanel = new Panel(0, 0, 0, topH, ColorUtil.fromHex("#2D2D2D"))
                .setBorder(ColorUtil.fromHex("#CCCCCC"), 2)
                .setAlpha(0.85f)
                .setFlexGrow(1)
                .onClick(p -> System.out.println("左侧信息面板被点击"));
        infoPanel.addChild(new SimpleLabel(pad, pad, Component.literal("信息面板"), 0xFFFFFFFF));
        infoPanel.addChild(new SimpleLabel(pad, pad * 2 + 4, Component.literal("这里是内容区"), 0xFFAAAAAA));
        topRow.addChild(infoPanel);

        var actionPanel = new Panel(0, 0, 0, topH, ColorUtil.fromHex("#3D1F2A"))
                .setBorder(ColorUtil.fromHex("#9d2933"), 2)
                .setAlpha(0.85f)
                .setFlexGrow(1)
                .onClick(p -> System.out.println("右侧操作面板被点击"));
        actionPanel.addChild(new SimpleLabel(pad, pad, Component.literal("操作面板"), 0xFFFFFFFF));
        actionPanel.addChild(new SimpleLabel(pad, pad * 2 + 4, Component.literal("点击面板执行操作"), 0xFFCCCCCC));
        topRow.addChild(actionPanel);

        // 剩余空间：滚动列表演示
        var topRemaining = new Panel(0, 0, 0, topH, ColorUtil.fromHex("#1A1A2E"))
                .setAlpha(0.5f)
                .setFlexGrow(1)
                .setBorder(ColorUtil.fromHex("#888888"), 1)
                .setLayout(Panel.LayoutDirection.HORIZONTAL, 4);
        var list = new ScrollList(0, 0, 0, topH, 0x00000000)
                .setFlexGrow(1)
                .setHoverColor(0x22FFFFFF)
                .setScrollbarColor(0xFF888888);
        // 添加自定义子项
        list.addItem(new LabelItem(Component.literal("§n§l设置选项"), 0xFFFFAA00).setHeight(22));
        list.addItem(new CheckboxItem(Component.literal("启用自动保存"), true)
                .onChange(v -> System.out.println("自动保存: " + v)));
        list.addItem(new CheckboxItem(Component.literal("显示通知"), false)
                .onChange(v -> System.out.println("通知: " + v)));
        list.addItem(new ToggleItem(Component.literal("主题"),
                List.of(Component.literal("深色"), Component.literal("浅色"), Component.literal("跟随系统")), 0)
                .onChange(v -> System.out.println("主题: " + v)));
        list.addItem(new SliderItem(Component.literal("音量"), 0.7f)
                .setFillColor(0xFF2196F3)
                .onChange(v -> System.out.println("音量: " + (int)(v * 100) + "%")));
        list.addItem(new SliderItem(Component.literal("亮度"), 0.5f)
                .setFillColor(0xFFFFC107)
                .onChange(v -> System.out.println("亮度: " + (int)(v * 100) + "%")));
        list.addItem(new ToggleItem(Component.literal("难度"),
                List.of(Component.literal("和平"), Component.literal("简单"), Component.literal("普通"), Component.literal("困难")), 2)
                .setValueColor(0xFFFF5555)
                .onChange(v -> System.out.println("难度变更")));
        topRemaining.addChild(list);
        topRow.addChild(topRemaining);
        topRow.addChild(new Panel(0, 0, 8, 0, 0).setFlexGrow(0)); // 右边距（保持与左边距一致）

        root.addChild(topRow);

        // ────────────────────────────────────
        // 渐变按钮行
        // ────────────────────────────────────
        var gradientRow = new Panel(0, 0, sw, 22, 0)
                .setLayout(Panel.LayoutDirection.HORIZONTAL, 6);

        gradientRow.addChild(new CustomButton(0, 0, 70, 22, Component.literal("⬇ 渐变"), ColorUtil.fromHex("#333333"))
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(6)
                .setGradient(0xFF43A047, 0xFF1B5E20, CustomButton.GradientDirection.TOP_BOTTOM)
                .setGlow(0x40A5D6A7)
                .withDefaultClickSound()
                .withDefaultHoverSound()
                .onClick(() -> System.out.println("TOP_BOTTOM 渐变")));

        gradientRow.addChild(new CustomButton(0, 0, 70, 22, Component.literal("⬆ 渐变"), ColorUtil.fromHex("#333333"))
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(6)
                .setGradient(0xFF43A047, 0xFF1B5E20, CustomButton.GradientDirection.BOTTOM_TOP)
                .setGlow(0x40A5D6A7)
                .withDefaultClickSound()
                .withDefaultHoverSound()
                .onClick(() -> System.out.println("BOTTOM_TOP 渐变")));

        gradientRow.addChild(new CustomButton(0, 0, 80, 22, Component.literal("➡ 渐变"), ColorUtil.fromHex("#333333"))
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(6)
                .setGradient(0xFF1565C0, 0xFF42A5F5, CustomButton.GradientDirection.LEFT_RIGHT)
                .setGlow(0x4042A5F5)
                .withDefaultClickSound()
                .withDefaultHoverSound()
                .onClick(() -> System.out.println("LEFT_RIGHT 渐变")));

        gradientRow.addChild(new CustomButton(0, 0, 80, 22, Component.literal("⬅ 渐变"), ColorUtil.fromHex("#333333"))
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(6)
                .setGradient(0xFF1565C0, 0xFF42A5F5, CustomButton.GradientDirection.RIGHT_LEFT)
                .setGlow(0x4042A5F5)
                .withDefaultClickSound()
                .withDefaultHoverSound()
                .onClick(() -> System.out.println("RIGHT_LEFT 渐变")));

        gradientRow.addChild(new CustomButton(0, 0, 94, 22, Component.literal("✨ 渐变+放大"), ColorUtil.fromHex("#333333"))
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(8)
                .setGradient(0xFF7B1FA2, 0xFFCE93D8, CustomButton.GradientDirection.LEFT_RIGHT)
                .setGlow(0x40CE93D8)
                .setHoverScale(1.10f)
                .withDefaultClickSound()
                .withDefaultHoverSound()
                .onClick(() -> System.out.println("渐变+放大组合按钮")));

        root.addChild(gradientRow);

        // ────────────────────────────────────
        // 下拉菜单演示行
        // ────────────────────────────────────
        var dropdownRow = new Panel(0, 0, sw, 22, 0)
                .setLayout(Panel.LayoutDirection.HORIZONTAL, 10);

        dropdownRow.addChild(new DropdownMenu<String>(0, 0, 130, 22,
                Component.literal("选择语言"), ColorUtil.fromHex("#444444"))
                .setTextColor(0xFFFFFFFF)
                .setHoverItemColor(ColorUtil.fromHex("#5A5A5A"))
                .setDropdownBgColor(ColorUtil.fromHex("#2D2D2D"))
                .setBorderColor(ColorUtil.fromHex("#888888"))
                .setItemHeight(18)
                .setMaxVisibleItems(6)
                .addOption("zh_cn", Component.literal("简体中文"))
                .addOption("zh_tw", Component.literal("繁體中文"))
                .addOption("en_us", Component.literal("English"))
                .addOption("ja_jp", Component.literal("日本語"))
                .addOption("ko_kr", Component.literal("한국어"))
                .addOption("fr_fr", Component.literal("Français"))
                .addOption("de_de", Component.literal("Deutsch"))
                .onSelect(v -> System.out.println("选中语言: " + v)));

        dropdownRow.addChild(new DropdownMenu<Integer>(0, 0, 100, 22,
                Component.literal("难度"), ColorUtil.fromHex("#3D1F2A"))
                .setTextColor(0xFFFFFFFF)
                .setHoverItemColor(ColorUtil.fromHex("#5A2A35"))
                .setDropdownBgColor(ColorUtil.fromHex("#2D1520"))
                .setBorderColor(ColorUtil.fromHex("#9d2933"))
                .setItemHeight(18)
                .addOption(0, Component.literal("☀ 和平"))
                .addOption(1, Component.literal("🌙 简单"))
                .addOption(2, Component.literal("⚡ 普通"))
                .addOption(3, Component.literal("🔥 困难"))
                .setSelectedIndex(2)
                .onSelect(v -> System.out.println("选中难度: " + v)));

        root.addChild(dropdownRow);

        // ────────────────────────────────────
        // 演示行：左侧头像面板 + 右侧 Swap 标签切换
        // ────────────────────────────────────
        var demoRow = new Panel(0, 0, sw, 150, 0)
                .setLayout(Panel.LayoutDirection.HORIZONTAL, 10);

        // 左侧：头像演示（较窄）
        var avatarPanel = new Panel(0, 0, 230, 150, ColorUtil.fromHex("#1A1A2E"))
                .setAlpha(0.7f)
                .setLayout(Panel.LayoutDirection.VERTICAL, 6);
        avatarPanel.addChild(new Panel(0, 0, 0, 0, 0).setFlexGrow(1)); // 上边距

        var r1 = new Panel(0, 0, avatarPanel.width, 30, 0).setLayout(Panel.LayoutDirection.HORIZONTAL, 10);
        r1.addChild(new Avatar(0, 0, 28, "M").setBackground(ColorUtil.fromHex("#9d2933"))
                .setBorder(ColorUtil.fromHex("#FFFFFF"), 2).setShape(Avatar.Shape.CIRCLE));
        r1.addChild(new SimpleLabel(0, 6, Component.literal("首字母"), 0xFFFFFFFF));
        avatarPanel.addChild(r1);

        var r2 = new Panel(0, 0, avatarPanel.width, 30, 0).setLayout(Panel.LayoutDirection.HORIZONTAL, 10);
        r2.addChild(new Avatar(0, 0, 28).setTexture(Identifier.fromNamespaceAndPath("mohistmc", "textures/gui/avatar.png"))
                .setBorderTexture(Identifier.fromNamespaceAndPath("mohistmc", "textures/gui/avatar_border.png")));
        r2.addChild(new SimpleLabel(0, 6, Component.literal("纹理头像"), 0xFFFFFFFF));
        avatarPanel.addChild(r2);

        var r3 = new Panel(0, 0, avatarPanel.width, 24, 0).setLayout(Panel.LayoutDirection.HORIZONTAL, 6);
        r3.addChild(new SimpleLabel(0, 0, Component.literal("形状:"), 0xFFCCCCCC));
        r3.addChild(new Avatar(0, 0, 16).setBackground(ColorUtil.fromHex("#FF9800")).setShape(Avatar.Shape.SQUARE));
        r3.addChild(new Avatar(0, 0, 16).setBackground(ColorUtil.fromHex("#9C27B0")).setShape(Avatar.Shape.CIRCLE));
        r3.addChild(new Avatar(0, 0, 16).setBackground(ColorUtil.fromHex("#2196F3")).setShape(Avatar.Shape.ROUNDED).setRoundRadius(4));
        avatarPanel.addChild(r3);

        avatarPanel.addChild(new Panel(0, 0, 0, 0, 0).setFlexGrow(1)); // 下边距
        demoRow.addChild(avatarPanel);

        // 右侧：Swap 标签切换
        var swapInfo = new Panel(0, 0, 0, 0, ColorUtil.fromHex("#2D2D2D"))
                .setAlpha(0.5f);
        swapInfo.addChild(new SimpleLabel(12, 12, Component.literal("信息面板"), 0xFFFFFFFF));
        swapInfo.addChild(new SimpleLabel(12, 34, Component.literal("这是信息标签页的内容"), 0xFFAAAAAA));

        var swapSetting = new Panel(0, 0, 0, 0, ColorUtil.fromHex("#2D1F2A"))
                .setAlpha(0.5f);
        swapSetting.addChild(new SimpleLabel(12, 12, Component.literal("⚙ 设置面板"), 0xFFFFFFFF));
        swapSetting.addChild(new SimpleLabel(12, 34, Component.literal("这是设置标签页的内容"), 0xFFCCCCCC));

        var swap = new Swap(0, 0, 300, 150, ColorUtil.fromHex("#2A2A3E"))
                .addPage(swapInfo, Component.literal("信息"))
                .addPage(swapSetting, Component.literal("设置"))
                .setActiveTabColor(ColorUtil.fromHex("#9d2933"))
                .setInactiveTabColor(ColorUtil.fromHex("#3A3A4E"))
                .setTabHeight(18)
                .setOnSwap(i -> System.out.println("切换到标签: " + i));
        demoRow.addChild(swap);
        demoRow.addChild(new Panel(0, 0, 0, 0, 0).setFlexGrow(1)); // 右边距
        root.addChild(demoRow);

        // ────────────────────────────────────
        // 弹性撑杆：占满剩余空间，把底部内容推到窗口底部
        // ────────────────────────────────────
        root.addChild(new Panel(0, 0, 0, 0, 0).setFlexGrow(1));

        // ────────────────────────────────────
        // 状态栏
        // ────────────────────────────────────
        var bottomPanel = new Panel(0, 0, sw, 34, ColorUtil.fromHex("#1a1a2e"))
                .setAlpha(0.6f)
                .onClick(p -> System.out.println("状态栏面板被点击"));
        bottomPanel.addChild(new SimpleLabel(12, 8, Component.literal("状态栏 | 就绪"), 0xFF88AAFF));
        root.addChild(bottomPanel);

        // ────────────────────────────────────
        // 底部按钮行
        // ────────────────────────────────────
        var bottomRow = new Panel(0, 0, sw, 18, 0)
                .setLayout(Panel.LayoutDirection.HORIZONTAL, 6);

        // 禁用/启用演示 — 用一个可变的引用容器绕过 lambda 的 effectively final 限制
        var ref = new Object() { CustomButton toggle; };
        var saveBtn = new CustomButton(0, 0, 64, 18, Component.literal("保存"), ColorUtil.fromHex("#9d2933"))
                .setTextColor(0xFFFFFFFF)
                .setHoverColor(ColorUtil.fromHex("#B33040"))
                .setBorderRadius(6)
                .setGlow(0x60FFFFFF)
                .setAlpha(0.95f)
                .withDefaultClickSound()
                .onClick(() -> System.out.println("保存被点击"));
        bottomRow.addChild(saveBtn);

        ref.toggle = new CustomButton(0, 0, 64, 18, Component.literal("禁用"), ColorUtil.fromHex("#555555"))
                .setTextColor(0xFFFFFFFF)
                .setHoverColor(ColorUtil.fromHex("#888888"))
                .setBorderRadius(6)
                .setBorder(ColorUtil.fromHex("#999999"), 1)
                .withDefaultClickSound()
                .onClick(() -> {
                    boolean next = !saveBtn.isEnabled();
                    saveBtn.setEnabled(next);
                    ref.toggle.setText(next ? Component.literal("禁用") : Component.literal("启用"));
                });
        bottomRow.addChild(ref.toggle);

        bottomRow.addChild(new CustomButton(0, 0, 64, 18, Component.literal("取消"), ColorUtil.fromHex("#555555"))
                .setTextColor(0xFFFFFFFF)
                .setHoverColor(ColorUtil.fromHex("#777777"))
                .setBorderRadius(6)
                .setBorder(ColorUtil.fromHex("#888888"), 1)
                .withDefaultClickSound()
                .onClick(() -> System.out.println("取消被点击")));

        // 删除按钮 → 弹出确认对话框
        var deleteModal = new Modal(
                Component.literal("确认删除"),
                Component.literal("确定要删除此项目吗？此操作无法撤销。"),
                () -> System.out.println("✓ 已确认删除"),
                () -> System.out.println("✗ 已取消删除")
        ).setDialogWidth(280);
        addModal(deleteModal);

        bottomRow.addChild(new CustomButton(0, 0, 64, 18, Component.literal("删除"), ColorUtil.fromHex("#CC3333"))
                .setTextColor(0xFFFFFFFF)
                .setHoverColor(ColorUtil.fromHex("#FF4444"))
                .setBorder(ColorUtil.fromHex("#FF6666"), 1)
                .withDefaultClickSound()
                .onClick(() -> {
                    System.out.println("删除被点击 — 弹出对话框");
                    deleteModal.show();
                }));

        bottomRow.addChild(new CustomButton(0, 0, 80, 18, Component.literal("✨ 放大"), ColorUtil.fromHex("#2E7D32"))
                .setTextColor(0xFFFFFFFF)
                .setHoverColor(ColorUtil.fromHex("#43A047"))
                .setBorderRadius(8)
                .setGlow(0x40A5D6A7)
                .setHoverScale(1.12f)
                .withDefaultClickSound()
                .onClick(() -> System.out.println("放大按钮被点击")));

        bottomRow.addChild(new CustomButton(0, 0, 80, 18, Component.literal("⚙ 设置"), ColorUtil.fromHex("#ca6924"))
                .setTextColor(0xFFFFFFFF)
                .setHoverColor(ColorUtil.fromHex("#e29c45"))
                .setBorderRadius(4)
                .setGlow(0x40A5D6A7)
                .withDefaultClickSound()
                .onClick(() -> this.minecraft.gui.setScreen(new OptionsScreen(this, this.minecraft.options, false))));

        root.addChild(bottomRow);

        // ======== 将根面板添加到屏幕 ========
        addWidget(root);
    }
}
