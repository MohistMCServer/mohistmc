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

package com.mohistmc.mod.module.curios.common.network.client;

import com.mohistmc.mod.module.curios.api.CuriosApi;
import com.mohistmc.mod.module.curios.api.SlotContext;
import com.mohistmc.mod.module.curios.api.event.SlotModifiersUpdatedEvent;
import com.mohistmc.mod.module.curios.api.type.ICuriosMenu;
import com.mohistmc.mod.module.curios.api.type.capability.ICurio;
import com.mohistmc.mod.module.curios.api.type.inventory.ICurioStacksHandler;
import com.mohistmc.mod.module.curios.client.screen.CuriosScreen;
import com.mohistmc.mod.module.curios.common.capability.CurioInventory;
import com.mohistmc.mod.module.curios.common.data.CuriosSlotResources;
import com.mohistmc.mod.module.curios.common.inventory.CurioStacksHandler;
import com.mohistmc.mod.module.curios.common.inventory.container.CuriosMenu;
import com.mohistmc.mod.module.curios.common.network.server.SPacketBreak;
import com.mohistmc.mod.module.curios.common.network.server.SPacketGrabbedItem;
import com.mohistmc.mod.module.curios.common.network.server.SPacketPage;
import com.mohistmc.mod.module.curios.common.network.server.SPacketQuickMove;
import com.mohistmc.mod.module.curios.common.network.server.sync.SPacketSyncActiveState;
import com.mohistmc.mod.module.curios.common.network.server.sync.SPacketSyncCurios;
import com.mohistmc.mod.module.curios.common.network.server.sync.SPacketSyncData;
import com.mohistmc.mod.module.curios.common.network.server.sync.SPacketSyncModifiers;
import com.mohistmc.mod.module.curios.common.network.server.sync.SPacketSyncRender;
import com.mohistmc.mod.module.curios.common.network.server.sync.SPacketSyncStack;
import com.mohistmc.mod.module.curios.impl.CuriosRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

public class CuriosClientPackets {

    public static void handle(final SPacketQuickMove data) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer clientPlayer = mc.player;

