package com.mohistmc.mod.module.create.client.vanillin.compose;

import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;

@FunctionalInterface
public interface VisualizationPredicate<T> {
    VisualizationPredicate<?> ALWAYS_EXIST = (ctx, t) -> true;

    boolean shouldVisualize(VisualizationContext ctx, T entity);

    @SuppressWarnings("unchecked")
    static <T> VisualizationPredicate<T> alwaysTrue() {
        return (VisualizationPredicate<T>) ALWAYS_EXIST;
    }
}
