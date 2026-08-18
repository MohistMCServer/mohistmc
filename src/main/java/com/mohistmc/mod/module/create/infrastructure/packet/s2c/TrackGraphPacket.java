package com.mohistmc.mod.module.create.infrastructure.packet.s2c;

import java.util.UUID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public abstract class TrackGraphPacket implements Packet<ClientGamePacketListener> {
    public UUID graphId;
    public int netId;
    public boolean packetDeletesGraph;
}
