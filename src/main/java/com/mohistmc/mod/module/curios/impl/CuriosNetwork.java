package com.mohistmc.mod.module.curios.impl;

import com.mohistmc.mod.module.curios.api.SlotContext;
import com.mohistmc.mod.module.curios.api.internal.services.ICuriosNetwork;
import com.mohistmc.mod.module.curios.common.network.server.SPacketBreak;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public class CuriosNetwork implements ICuriosNetwork {

    @Override
    public void breakCurioInSlot(SlotContext slotContext) {
        LivingEntity livingEntity = slotContext.entity();

        if (livingEntity != null) {
            PacketDistributor
                    .sendToPlayersTrackingEntityAndSelf(livingEntity,
                            new SPacketBreak(livingEntity.getId(),
                                    slotContext.identifier(),
                                    slotContext.index()));
        }
    }
}
