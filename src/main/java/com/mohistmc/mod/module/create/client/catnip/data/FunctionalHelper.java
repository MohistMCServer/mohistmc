package com.mohistmc.mod.module.create.client.catnip.data;

import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class FunctionalHelper {

    public static <U> Function<Object, @Nullable U> filterAndCast(Class<? extends U> clazz) {
        return t -> clazz.isInstance(t) ? clazz.cast(t) : null;
    }

}