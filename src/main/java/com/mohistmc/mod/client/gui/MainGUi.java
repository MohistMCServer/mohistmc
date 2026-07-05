package com.mohistmc.mod.client.gui;

import com.mohistmc.mod.client.imgui.ImGuiRenderable;
import imgui.ImGui;
import imgui.ImGuiIO;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * @author Mgazul
 * @date 2026/7/6 02:54
 */
public class MainGUi extends Screen implements ImGuiRenderable {

    protected MainGUi(Component title) {
        super(Component.literal("Example Screen"));
    }

    @Override
    public void render(ImGuiIO io) {
        if (ImGui.begin("fabric-gui-imgui")) {
            ImGui.text("Enaium!");
            ImGui.end();
        }
    }
}
