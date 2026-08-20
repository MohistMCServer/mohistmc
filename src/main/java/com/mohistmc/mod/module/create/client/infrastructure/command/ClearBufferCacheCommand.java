package com.mohistmc.mod.module.create.client.infrastructure.command;

import com.mohistmc.mod.module.create.client.Create;
import com.mohistmc.mod.module.ponder.Ponder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ClearBufferCacheCommand {
    public static LiteralArgumentBuilder<ClientSuggestionProvider> register() {
        return ClientCommand.literal(
            "clearRenderBuffers", context -> {
                Ponder.invalidateRenderers();
                Create.invalidateRenderers();
                context.getSource().minecraft.gui.chatListener()
                    .handleSystemMessage(Component.literal("Cleared rendering buffers."), false);
                return Command.SINGLE_SUCCESS;
            }
        );
    }
}
