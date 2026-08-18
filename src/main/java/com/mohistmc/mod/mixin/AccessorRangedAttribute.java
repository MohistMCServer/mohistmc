package com.mohistmc.mod.mixin;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

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

