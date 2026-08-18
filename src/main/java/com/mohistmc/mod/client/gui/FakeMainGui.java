package com.mohistmc.mod.client.gui;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.api.gui.Avatar;
import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.api.gui.SimpleLabel;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FakeMainGui extends EnhancedScreen {

    private static final Identifier BG = Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/gui/bg1.png");

    private static final int SIDEBAR_W = 220;
    private static final int BTN_H = 40;
    private static final int BTN_GAP = 8;

    public FakeMainGui() {
        super(Component.translatable("narrator.screen.title"), BG);
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();  // 1280
        int sh = getImageHeight(); // 窄屏时自动扩展填满屏幕

        // ====== 右侧边栏 ======
        var sidebar = new Panel(0, 0, SIDEBAR_W, sh, 0x77000000)
                .setRightAnchored(true).setEditorId("sidebar");

        // Logo
        int logoSize = 128;
        var logo = new Avatar((SIDEBAR_W - logoSize) / 2, 32, logoSize);
        logo.setEditorId("logo");
        logo.setTexture(Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/item/logo.png"));
        sidebar.addChild(logo);

        // 标题
        var title = new SimpleLabel(0, 32 + logoSize + 12,
                Component.literal("MohistMC"), 0xFFFFFF);
        title.setEditorId("title");
        title.setLabelWidth(SIDEBAR_W).setAlign(SimpleLabel.Align.CENTER);
        title.setFontSize(28);
        sidebar.addChild(title);

        var subtitle = new SimpleLabel(0, 32 + logoSize + 48,
                Component.literal("Forge Hybrid Server"), 0xB0B0B0);
        subtitle.setEditorId("subtitle");
        subtitle.setLabelWidth(SIDEBAR_W).setAlign(SimpleLabel.Align.CENTER);
        subtitle.setFontSize(14);
        sidebar.addChild(subtitle);

        // 按钮（垂直居中偏上，基于 sh）
        int btnStartY = sh / 2 - 80;

        var singleplayer = new CustomButton(20, btnStartY, SIDEBAR_W - 40, BTN_H,
                Component.translatable("menu.singleplayer"), 0xFF336633)
                .setEditorId("btn_singleplayer")
                .setTextColor(0xFFFFFFFF).setBorderRadius(8)
                .setTooltip(Component.translatable("menu.singleplayer"))
                .onClick(() -> minecraft.gui.setScreen(new SelectWorldScreen(this)));
        sidebar.addChild(singleplayer);

        var multiplayer = new CustomButton(20, btnStartY + BTN_H + BTN_GAP, SIDEBAR_W - 40, BTN_H,
                Component.translatable("menu.multiplayer"), 0xFF336666)
                .setEditorId("btn_multiplayer")
                .setTextColor(0xFFFFFFFF).setBorderRadius(8)
                .setTooltip(Component.translatable("menu.multiplayer"))
                .onClick(() -> minecraft.gui.setScreen(new JoinMultiplayerScreen(this)));
        sidebar.addChild(multiplayer);

        var options = new CustomButton(20, btnStartY + (BTN_H + BTN_GAP) * 2, SIDEBAR_W - 40, BTN_H,
                Component.translatable("menu.options"), 0xFF333366)
                .setEditorId("btn_options")
                .setTextColor(0xFFFFFFFF).setBorderRadius(8)
                .setTooltip(Component.translatable("menu.options"))
                .onClick(() -> minecraft.gui.setScreen(new OptionsScreen(this, minecraft.options, false)));
        sidebar.addChild(options);

        var demo = new CustomButton(20, btnStartY + (BTN_H + BTN_GAP) * 3, SIDEBAR_W - 40, BTN_H,
                Component.literal("🧪 演示界面"), 0xFF333333)
                .setEditorId("btn_demo")
                .setTextColor(0xFFFFFFFF).setBorderRadius(8)
                .setTooltip(Component.literal("打开组件演示界面"))
                .onClick(() -> minecraft.gui.setScreen(new MyCustomScreen()));
        sidebar.addChild(demo);

        // 底部按钮（基于 sh）
        int bottomY = sh - BTN_H - 24;

        var language = new CustomButton(20, bottomY, BTN_H, BTN_H,
                Component.literal("文"), 0xFF555555)
                .setEditorId("btn_language")
                .setBorderRadius(8)
                .setTooltip(Component.translatable("narrator.button.language"))
                .onClick(() -> minecraft.gui.setScreen(
                        new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager())));
        sidebar.addChild(language);

        var quit = new CustomButton(20, bottomY, BTN_H, BTN_H,
                Component.translatable("menu.quit"), 0xFF663333)
                .setEditorId("btn_quit")
                .setTextColor(0xFFFFFFFF).setBorderRadius(8)
                .setTooltip(Component.translatable("menu.quit"))
                .setRightAnchored(true) // 右锚定：文字过长时按钮向左扩展，不超出边栏
                .onClick(minecraft::stop);
        sidebar.addChild(quit);

        // 底部版权
        var disclaimer = new SimpleLabel(138, 4,
                Component.literal("不隶属于 MOJANG"), 0xFFAAAAAA);
        disclaimer.setEditorId("disclaimer");
        disclaimer.setBottomAnchored(true);
        disclaimer.setFontSize(14);
        addWidget(disclaimer);

        var credits = new CustomButton(12, 3, 123, 29,
                Component.literal("MohistMC 出品"), 0x00000000)
                .setEditorId("credits")
                .setTextColor(0xFF00CCFF).setHoverColor(0x3300CCFF).setBorderRadius(0)
                .setBottomAnchored(true)
                .onClick(() -> ConfirmLinkScreen.confirmLinkNow(this, "https://www.mohistmc.cn/"));
        addWidget(credits);

        addWidget(sidebar);
    }
}