package com.mohistmc.mod.module.create.client.infrastructure.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mohistmc.mod.module.create.client.catnip.gui.ScreenOpener;
import com.mohistmc.mod.module.create.client.content.equipment.goggles.GoggleConfigScreen;
import com.mohistmc.mod.module.create.client.infrastructure.config.AllConfigs;
import com.mohistmc.mod.module.create.client.infrastructure.config.CClient;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;

public class OverlayConfigCommand {
    public static LiteralArgumentBuilder<ClientSuggestionProvider> register() {
        return ClientCommand.literal(
            "overlay", context -> {
                context.getSource().minecraft.schedule(() -> ScreenOpener.open(new GoggleConfigScreen()));
                return Command.SINGLE_SUCCESS;
            }
        ).then(ClientCommand.literal(
            "reset", context -> {
                CClient client = AllConfigs.client();
                client.overlayOffsetX.set(0);
                client.overlayOffsetY.set(0);
                context.getSource().minecraft.gui.chatListener()
                    .handleSystemMessage(
                        Component.literal("Create Goggle Overlay has been reset to default position"),
                        false
                    );
                return Command.SINGLE_SUCCESS;
            }
        ));
    }
}
