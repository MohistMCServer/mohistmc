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

package com.mohistmc.mod.module.curios.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mohistmc.mod.module.curios.CuriosConstants;
import com.mohistmc.mod.module.curios.api.CuriosResources;
import com.mohistmc.mod.module.curios.api.type.ISlotType;
import com.mohistmc.mod.module.curios.common.slot.SlotType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public class CuriosSlotResources {

    public static final Identifier ID = CuriosResources.resource("mohistmc_slots");
    public static final StreamCodec<RegistryFriendlyByteBuf, CuriosSlotResources> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.registry(Registries.ENTITY_TYPE),
                            ByteBufCodecs.collection(
                                    HashSet::new,
                                    ByteBufCodecs.STRING_UTF8
                            )
                    ),
                    curiosEntityResources -> {
                        Map<EntityType<?>, Map<String, ISlotType>> map = curiosEntityResources.entitySlots;
                        Map<EntityType<?>, Set<String>> result = new HashMap<>();
                        for (Map.Entry<EntityType<?>, Map<String, ISlotType>> entry : map.entrySet()) {
                            result.put(entry.getKey(), entry.getValue().keySet());
                        }
                        return result;
                    },
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ISlotType.STREAM_CODEC
                    ),
                    CuriosSlotResources::getSlots,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.collection(
                                    HashSet::new,
                                    ByteBufCodecs.STRING_UTF8
                            )
                    ),
                    CuriosSlotResources::getModsFromSlots,
                    CuriosSlotResources::new
            );
    public static CuriosSlotResources SERVER;
    public static CuriosSlotResources CLIENT = new CuriosSlotResources();
    private Map<String, ISlotType> slots = ImmutableMap.of();
    private Map<EntityType<?>, Map<String, ISlotType>> entitySlots = ImmutableMap.of();
    private Set<String> configSlots = ImmutableSet.of();
    private Map<String, Set<String>> idToMods = ImmutableMap.of();

    // Built-in slot definitions, constructed directly as objects — no datapack parsing involved.
    // Players get every slot by default, each with size 1 (the entities mechanism is removed).
    private static final List<ISlotType> BUILTIN_SLOTS = List.of(
            new SlotType("back", 80, 1, true, false, Identifier.parse("mohistmc:slot/empty_back_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("belt", 180, 1, true, false, Identifier.parse("mohistmc:slot/empty_belt_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("body", 100, 1, true, false, Identifier.parse("mohistmc:slot/empty_body_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("bracelet", 120, 1, true, false, Identifier.parse("mohistmc:slot/empty_bracelet_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("charm", 200, 1, true, false, Identifier.parse("mohistmc:slot/empty_charm_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("curio", 20, 1, true, false, Identifier.parse("mohistmc:slot/empty_curio_slot"), true, Set.of(), Set.of()),
            new SlotType("feet", 190, 1, true, false, Identifier.parse("mohistmc:slot/empty_feet_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("hands", 140, 1, true, false, Identifier.parse("mohistmc:slot/empty_hands_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("head", 40, 1, true, false, Identifier.parse("mohistmc:slot/empty_head_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("necklace", 60, 1, true, false, Identifier.parse("mohistmc:slot/empty_necklace_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of()),
            new SlotType("ring", 160, 1, true, false, Identifier.parse("mohistmc:slot/empty_ring_slot"), true, Set.of(CuriosResources.resource("tag")), Set.of())
    );

    public CuriosSlotResources() {
    }

    public CuriosSlotResources(Map<EntityType<?>, Set<String>> entitySlots,
                               Map<String, ISlotType> slots, Map<String, Set<String>> idToMods) {
        this.slots = slots;
        Map<EntityType<?>, Map<String, ISlotType>> newEntitySlots = new LinkedHashMap<>();
        entitySlots.forEach((k, v) -> {
            Map<String, ISlotType> slotTypes = newEntitySlots.computeIfAbsent(k, (k1) -> new HashMap<>());
            for (String s : v) {
                ISlotType slotType = slots.get(s);

                if (slotType != null) {
                    slotTypes.put(s, slotType);
                }
            }
        });
        this.entitySlots = ImmutableMap.copyOf(newEntitySlots);
        this.idToMods = ImmutableMap.copyOf(idToMods);
    }

    public void populateData() {
        // Slots are defined directly in code — no datapack/config parsing involved.
        Map<String, ISlotType> slotTypes = new LinkedHashMap<>();
        for (ISlotType slotType : BUILTIN_SLOTS) {
            slotTypes.put(slotType.getId(), slotType);
        }
        this.slots = ImmutableMap.copyOf(slotTypes);

        Map<String, ImmutableSet.Builder<String>> modMap = new HashMap<>();
        for (String id : slotTypes.keySet()) {
            modMap.computeIfAbsent(id, (k) -> ImmutableSet.builder()).add("mohistmc");
        }
        this.idToMods = modMap.entrySet().stream()
                .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().build()));

        // Players get every slot by default (entities mechanism removed).
        this.entitySlots = ImmutableMap.of(EntityTypes.PLAYER, this.slots);

        CuriosConstants.LOG.info("Loaded {} curio slots", this.slots.size());
    }

    public Map<EntityType<?>, Map<String, ISlotType>> getAllEntitySlots() {
        return this.entitySlots;
    }

    public void setAllEntitySlots(Map<EntityType<?>, Map<String, ISlotType>> slots) {
        this.entitySlots = slots;
    }

    public Map<String, ISlotType> getPlayerSlots() {
        return this.getEntitySlots(EntityTypes.PLAYER);
    }

    public Map<String, ISlotType> getEntitySlots(EntityType<?> type) {

        if (this.entitySlots.containsKey(type)) {
            return this.entitySlots.get(type);
        }
        return ImmutableMap.of();
    }

    public Map<String, Set<String>> getModsFromSlots() {
        return this.idToMods;
    }

    public Map<String, ISlotType> getSlots() {
        return this.slots;
    }

    public void setSlots(Map<String, ISlotType> slots) {
        this.slots = slots;
    }

    @Nullable
    public ISlotType getSlot(String id) {
        return this.slots.get(id);
    }

    public Set<String> getConfigSlots() {
        return this.configSlots;
    }
}
