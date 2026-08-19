package com.mohistmc.mod.module.flywheel.backend.engine;

import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.api.instance.Instancer;
import com.mohistmc.mod.module.flywheel.backend.engine.embed.Environment;

public abstract class AbstractInstancer<I extends Instance> implements Instancer<I> {
    public final InstanceType<I> type;
    public final Environment environment;
    public final Recreate<I> recreate;

    protected AbstractInstancer(InstancerKey<I> key, Recreate<I> recreate) {
        type = key.type();
        environment = key.environment();
        this.recreate = recreate;
    }

    public abstract InstanceHandleImpl.State<I> revealInstance(InstanceHandleImpl<I> handle, I instance);

    public abstract int instanceCount();

    public abstract void parallelUpdate();

    public abstract void delete();

    public abstract void clear();

    @Override
    public String toString() {
        return "AbstractInstancer[" + instanceCount() + ']';
    }

    public record Recreate<I extends Instance>(InstancerKey<I> key, DrawManager<?> drawManager) {
        public AbstractInstancer<I> recreate() {
            return drawManager.getInstancer(key);
        }
    }
}
