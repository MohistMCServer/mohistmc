package com.mohistmc.mod.module.create;

import java.util.function.Supplier;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
        NeoForgeRegistries.FLUID_TYPES,
        MOD_ID
    );

    public static final Supplier<FluidType> POTION = FLUID_TYPES.register(
        "potion",
        () -> new FluidType(FluidType.Properties.create().descriptionId("fluid.mohistmc.potion"))
    );
    public static final Supplier<FluidType> TEA = FLUID_TYPES.register(
        "tea",
        () -> new FluidType(FluidType.Properties.create().descriptionId("fluid.mohistmc.tea"))
    );
    public static final Supplier<FluidType> MILK = FLUID_TYPES.register(
        "milk",
        () -> new FluidType(FluidType.Properties.create().descriptionId("fluid.mohistmc.milk"))
    );
    public static final Supplier<FluidType> HONEY = FLUID_TYPES.register(
        "honey",
        () -> new FluidType(
            FluidType.Properties.create().density(1400).viscosity(2000).descriptionId("fluid.mohistmc.honey")
        )
    );
    public static final Supplier<FluidType> CHOCOLATE = FLUID_TYPES.register(
        "chocolate",
        () -> new FluidType(
            FluidType.Properties.create().density(1400).viscosity(1500).descriptionId("fluid.mohistmc.chocolate")
        )
    );

    public static void init() {
    }
}
