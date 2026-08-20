package com.mohistmc.mod.module.curios.api.internal.services;

import com.mohistmc.mod.module.curios.api.type.ISlotType;
import com.mohistmc.mod.module.curios.api.type.data.IEntitiesData;
import com.mohistmc.mod.module.curios.api.type.data.ISlotData;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ICuriosCodecs {

    Codec<ISlotType> slotTypeCodec();

    Codec<ISlotData.Entry> slotDataEntryCodec();

    Codec<IEntitiesData.Entry> entitiesDataEntryCodec();

    Codec<Holder<Attribute>> slotAttributeCodec();

    StreamCodec<RegistryFriendlyByteBuf, Holder<Attribute>> slotAttributeStreamCodec();

    StreamCodec<RegistryFriendlyByteBuf, ISlotType> slotTypeStreamCodec();
}
