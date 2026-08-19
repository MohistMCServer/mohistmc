package com.mohistmc.mod.module.ponder.api;

/**
 * Used for simulating BE's in a client-only setting (like Ponder)
 */
public interface VirtualBlockEntity {

    void markVirtual();

    boolean isVirtual();

}