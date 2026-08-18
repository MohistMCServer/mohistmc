package com.mohistmc.mod.module.create.content.equipment.clipboard;

import com.mojang.serialization.Codec;
import com.mohistmc.mod.module.create.AllDataComponents;
import com.mohistmc.mod.module.create.infrastructure.component.ClipboardContent;
import com.mohistmc.mod.module.create.infrastructure.component.ClipboardType;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.DataComponentPredicate;

public record ClipboardTypePredicate(ClipboardType type) implements DataComponentPredicate {
    public static final Codec<ClipboardTypePredicate> CODEC = ClipboardType.CODEC.xmap(
        ClipboardTypePredicate::new,
        ClipboardTypePredicate::type
    );

    @Override
    public boolean matches(DataComponentGetter components) {
        ClipboardContent content = components.get(AllDataComponents.CLIPBOARD_CONTENT);
        return type == (content == null ? ClipboardType.EMPTY : content.type());
    }
}
