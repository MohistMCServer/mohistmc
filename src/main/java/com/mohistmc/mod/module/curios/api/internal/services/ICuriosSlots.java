package com.mohistmc.mod.module.curios.api.internal.services;

import com.mohistmc.mod.module.curios.api.SlotContext;
import com.mohistmc.mod.module.curios.api.type.ISlotType;
import com.mohistmc.mod.module.curios.api.type.data.IEntitiesData;
import com.mohistmc.mod.module.curios.api.type.data.ISlotData;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ICuriosSlots {

    Map<String, ISlotType> getSlotTypes(boolean isClient);

    Map<String, ISlotType> getSlotTypes(LivingEntity livingEntity);

    Map<String, ISlotType> getSlotTypes(EntityType<?> entityType, boolean isClient);

    Map<String, ISlotType> getSlotTypes(ItemStack stack, boolean isClient);

    Map<String, ISlotType> getSlotTypes(ItemStack stack, LivingEntity livingEntity);

    ISlotData getSlotData(String id);

    IEntitiesData getEntitiesData();

    void registerPredicate(Identifier resourceLocation,
                           BiPredicate<SlotContext, ItemStack> slotContent);

    BiPredicate<SlotContext, ItemStack> getPredicate(Identifier resourceLocation);

    Map<Identifier, BiPredicate<SlotContext, ItemStack>> getPredicates();
}
