package com.mohistmc.mod.module.flywheel.lib.task;

import com.google.common.collect.ImmutableList;
import com.mohistmc.mod.module.flywheel.api.task.Plan;
import com.mohistmc.mod.module.flywheel.api.task.TaskExecutor;

import java.util.List;

public record NestedPlan<C>(List<Plan<C>> parallelPlans) implements SimplyComposedPlan<C> {
    @SafeVarargs
    public static <C> NestedPlan<C> of(Plan<C>... plans) {
        return new NestedPlan<>(ImmutableList.copyOf(plans));
    }

    @Override
    public void execute(TaskExecutor taskExecutor, C context, Runnable onCompletion) {
        if (parallelPlans.isEmpty()) {
            onCompletion.run();
            return;
        }

        var size = parallelPlans.size();

        if (size == 1) {
            parallelPlans.getFirst().execute(taskExecutor, context, onCompletion);
            return;
        }

        var wait = new Synchronizer(size, onCompletion);
        for (var plan : parallelPlans) {
            plan.execute(taskExecutor, context, wait);
        }
    }

    @Override
    public Plan<C> and(Plan<C> plan) {
        return new NestedPlan<>(ImmutableList.<Plan<C>>builder().addAll(parallelPlans).add(plan).build());
    }
}
