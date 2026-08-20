package com.mohistmc.mod.api.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 服务器卡片项 — 用于 ScrollList 中展示单个服务器信息
 */
public class ServerCardItem extends ScrollListItem {

    private static final Identifier FALLBACK_ICON = Identifier.withDefaultNamespace("textures/misc/unknown_server.png");
    private static final int ICON_SIZE = 32;

    public final ServerData server;
    private final ServerStatusPinger pinger;
    private final Minecraft mc;
    @Nullable
    private Identifier iconTexture;
    @Nullable
    private List<Component> playerList;

    public ServerCardItem(ServerData server, ServerStatusPinger pinger) {
        this.server = server;
        this.pinger = pinger;
        this.mc = Minecraft.getInstance();
        this.height = 48;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, int w, boolean hovered, int alpha) {
        int ic = x + 4;
        int icY = y + (height - ICON_SIZE) / 2;

        // 服务器图标背景
        g.fill(ic, icY, ic + ICON_SIZE, icY + ICON_SIZE, 0xFF333333);

        // 图标（尝试加载 favicon，否则用 fallback）
        Identifier tex = iconTexture;
        if (tex == null) {
            tex = FALLBACK_ICON;
        }
        g.blit(RenderPipelines.GUI_TEXTURED, tex, ic, icY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        // 文字区域
        int tx = ic + ICON_SIZE + 6;
        int tw = w - (tx - x) - 4;
        var font = mc.font;

        // 服务器名（白色，截断）
        String name = server.name != null ? server.name : "?";
        Component nameComp = Component.literal(font.plainSubstrByWidth(name, tw));
        g.text(font, nameComp, tx, y + 4, 0xFFFFFFFF);

        // 状态 / MOTD（灰色，最多两行）
        Component motd = server.motd;
        if (motd != null && !motd.getString().isEmpty()) {
            var lines = font.split(motd, tw);
            for (int i = 0; i < Math.min(lines.size(), 2); i++) {
                g.text(font, lines.get(i), tx, y + 16 + 9 * i, 0xFFAAAAAA);
            }
        }

        // 底部行：服务器状态信息
        Component status = getStatusText();
        if (status != null) {
            g.text(font, status, tx, y + height - 10, 0xFF888888);
        }

        // 右侧：ping 指示器
        int pingX = x + w - 14;
        int pingY = y + (height - 8) / 2;
        drawPingIndicator(g, pingX, pingY);

        // 悬停边框
        if (hovered) {
            g.fill(x, y, x + w, y + 1, 0x44FFFFFF);
            g.fill(x, y + height - 1, x + w, y + height, 0x44FFFFFF);
        }
    }

    private Component getStatusText() {
        return switch (server.state()) {
            case INITIAL, PINGING -> Component.translatable("multiplayer.status.pinging");
            case INCOMPATIBLE -> Component.translatable("multiplayer.status.incompatible");
            case UNREACHABLE -> Component.translatable("multiplayer.status.cannot_connect");
            case SUCCESSFUL -> {
                StringBuilder sb = new StringBuilder();
                if (server.players != null) {
                    sb.append("§7").append(server.players.online()).append("§8/§7").append(server.players.max()).append(" §8玩家  ");
                }
                sb.append("§7延迟 §f").append(server.ping).append("ms");
                yield Component.literal(sb.toString());
            }
        };
    }

    /** 绘制 ping 格数指示器（1~5 格） */
    private void drawPingIndicator(GuiGraphicsExtractor g, int x, int y) {
        int bars;
        if (server.state() == ServerData.State.PINGING) {
            bars = 1;
        } else if (server.state() != ServerData.State.SUCCESSFUL) {
            bars = 0;
        } else if (server.ping < 150L) {
            bars = 5;
        } else if (server.ping < 300L) {
            bars = 4;
        } else if (server.ping < 600L) {
            bars = 3;
        } else if (server.ping < 1000L) {
            bars = 2;
        } else {
            bars = 1;
        }

        for (int i = 0; i < 5; i++) {
            int barH = 2 + i * 2;
            int barColor;
            if (i < bars) {
                barColor = bars <= 1 ? 0xFFFF4444 : bars <= 3 ? 0xFFFFAA00 : 0xFF00EE00;
            } else {
                barColor = 0xFF444444;
            }
            g.fill(x + i * 3, y + (8 - barH), x + i * 3 + 2, y + (8 - barH) + barH, barColor);
        }
    }

    /** 点击处理：右侧加入 / 双击加入 / 重排 */
    @Override
    public boolean handleClick(int rx, int ry, int w) {
        int icRight = 4 + ICON_SIZE;
        // 右侧加入按钮区域（图标右侧区域）
        if (rx >= icRight && ry >= 0 && rx < w - 16 && ry < height) {
            joinServer();
            return true;
        }
        return false;
    }

    /** 加入服务器 */
    public void joinServer() {
        var address = ServerAddress.parseString(server.ip);
        var screen = Minecraft.getInstance().gui.screen();
        if (screen != null) {
            ConnectScreen.startConnecting(screen, mc, address, server, false, null);
        }
    }

    public void setIconTexture(@Nullable Identifier tex) {
        this.iconTexture = tex;
    }
}
