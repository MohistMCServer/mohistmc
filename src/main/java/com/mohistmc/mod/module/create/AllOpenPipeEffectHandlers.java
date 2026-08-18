package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.api.effect.OpenPipeEffectHandler;
import com.mohistmc.mod.module.create.api.registry.SimpleRegistry;
import com.mohistmc.mod.module.create.impl.effect.*;
import net.minecraft.tags.FluidTags;

public class AllOpenPipeEffectHandlers {
    public static void register() {
        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(
            FluidTags.WATER,
            new WaterEffectHandler()
        ));
        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(
            FluidTags.LAVA,
            new LavaEffectHandler()
        ));
        OpenPipeEffectHandler.REGISTRY.registerProvider(SimpleRegistry.Provider.forFluidTag(
            AllFluidTags.MILK,
            new MilkEffectHandler()
        ));
        OpenPipeEffectHandler.REGISTRY.register(AllFluids.POTION, new PotionEffectHandler());
        OpenPipeEffectHandler.REGISTRY.register(AllFluids.TEA, new TeaEffectHandler());
    }
}
