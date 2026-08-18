package com.mohistmc.mod.module.create.content.redstone.link;

import com.mohistmc.mod.module.create.catnip.data.Couple;
import com.mohistmc.mod.module.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import net.minecraft.core.BlockPos;

public interface IRedstoneLinkable {

    int getTransmittedStrength();

    void setReceivedStrength(int power);

    boolean isListening();

    boolean isAlive();

    Couple<Frequency> getNetworkKey();

    BlockPos getLocation();

}
