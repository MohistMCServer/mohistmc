package com.mohistmc.mod.module.flywheel.backend.engine;

import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.api.model.Model;
import com.mohistmc.mod.module.flywheel.backend.engine.embed.Environment;

public record InstancerKey<I extends Instance>(Environment environment, InstanceType<I> type, Model model, int bias) {
}
