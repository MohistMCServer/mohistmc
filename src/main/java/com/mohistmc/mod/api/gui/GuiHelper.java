package com.mohistmc.mod.api.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class GuiHelper {
    public static Button createButton(int relX, int relY, int width, int height, Component text, Button.OnPress onPress) {
        // 这里的位置是相对于 Screen 的，真正的 setPos 在 addWidget 时调整
        return Button.builder(text, onPress)
                .pos(relX, relY) // 临时位置
                .size(width, height)
                .build();
    }
}