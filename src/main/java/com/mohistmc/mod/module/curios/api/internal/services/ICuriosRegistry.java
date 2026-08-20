package com.mohistmc.mod.module.curios.api.internal.services;

import com.mohistmc.mod.module.curios.api.CurioAttributeModifiers;
import net.minecraft.core.component.DataComponentType;

public interface ICuriosRegistry {

    DataComponentType<CurioAttributeModifiers> getAttributeModifierComponent();
}
