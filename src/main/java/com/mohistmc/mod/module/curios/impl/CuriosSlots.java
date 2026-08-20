package com.mohistmc.mod.module.curios.impl;

import com.google.common.collect.ImmutableMap;
import com.mohistmc.mod.module.curios.api.CuriosResources;
import com.mohistmc.mod.module.curios.api.CuriosSlotTypes;
import com.mohistmc.mod.module.curios.api.CuriosTags;
import com.mohistmc.mod.module.curios.api.SlotContext;
import com.mohistmc.mod.module.curios.api.internal.services.ICuriosSlots;
import com.mohistmc.mod.module.curios.api.type.ISlotType;
import com.mohistmc.mod.module.curios.api.type.data.IEntitiesData;
import com.mohistmc.mod.module.curios.api.type.data.ISlotData;
import com.mohistmc.mod.module.curios.common.data.CuriosSlotResources;
import com.mohistmc.mod.module.curios.common.data.EntitiesData;
import com.mohistmc.mod.module.curios.common.data.SlotData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CuriosSlots implements ICuriosSlots {

    private static final Map<Identifier, BiPredicate<SlotContext, ItemStack>> PREDICATES =
            Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());

    static {
        PREDICATES.put(CuriosResources.resource("all"), (ctx, stack) -> true);
        PREDICATES.put(CuriosResources.resource("none"), (ctx, stack) -> false);
        PREDICATES.put(CuriosResources.resource("tag"),
                (ctx, stack) -> {
                    String id = ctx.identifier();
                    TagKey<Item> tag1 =
                            ItemTags.create(CuriosResources.resource(id));
                    return stack.is(tag1) || stack.is(CuriosTags.CURIO);
                });
    }

    private static CuriosSlotResources getSidedSlots(boolean isClient) {
        return isClient ? CuriosSlotResources.CLIENT : CuriosSlotResources.SERVER;
    }

    @Override
    public Map<String, ISlotType> getSlotTypes(boolean isClient) {
        return getSidedSlots(isClient).getSlots();
    }

    @Override
    public Map<String, ISlotType> getSlotTypes(LivingEntity livingEntity) {
        return getSidedSlots(livingEntity.level().isClientSide()).getEntitySlots(
                livingEntity.getType());
    }

    @Override
    public Map<String, ISlotType> getSlotTypes(EntityType<?> entityType, boolean isClient) {
        return getSidedSlots(isClient).getEntitySlots(entityType);
    }

    @Override
    public Map<String, ISlotType> getSlotTypes(ItemStack stack, boolean isClient) {
        Map<String, ISlotType> results = new TreeMap<>();

        for (ISlotType value : getSidedSlots(isClient).getSlots().values()) {
            String key = value.getId();

            if (value.isItemValid(new SlotContext(key, null, 0, false, true), stack)) {
                results.put(key, value);
            }
        }

        if (!stack.is(CuriosTags.GENERIC_EXCLUSIONS) && !results.isEmpty()) {
            String key = CuriosSlotTypes.Preset.CURIO.id();
            results.put(key, ISlotType.get(key));
        }
        return results;
    }

    @Override
    public Map<String, ISlotType> getSlotTypes(ItemStack stack, LivingEntity livingEntity) {
        Map<String, ISlotType> results = new TreeMap<>();
        Map<String, ISlotType> slots = getSlotTypes(livingEntity);

        for (Map.Entry<String, ISlotType> entry : slots.entrySet()) {
            ISlotType value = entry.getValue();

            if (value.isItemValid(new SlotContext(entry.getKey(), livingEntity, 0, false, true), stack)) {
                results.put(entry.getKey(), value);
            }
        }

        if (!stack.is(CuriosTags.GENERIC_EXCLUSIONS) && !results.isEmpty()) {
            String key = CuriosSlotTypes.Preset.CURIO.id();
            results.put(key, ISlotType.get(key));
        }
        return results;
    }

    @Override
    public ISlotData getSlotData(String id) {
        return new SlotData(id, false);
    }

    @Override
    public IEntitiesData getEntitiesData() {
        return new EntitiesData();
    }

    @Override
    public void registerPredicate(Identifier resourceLocation,
                                  BiPredicate<SlotContext, ItemStack> predicate) {
        PREDICATES.put(resourceLocation, predicate);
    }

    @Override
    public BiPredicate<SlotContext, ItemStack> getPredicate(Identifier resourceLocation) {
        return PREDICATES.get(resourceLocation);
    }

    @Override
    public Map<Identifier, BiPredicate<SlotContext, ItemStack>> getPredicates() {
        return ImmutableMap.copyOf(PREDICATES);
    }
}
