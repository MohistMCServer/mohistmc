package com.mohistmc.mod.module.flywheel.backend.engine;

import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.backend.engine.embed.Environment;

public record GroupKey<I extends Instance>(InstanceType<I> instanceType, Environment environment) {
}
