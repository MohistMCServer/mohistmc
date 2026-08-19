package com.mohistmc.mod.module.flywheel.api.instance;

import com.mohistmc.mod.module.flywheel.api.backend.BackendImplemented;

@BackendImplemented
public interface InstanceHandle {
    void setChanged();

    void setDeleted();

    void setVisible(boolean visible);

    boolean isVisible();
}
