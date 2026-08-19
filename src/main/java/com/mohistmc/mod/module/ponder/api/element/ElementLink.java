package com.mohistmc.mod.module.ponder.api.element;

import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface ElementLink<T extends @Nullable PonderElement> {
    UUID getId();

    T cast(PonderElement e);
}