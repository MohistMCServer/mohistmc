package com.mohistmc.mod.mixin;

import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/8/18 17:51
 */

@Mixin({ RangedAttribute.class })
public interface AccessorRangedAttribute
{
    @Accessor("minValue")
    @Mutable
    void attributefix$setMinValue(final double p0);

    @Accessor("maxValue")
    @Mutable
    void attributefix$setMaxValue(final double p0);
}

