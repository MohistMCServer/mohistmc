package com.mohistmc.mod.module;

import com.google.common.collect.ImmutableMap;
import com.mohistmc.mod.mixin.AccessorRangedAttribute;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/**
 * @author Mgazul
 * @date 2026/8/18 17:45
 */
public class AttributeFixMod {

    private static final Map<Identifier, Double> NEW_DEFAULT_VALUES;
    private static AttributeFixMod instance;
    private boolean hasInitialized;

    static {
        NEW_DEFAULT_VALUES = ImmutableMap.of(Identifier.fromNamespaceAndPath("minecraft", "max_health"), Double.MAX_VALUE, Identifier.fromNamespaceAndPath("minecraft", "armor"), Double.MAX_VALUE, Identifier.fromNamespaceAndPath("minecraft", "armor_toughness"), Double.MAX_VALUE, Identifier.fromNamespaceAndPath("minecraft", "attack_damage"), Double.MAX_VALUE, Identifier.fromNamespaceAndPath("minecraft", "attack_knockback"), Double.MAX_VALUE);
    }

    public AttributeFixMod() {
        this.hasInitialized = false;
    }

    public void init() {
        if (this.hasInitialized) {
            throw new IllegalStateException("The AttributeFix has already been initialized.");
        }
        for (Attribute attribute : BuiltInRegistries.ATTRIBUTE) {
            Identifier id = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
            if (id != null && attribute instanceof RangedAttribute ranged && ranged instanceof AccessorRangedAttribute accessor) {
                if (NEW_DEFAULT_VALUES.containsKey(id)) {
                    accessor.attributefix$setMaxValue(NEW_DEFAULT_VALUES.get(id));
                }
            }
        }
        this.hasInitialized = true;
    }

    public static AttributeFixMod getInstance() {
        if (AttributeFixMod.instance == null) {
            AttributeFixMod.instance = new AttributeFixMod();
        }
        return AttributeFixMod.instance;
    }

}
