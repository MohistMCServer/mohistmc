package com.mohistmc.mod.client.gui;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.IconButton;
import com.mohistmc.mod.api.gui.LabelBadge;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.module.mail.common.network.payload.OpenMailboxRequestPayload;
import com.mohistmc.mod.module.curios.common.network.client.CPacketOpenCurios;
import com.mohistmc.mod.module.shop.common.network.payload.BalanceRequestPayload;
import com.mohistmc.mod.utils.ProcessWorkingSetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.modlist.ModListScreen;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义 ESC 界面 — 左侧工具栏 + 玩家信息卡 + 图标网格
 *
 * <p>占据屏幕左侧约 1/4，右侧透出游戏画面。
 *
 * <p>纹理文件（用户需自行添加至 {@code assets/mohistmc/textures/ui/}）：</p>
 * <ul>
 *   <li>{@code back.png} — 返回游戏</li>
 *   <li>{@code achievement.png} — 进度</li>
 *   <li>{@code mods.png} — 模组</li>
 *   <li>{@code settings.png} — 原版设置</li>
 *   <li>{@code exit.png} — 离开游戏</li>
 *   <li>{@code bg1.png} — 内容区背景（纵向平铺）</li>
 *   <li>{@code bg.png} — 玩家信息卡背景</li>
 *   <li>{@code 0.png} ~ {@code 6.png} — 图标网格按钮</li>
 *   <li>{@code name_logo.png} — 服务器名装饰</li>
 *   <li>{@code ping_logo.png} — 延迟图标</li>
 * </ul>
 */
public class EscGui extends EnhancedScreen {

    private static Identifier tex(String name) {
        return Identifier.fromNamespaceAndPath(MohistMC.MODID, "textures/ui/" + name);
    }

    /** 左侧工具栏宽度 */
    private static final int TOOL_W = 36;
    /** 工具栏左右固定边距 */
    private static final int TOOL_MARGIN = 4;
    /** 工具栏图标大小（响应式：宽度减去边距） */
    private static final int BTN_SZ = TOOL_W - TOOL_MARGIN * 2;
    /** 网格列数 */
    private static final int COLS = 4;
    /** 网格间距 */
    private static final int GAP = 4;

    /** 金币行余额徽章（收到服务端余额同步后更新数值，宽度自动增长） */
    private LabelBadge balanceValue;

    public EscGui() {
        super(Component.translatable("narrator.screen.title"), null);
    }

    /** 暂停游戏，让世界在背景继续渲染 */
    @Override
    public boolean isPauseScreen() {
        return true;
    }

    /** 左侧面板，画布靠左对齐 */
    @Override
    protected boolean isLeftAligned() {
        return true;
    }

