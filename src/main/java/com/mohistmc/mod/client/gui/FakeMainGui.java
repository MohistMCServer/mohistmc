package com.mohistmc.mod.client.gui;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.api.gui.Avatar;
import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.api.gui.SimpleLabel;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 自定义主菜单 — 仿主流 Mod 侧边栏风格
 */
@OnlyIn(Dist.CLIENT)
public class FakeMainGui extends EnhancedScreen {

    private static final Identifier BG = Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/gui/bg.png");

    public FakeMainGui() {
        super(Component.translatable("narrator.screen.title"), BG);
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();
        int panelW = 100;

        // ====== 右侧边栏：半透黑底 ======
        var sidebar = new Panel(0, 0, panelW, sh, 0x77000000)
                .setRightAnchored(true);

        // ---- Logo（左上） ----
        var logo = new Avatar(18, 5, 64);
        logo.setTexture(Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/item/logo.png"));
        sidebar.addChild(logo);

        // ---- 按钮组（垂直居中） ----
        int btnY = sh / 2 - 40;

        var singleplayer = new CustomButton(5, btnY, 90, 20,
                Component.translatable("menu.singleplayer"), 0xFF555555)
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(4)
                .setTooltip(Component.translatable("menu.singleplayer"))
                .onClick(() -> minecraft.gui.setScreen(new SelectWorldScreen(this)));
        sidebar.addChild(singleplayer);

        var multiplayer = new CustomButton(5, btnY + 22, 90, 20,
                Component.literal("多人游戏"), 0xFF555555)
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(4)
                .setTooltip(Component.translatable("menu.multiplayer"))
                .onClick(() -> {
                    // minecraft.gui.setScreen(new ModernServerListScreen());
                    minecraft.gui.setScreen(new JoinMultiplayerScreen(this));
                });
        sidebar.addChild(multiplayer);

        var options = new CustomButton(5, btnY + 44, 90, 20,
                Component.translatable("menu.options"), 0xFF555555)
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(4)
                .setTooltip(Component.translatable("menu.options"))
                .onClick(() -> minecraft.gui.setScreen(new OptionsScreen(this, minecraft.options, false)));
        sidebar.addChild(options);

        // ---- Demo 按钮 ----
        var demo = new CustomButton(5, btnY + 66, 90, 20,
                Component.literal("🧪 演示界面"), 0xFF333333)
                .setTextColor(0xFFFFFFFF)
                .setBorderRadius(4)
                .setTooltip(Component.literal("打开组件演示界面"))
                .onClick(() -> minecraft.gui.setScreen(new MyCustomScreen()));
        sidebar.addChild(demo);

        // ---- 底栏：语言 + 退出 ----
        int bottomY = sh - 25;

        var language = new CustomButton(5, bottomY, 20, 20,
                Component.literal("文"), 0xFF555555)
                .setBorderRadius(4)
                .setTooltip(Component.translatable("narrator.button.language"))
                .onClick(() -> minecraft.gui.setScreen(
                        new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager())));
        sidebar.addChild(language);

        var quit = new CustomButton(panelW - 50, bottomY, 45, 20,
                Component.literal("退出游戏"), 0xFF555555)
                .setBorderRadius(4)
                .setTooltip(Component.translatable("menu.quit"))
                .onClick(minecraft::stop);
        sidebar.addChild(quit);

        // ====== 底部左侧信息 ======
        addWidget(new SimpleLabel(5, sh - 20,
                Component.literal("不隶属于 MOJANG"), 0xFFAA00));

        // ---- Credits 按钮 ----
        var credits = new CustomButton(5, sh - 14, 152, 12,
                Component.literal("MohistMC 出品"), 0x00000000)
                .setTextColor(0xFF00FF00)
                .setHoverColor(0x3300FF00)
                .setBorderRadius(0)
                .onClick(() -> ConfirmLinkScreen.confirmLinkNow(this, "https://www.mohistmc.cn/"));
        addWidget(credits);

        // ====== 根组件加入屏幕 ======
        addWidget(sidebar);
    }
}
