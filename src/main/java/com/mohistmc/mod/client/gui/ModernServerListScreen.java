package com.mohistmc.mod.client.gui;

import com.mohistmc.mod.api.gui.CustomButton;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.ScrollList;
import com.mohistmc.mod.api.gui.ServerCardItem;
import com.mohistmc.mod.api.gui.SimpleLabel;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 现代多人游戏服务器列表 — 卡片式布局，左侧服务器列表 + 底部操作栏
 */
@OnlyIn(Dist.CLIENT)
public class ModernServerListScreen extends EnhancedScreen {

    private final ServerStatusPinger pinger = new ServerStatusPinger();
    private ServerList servers;
    private ScrollList serverList;
    private ServerCardItem selectedCard;
    private ServerData editingServer;

    public ModernServerListScreen() {
        super(Component.translatable("multiplayer.title"), 0xCC000000);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    protected void buildWidgets() {
        int sw = width;
        int sh = height;

        servers = new ServerList(minecraft);
        servers.load();

        // 标题
        addWidget(new SimpleLabel(sw / 2 - minecraft.font.width(Component.translatable("multiplayer.title")) / 2, 12,
                Component.translatable("multiplayer.title"), 0xFFFFFFFF));

        // 服务器列表（透明背景，卡片风格）
        int listTop = 30;
        int listBottom = sh - 52;
        serverList = new ScrollList(40, listTop, sw - 80, listBottom - listTop, 0x00000000);
        serverList.setScrollbarColor(0x55FFFFFF);
        refreshServerCards();
        addWidget(serverList);

        // 底部按钮行
        int btnY = sh - 38;
        int btnW = 90;
        int btnGap = 6;
        int btnStartX = (sw - (btnW * 4 + btnGap * 3)) / 2;

        addWidget(new CustomButton(btnStartX, btnY, btnW, 20,
                Component.translatable("selectServer.select"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4).onClick(this::joinSelected));
        addWidget(new CustomButton(btnStartX + btnW + btnGap, btnY, btnW, 20,
                Component.translatable("selectServer.direct"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4).onClick(this::directConnect));
        addWidget(new CustomButton(btnStartX + (btnW + btnGap) * 2, btnY, btnW, 20,
                Component.translatable("selectServer.add"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4).onClick(this::addServer));
        addWidget(new CustomButton(btnStartX + (btnW + btnGap) * 3, btnY, btnW, 20,
                Component.translatable("selectServer.refresh"), 0xFF555555)
                .setTextColor(0xFFFFFFFF).setBorderRadius(4).onClick(this::refreshServerList));

        // 第二行：编辑 / 删除 / 返回（居中）
        int btnY2 = sh - 16;
        int btnW2 = 60;
        int btnGap2 = 2;
        int btnRowW = btnW2 * 3 + btnGap2 * 2;
        int btnStart2 = sw / 2 - btnRowW / 2;
        addWidget(new CustomButton(btnStart2, btnY2, btnW2, 14,
                Component.translatable("selectServer.edit"), 0xFF444444)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3).onClick(this::editServer));
        addWidget(new CustomButton(btnStart2 + btnW2 + btnGap2, btnY2, btnW2, 14,
                Component.translatable("selectServer.delete"), 0xFF444444)
                .setTextColor(0xFFFFFFFF).setBorderRadius(3).onClick(this::deleteServer));
        addWidget(new CustomButton(btnStart2 + (btnW2 + btnGap2) * 2, btnY2, btnW2, 14,
                CommonComponents.GUI_BACK, 0xFF444444)
                .setTextColor(0xFFAAAAAA).setBorderRadius(3)
                .onClick(() -> minecraft.gui.setScreen(null)));

        // 开始 ping 所有服务器
        var group = EventLoopGroupHolder.remote(minecraft.options.useNativeTransport());
        for (var item : serverList.getItems()) {
            if (item instanceof ServerCardItem card) {
                ServerData sd = card.server;
                try {
                    pinger.pingServer(sd,
                            () -> minecraft.execute(this::pingDataChanged),
                            () -> {
                                boolean ok = sd.protocol == SharedConstants.getCurrentVersion().protocolVersion();
                                sd.setState(ok ? ServerData.State.SUCCESSFUL : ServerData.State.INCOMPATIBLE);
                                minecraft.execute(this::pingDataChanged);
                            },
                            group);
                } catch (Exception ignored) {
                    sd.setState(ServerData.State.UNREACHABLE);
                }
            }
        }
    }

    private void pingDataChanged() {}

    private void refreshServerCards() {
        serverList.clearItems();
        for (int i = 0; i < servers.size(); i++) {
            serverList.addItem(new ServerCardItem(servers.get(i), pinger));
        }
    }

    private void joinSelected() {
        if (selectedCard != null) selectedCard.joinServer();
    }

    private void directConnect() {
        editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
        minecraft.gui.setScreen(new DirectJoinServerScreen(this, this::directJoinCallback, editingServer));
    }

    private void addServer() {
        editingServer = new ServerData("", "", ServerData.Type.OTHER);
        minecraft.gui.setScreen(new ManageServerScreen(this,
                Component.translatable("manageServer.add.title"), this::addServerCallback, editingServer));
    }

    private void editServer() {
        if (selectedCard == null) return;
        var current = selectedCard.server;
        editingServer = new ServerData(current.name, current.ip, ServerData.Type.OTHER);
        editingServer.copyFrom(current);
        minecraft.gui.setScreen(new ManageServerScreen(this,
                Component.translatable("manageServer.edit.title"), this::editServerCallback, editingServer));
    }

    private void deleteServer() {
        if (selectedCard == null) return;
        String name = selectedCard.server.name;
        if (name != null) {
            minecraft.gui.setScreen(new ConfirmScreen(this::deleteCallback,
                    Component.translatable("selectServer.deleteQuestion"),
                    Component.translatable("selectServer.deleteWarning", name),
                    Component.translatable("selectServer.deleteButton"),
                    CommonComponents.GUI_CANCEL));
        }
    }

    private void refreshServerList() {
        minecraft.gui.setScreen(new ModernServerListScreen());
    }

    private void deleteCallback(boolean result) {
        if (result && selectedCard != null) {
            servers.remove(selectedCard.server);
            servers.save();
            selectedCard = null;
            refreshServerCards();
        }
        minecraft.gui.setScreen(this);
    }

    private void editServerCallback(boolean result) {
        if (result && selectedCard != null) {
            var current = selectedCard.server;
            current.name = editingServer.name;
            current.ip = editingServer.ip;
            current.copyFrom(editingServer);
            servers.save();
            refreshServerCards();
        }
        minecraft.gui.setScreen(this);
    }

    private void addServerCallback(boolean result) {
        if (result) {
            var existing = servers.unhide(editingServer.ip);
            if (existing != null) existing.copyNameIconFrom(editingServer);
            else servers.add(editingServer, false);
            servers.save();
            selectedCard = null;
            refreshServerCards();
        }
        minecraft.gui.setScreen(this);
    }

    private void directJoinCallback(boolean result) {
        if (result) {
            var existing = servers.get(editingServer.ip);
            if (existing == null) {
                servers.add(editingServer, true);
                servers.save();
                joinServer(editingServer);
            } else {
                joinServer(existing);
            }
        } else {
            minecraft.gui.setScreen(this);
        }
    }

    private void joinServer(ServerData data) {
        ConnectScreen.startConnecting(this, minecraft,
                ServerAddress.parseString(data.ip), data, false, null);
    }
}
