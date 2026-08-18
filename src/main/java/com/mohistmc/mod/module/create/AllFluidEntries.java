package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.infrastructure.fluids.FluidEntry;

public class AllFluidEntries {
    public static final FluidEntry POTION = new FluidEntry(AllFluidTypes.POTION);
    public static final FluidEntry TEA = new FluidEntry(AllFluidTypes.TEA);
    public static final FluidEntry MILK = new FluidEntry(AllFluidTypes.MILK);
    public static final FluidEntry HONEY = new FluidEntry(AllFluidTypes.HONEY);
    public static final FluidEntry CHOCOLATE = new FluidEntry(AllFluidTypes.CHOCOLATE);

    public static void init() {
    }
}
