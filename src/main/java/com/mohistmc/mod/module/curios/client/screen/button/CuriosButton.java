/*
 * Copyright (c) 2018-2024 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.mohistmc.mod.module.curios.client.screen.button;

import com.mohistmc.mod.module.curios.api.CuriosResources;
import com.mohistmc.mod.module.curios.client.screen.CuriosScreen;
import com.mohistmc.mod.module.curios.common.network.client.CPacketOpenCurios;
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class CuriosButton extends ImageButton {

    public static final WidgetSprites BIG =
            new WidgetSprites(CuriosResources.resource("button"),
                    CuriosResources.resource("button_highlighted"));
    public static final WidgetSprites SMALL =
            new WidgetSprites(CuriosResources.resource("button_small"),
                    CuriosResources.resource("button_small_highlighted"));
    private final AbstractContainerScreen<?> parentGui;

    public CuriosButton(AbstractContainerScreen<?> parentGui, int xIn, int yIn, int widthIn,
                        int heightIn,
                        WidgetSprites sprites) {
        super(xIn, yIn, widthIn, heightIn, sprites,
                (button) -> {
                    Minecraft mc = Minecraft.getInstance();

                    if (mc.player != null) {
                        ItemStack stack = mc.player.containerMenu.getCarried();
                        mc.player.containerMenu.setCarried(ItemStack.EMPTY);
                        ClientPacketDistributor.sendToServer(new CPacketOpenCurios(stack));
                    }
                });
        this.parentGui = parentGui;
    }

    @Override
    public void extractContents(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY,
                                float partialTicks) {
        Pair<Integer, Integer> offsets =
                CuriosScreen.getButtonOffset(parentGui instanceof CreativeModeInventoryScreen);
        this.setX(parentGui.getLeftPos() + offsets.getFirst() + 2);
        int yOffset = parentGui instanceof CreativeModeInventoryScreen ? 70 : 85;
        this.setY(parentGui.getTopPos() + offsets.getSecond() + yOffset);

        if (parentGui instanceof CreativeModeInventoryScreen gui) {
            boolean isInventoryTab = gui.isInventoryOpen();
            this.active = isInventoryTab;

            if (!isInventoryTab) {
                return;
            }
        }
        super.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