        if (clientPlayer != null
                && clientPlayer.containerMenu instanceof CuriosMenu container) {
            container.quickMoveStack(clientPlayer, data.moveIndex());
        }
    }

    public static void handle(final SPacketPage data) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer clientPlayer = mc.player;
        Screen screen = mc.gui.screen();

        if (clientPlayer != null) {
            AbstractContainerMenu container = clientPlayer.containerMenu;

            if (container instanceof CuriosMenu && container.containerId == data.windowId()) {
                ((CuriosMenu) container).setPage(data.page());
            }
        }

        if (screen instanceof CuriosScreen) {
            ((CuriosScreen) screen).updateRenderButtons();
        }
    }

    public static void handle(final SPacketBreak data) {
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(data.entityId());

            if (entity instanceof LivingEntity livingEntity) {
                CuriosApi.getCuriosInventory(livingEntity)
                        .flatMap(handler -> handler.getStacksHandler(data.curioId())).ifPresent(stacks -> {
                            ItemStack stack = stacks.getStacks().getStackInSlot(data.slotId());
                            Optional<ICurio> possibleCurio = CuriosApi.getCurio(stack);
                            NonNullList<Boolean> renderStates = stacks.getRenders();
                            possibleCurio.ifPresent(curio -> curio.curioBreak(
                                    new SlotContext(data.curioId(), livingEntity, data.slotId(), false,
                                            renderStates.size() > data.slotId() && renderStates.get(
                                                    data.slotId()))));

                            if (possibleCurio.isEmpty()) {
                                ICurio.playBreakAnimation(stack, livingEntity);
                            }
                        });
            }
        }
    }

    public static void handle(final SPacketSyncRender data) {
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(data.entityId());

            if (entity instanceof LivingEntity) {
                CuriosApi.getCuriosInventory((LivingEntity) entity)
                        .flatMap(handler -> handler.getStacksHandler(data.curioId()))
                        .ifPresent(stacksHandler -> {
                            int index = data.slotId();
                            NonNullList<Boolean> renderStatuses = stacksHandler.getRenders();

                            if (renderStatuses.size() > index) {
                                renderStatuses.set(index, data.value());
                            }
                        });
            }
        }
    }

    public static void handle(final SPacketSyncModifiers data) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;

        if (world != null) {
            Entity entity = world.getEntity(data.entityId);

            if (entity instanceof LivingEntity livingEntity) {
                CuriosApi.getCuriosInventory(livingEntity)
                        .ifPresent(handler -> {
                            Map<String, ICurioStacksHandler> curios = handler.getCurios();

                            for (Map.Entry<String, CompoundTag> entry : data.updates.entrySet()) {
                                String id = entry.getKey();
                                ICurioStacksHandler stacksHandler = curios.get(id);

                                if (stacksHandler != null) {
                                    stacksHandler.applySyncTag(entry.getValue());
                                }
                            }

                            if (!data.updates.isEmpty()) {
                                NeoForge.EVENT_BUS.post(
                                        new SlotModifiersUpdatedEvent(livingEntity, data.updates.keySet()));
                            }

                            if (entity instanceof LocalPlayer localPlayer) {

                                if (localPlayer.containerMenu instanceof ICuriosMenu curiosMenu) {
                                    curiosMenu.resetSlots();
                                }

                                if (mc.gui.screen() instanceof CuriosScreen screen) {
                                    screen.updateRenderButtons();
                                }
                            }
                        });
            }
        }
    }

    public static void handle(final SPacketSyncData data) {
        CuriosSlotResources.CLIENT.setSlots(data.slotResources().getSlots());
        CuriosSlotResources.CLIENT.setAllEntitySlots(data.slotResources().getAllEntitySlots());
    }

    public static void handle(final SPacketSyncCurios data) {
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(data.entityId);

            if (entity instanceof LivingEntity livingEntity) {
                CuriosApi.getCuriosInventory(livingEntity)
                        .ifPresent(handler -> {
                            Map<String, ICurioStacksHandler> stacks = new LinkedHashMap<>();
                            CurioInventory inventory = livingEntity.getData(CuriosRegistry.INVENTORY.get());

                            for (Map.Entry<String, CompoundTag> entry : data.map.entrySet()) {
                                ICurioStacksHandler stacksHandler =
                                        new CurioStacksHandler(inventory, entry.getKey());
                                stacksHandler.applySyncTag(entry.getValue());
                                stacks.put(entry.getKey(), stacksHandler);
                            }
                            handler.setCurios(stacks);

                            if (entity instanceof LocalPlayer localPlayer
                                    && localPlayer.containerMenu instanceof ICuriosMenu curiosContainer) {
                                curiosContainer.resetSlots();
                            }
                        });
            }
        }
    }

    public static void handle(final SPacketGrabbedItem data) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;

        if (clientPlayer != null) {
            clientPlayer.containerMenu.setCarried(data.stack().copy());
        }
    }

    public static void handle(final SPacketSyncStack data) {
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(data.entityId());

            if (entity instanceof LivingEntity livingEntity) {
                CuriosApi.getCuriosInventory(livingEntity)
                        .flatMap(handler -> handler.getStacksHandler(data.curioId()))
                        .ifPresent(stacksHandler -> {
                            ItemStack stack = data.stack().copy();
                            CompoundTag compoundNBT = data.compoundTag();
                            int slot = data.slotId();
                            boolean cosmetic = SPacketSyncStack.HandlerType.fromValue(data.handlerType()) ==
                                    SPacketSyncStack.HandlerType.COSMETIC;

                            if (!compoundNBT.isEmpty()) {
                                NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                                CuriosApi.getCurio(stack).ifPresent(curio -> curio.readSyncData(
                                        new SlotContext(data.curioId(), livingEntity, slot, cosmetic,
                                                renderStates.size() > slot && renderStates.get(slot)),
                                        compoundNBT));
                            }

                            if (cosmetic) {
                                stacksHandler.getCosmeticStacks().setStackInSlot(slot, stack);
                            } else {
                                stacksHandler.getStacks().setStackInSlot(slot, stack);
                            }
                        });
            }
        }
    }

    public static void handle(SPacketSyncActiveState data) {
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null) {
            Entity entity = world.getEntity(data.entityId());

            if (entity instanceof LivingEntity) {
                CuriosApi.getCuriosInventory((LivingEntity) entity)
                        .flatMap(handler -> handler.getStacksHandler(data.curioId()))
                        .ifPresent(
                                stacksHandler -> {
                                    int index = data.slotId();
                                    NonNullList<Boolean> functionStatuses = stacksHandler.getActiveStates();

                                    if (functionStatuses.size() > index) {
                                        functionStatuses.set(index, data.value());
                                    }
                                });
            }
        }
    }
}
