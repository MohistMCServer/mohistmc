package com.mohistmc.mod.module.flywheel.lib.task;

import com.mohistmc.mod.module.flywheel.api.task.TaskExecutor;
import com.mohistmc.mod.module.flywheel.lib.task.functional.RunnableWithContext;

public record RunnablePlan<C>(RunnableWithContext<C> runnable) implements SimplyComposedPlan<C> {
    public static <C> RunnablePlan<C> of(RunnableWithContext<C> runnable) {
        return new RunnablePlan<>(runnable);
    }

    public static <C> RunnablePlan<C> of(RunnableWithContext.Ignored<C> runnable) {
        return new RunnablePlan<>(runnable);
    }

    @Override
    public void execute(TaskExecutor taskExecutor, C context, Runnable onCompletion) {
        runnable.accept(context);
        onCompletion.run();
    }
}
