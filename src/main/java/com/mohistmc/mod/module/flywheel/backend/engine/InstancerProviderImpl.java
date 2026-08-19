package com.mohistmc.mod.module.flywheel.backend.engine;

import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.api.instance.Instancer;
import com.mohistmc.mod.module.flywheel.api.instance.InstancerProvider;
import com.mohistmc.mod.module.flywheel.api.model.Model;
import com.mohistmc.mod.module.flywheel.backend.engine.embed.GlobalEnvironment;

public record InstancerProviderImpl(EngineImpl engine) implements InstancerProvider {
    @Override
    public <I extends Instance> Instancer<I> instancer(InstanceType<I> type, Model model, int bias) {
        return engine.instancer(GlobalEnvironment.INSTANCE, type, model, bias);
    }
}