    /** 不绘制原版暂停背景（模糊/暗色叠层），让游戏画面透出 */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // 什么都不做 — 保留游戏画面
    }

    @Override
    protected void buildWidgets() {
        int sw = getImageWidth();
        int sh = getImageHeight();

        // GUI 宽度：屏幕的 28%，响应式范围 180~450px
        int guiW = Math.clamp(sw * 28L / 100, 180, 450);
        int guiH = sh;

        // ================================================================
        // 左侧工具栏（0 ~ TOOL_W）
        // ================================================================
        var toolPanel = new Panel(0, 0, TOOL_W, guiH, Identifier.fromNamespaceAndPath("mohistmc", "textures/ui/bg0.png"))
                .setEditorId("toolPanel");
        int btnX = (TOOL_W - BTN_SZ) / 2;
        int topGap = 12;
        int bottomGap = 12;

        // 返回（顶部）
        addToolButton(toolPanel, btnX, topGap, "back", "tool_back",
                Component.translatable("menu.returnToGame"),
                () -> {
                    minecraft.gui.setScreen(null);
                    minecraft.mouseHandler.grabMouse();
                });

        // 中间按钮等距排列
        Runnable[] toolActions = {
                () -> minecraft.gui.setScreen(
                        new AdvancementsScreen(minecraft.player.connection.getAdvancements(), this)),
                () -> minecraft.gui.setScreen(ModListScreen.create(this)),
                () -> minecraft.gui.setScreen(new OptionsScreen(this, minecraft.options, false))
        };
        String[] toolNames = {"achievement", "mods", "settings"};
        String[] toolIds = {"tool_achievement", "tool_mods", "tool_settings"};
        Component[] toolTips = {
                Component.literal("进度"),
                Component.literal("模组"),
                Component.literal("原版设置")
        };

        // 中间按钮靠底部排列（退出按钮上方）
        int toolBtnGap = 8;
        int exitY = guiH - bottomGap - BTN_SZ;
        int toolGroupH = toolNames.length * BTN_SZ + (toolNames.length - 1) * toolBtnGap;
        int startY = exitY - toolBtnGap - toolGroupH;
        for (int i = 0; i < toolNames.length; i++) {
            addToolButton(toolPanel, btnX, startY + i * (BTN_SZ + toolBtnGap),
                    toolNames[i], toolIds[i], toolTips[i], toolActions[i]);
        }

        // 退出（底部）
        addToolButton(toolPanel, btnX, guiH - bottomGap - BTN_SZ, "exit", "tool_exit",
                Component.literal("离开当前游戏"),
                () -> minecraft.disconnectFromWorld(Component.translatable("menu.savingLevel")));

        addWidget(toolPanel);

        // ================================================================
        // 内容区（TOOL_W ~ guiW）
        // ================================================================
        int cx = TOOL_W;
        int cw = guiW - TOOL_W;
        // 信息卡高度：30% 屏高，同时至少留 110px 给网格，且不低于 95px
        int cardH = Math.max(Math.min(guiH * 3 / 10, guiH - 110), 95);

        // ---- 玩家信息卡（深蓝黑底） ----
        // 布局：所有内容平分高度，保持统一间距，文字自动缩放（LabelBadge 内部按卡高计算）
        var card = new Panel(cx, 0, cw, cardH, Identifier.fromNamespaceAndPath("mohistmc", "textures/ui/bg.png"))
                .setEditorId("infoCard");
        int pad = 6;
        int rowH = 16;   // 每行高度（图标+文字）
        int fh = Minecraft.getInstance().font.lineHeight;

        // ── 玩家名徽章（先加入以触发自动缩放，再取实际尺寸用于布局） ──
        String name = minecraft.player != null ? minecraft.player.getScoreboardName() : "Player";
        var nameBadge = new LabelBadge(0, 0, Component.literal(name))
                .setAutoScale(95)
                .setBgColor(0)
                .setBorderWidth(0)
                .setPaddingY(0)
                .setEditorId("nameBadge");
        card.addChild(nameBadge);
        int nameH = nameBadge.height;

        // 动态间距
        int avatarSize = Math.min(Math.min(cardH / 4, 64), cw / 2);
        int statRowsH = fh + 8 + 6 + rowH; // Lv. 行 + 8px gap + 经验条6px + ping 行
        int fixedH = avatarSize + 2 + nameH + statRowsH;
        int gap = Math.max(2, Math.min(10, (cardH - pad * 2 - fixedH) / 2));
        int y = pad;

        // ── 头像（居中） ──
        int avatarX = (cw - avatarSize) / 2;
        card.addChild(new PlayerHeadWidget(avatarX, y, avatarSize)
                .setEditorId("playerHead"));

        // ── 玩家名徽章（居中，宽度随文字自动向左右增长） ──
        int nameX = (cw - nameBadge.width) / 2;
        nameBadge.setRelativeX(nameX);
        nameBadge.setRelativeY(y + avatarSize + 2);
        y += avatarSize + 2 + nameH + 2; // 名字到等级只留 2px，避免间距过大

        // ── 等级 + 经验条 ──
        int level = minecraft.player != null ? minecraft.player.experienceLevel : 0;
        String lvText = "Lv." + level;
        // 等级徽章（无背景，宽度随文字自动增长，居中）
        var levelBadge = new LabelBadge(0, y, Component.literal(lvText))
                .setAutoScale(95)
                .setBgColor(0)
                .setBorderWidth(0)
                .setPaddingY(0)
                .setEditorId("levelLabel");
        card.addChild(levelBadge);
        int lvX = (cw - levelBadge.width) / 2;
        levelBadge.setRelativeX(lvX);
        int levelH = levelBadge.height;

        float xpPct = minecraft.player != null ? minecraft.player.experienceProgress : 0;
        card.addChild(new ExpBarWidget(8, y + levelH + 8, cw - 16, 6, xpPct)
                .setEditorId("expBar"));
        y += levelH + 8 + 6 + gap;

        // ── 图标+数值行（延迟、余额等） ──
        // 每行：[icon] + 数值 组成的独立徽章，宽度随数值长度自动向右增长
        int statX = 8;
        statX = addStatRow(card, statX, y, "ping_logo.png", getCurrentPing() + " ms");
        // 余额行：初始 "..."，收到服务端 BalanceSyncPayload 后刷新真实余额
        balanceValue = new LabelBadge(statX, y, Component.literal("..."))
                .setAutoScale(95)
                .setIcon(tex("jinbi.png"))
                .setEditorId("balanceBadge");
        card.addChild(balanceValue);

        addWidget(card);

        // 请求服务端同步余额（单机/联机均走网络路径）
        if (minecraft.getConnection() != null) {
            ClientPacketDistributor.sendToServer(new BalanceRequestPayload());
        }

        // ---- 图标网格区域背景（深灰，区别于信息卡的深蓝黑） ----
        addWidget(new Panel(cx, cardH, cw, guiH - cardH, Identifier.fromNamespaceAndPath("mohistmc", "textures/ui/bg1.png"))
                .setEditorId("gridBg"));

        // ---- 图标网格 ----
        record IconDef(String tex, Component tip, Runnable action) {}
        IconDef[] icons = {
                new IconDef("0", Component.literal("邮件"), () ->
                        ClientPacketDistributor.sendToServer(OpenMailboxRequestPayload.INSTANCE)),
                new IconDef("1", Component.literal("活动"), () -> {
                    System.out.println("debug");
                }),
                new IconDef("2", Component.literal("好友"), () -> {
                    ProcessWorkingSetUtils.setProcessWorkingSetSize(50, 100);
                }),
                new IconDef("3", Component.literal("背包"), () ->
                        ClientPacketDistributor.sendToServer(new CPacketOpenCurios(ItemStack.EMPTY))),
                new IconDef("4", Component.literal("百科"), () -> {}),
                new IconDef("5", Component.literal("公告"), () -> {}),
        };

        int gridMargin = 8;
        int gridY = cardH + 12;
        int gridInnerW = cw - gridMargin * 2;
        int gridSz = (gridInnerW - (COLS - 1) * GAP) / COLS;

        // 直接作为根级 widget 加入（跳过 Panel 容器嵌套，确保点击分发可靠）
        for (int i = 0; i < icons.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int ix = cx + gridMargin + col * (gridSz + GAP);
            int iy = gridY + row * (gridSz + GAP);

            addWidget(new IconButton(ix, iy, gridSz)
                    .setTexture(tex("background.png"))
                    .setHoverBgColor(0x00000000)
                    .setEditorId("icon_bg_" + i));
            addWidget(new IconButton(ix, iy, gridSz)
                    .setTexture(tex(icons[i].tex + ".png"))
                    .setHoverBgColor(0x44FFFFFF)
                    .setTooltip(icons[i].tip)
                    .setEditorId("icon_" + i)
                    .onClick(icons[i].action));
        }
    }

    // ======== 辅助 ========

    /** 添加一行 [图标 + 数值] 独立徽章，宽度随数值自动向右增长；返回下一个统计项的 X 起点 */
    private int addStatRow(Panel parent, int x, int y, String iconTex, String value) {
        var badge = new LabelBadge(x, y, Component.literal(value))
                .setAutoScale(95)
                .setIcon(tex(iconTex))
                .setEditorId("stat_" + iconTex);
        parent.addChild(badge);
        return x + badge.width + 8; // 组间距 8px
    }

    private void addToolButton(Panel parent, int x, int y, String texName, String editorId,
                               Component tip, Runnable action) {
        parent.addChild(new IconButton(x, y, BTN_SZ)
                .setEditorId(editorId)
                .setTexture(tex(texName + ".png"))
                .setHoverBgColor(0x55FFFFFF)
                .setTooltip(tip)
                .onClick(action));
    }

    /** 服务端余额同步：刷新余额徽章数值（宽度随数字长度自动增长） */
    public void updateBalance(int balance) {
        if (balanceValue != null) {
            balanceValue.setText(Component.literal(String.valueOf(balance)));
        }
    }

    private static int getCurrentPing() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null && mc.player != null) {
            var info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (info != null) return info.getLatency();
        }
        return 0;
    }

    // ================================================================
    // 玩家头像组件 — 使用原版 PlayerFaceExtractor 渲染
    // ================================================================
    private static class PlayerHeadWidget extends com.mohistmc.mod.api.gui.PositionedWidget {
        PlayerHeadWidget(int x, int y, int size) {
            super(x, y, size, size);
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            int ax = getAbsoluteX();
            int ay = getAbsoluteY();
            int s = Math.min(width, height);
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                net.minecraft.client.gui.components.PlayerFaceExtractor.extractRenderState(
                        graphics, mc.player.getSkin(), ax, ay, s, -1);
            }
        }
    }

    // ================================================================
    // 经验条组件
    // ================================================================
    private static class ExpBarWidget extends com.mohistmc.mod.api.gui.PositionedWidget {
        private final float progress;

        ExpBarWidget(int x, int y, int w, int h, float progress) {
            super(x, y, w, h);
            this.progress = progress;
        }

        @Override
        public void render(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            int ax = getAbsoluteX();
            int ay = getAbsoluteY();
            graphics.fill(ax, ay, ax + width, ay + height, 0xFF555555);
            int fillW = (int) (width * progress);
            if (fillW > 0) {
                graphics.fill(ax, ay, ax + fillW, ay + height, 0xFF00AA00);
            }
        }
    }
}
