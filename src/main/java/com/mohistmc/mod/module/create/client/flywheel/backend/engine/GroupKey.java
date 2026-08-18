package com.mohistmc.mod.module.create.client.flywheel.backend.engine;

import com.mohistmc.mod.module.create.client.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.create.client.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.create.client.flywheel.backend.engine.embed.Environment;

public record GroupKey<I extends Instance>(InstanceType<I> instanceType, Environment environment) {
}
