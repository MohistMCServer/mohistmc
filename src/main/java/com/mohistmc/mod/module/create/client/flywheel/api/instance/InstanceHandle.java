package com.mohistmc.mod.module.create.client.flywheel.api.instance;

import com.mohistmc.mod.module.create.client.flywheel.api.backend.BackendImplemented;

@BackendImplemented
public interface InstanceHandle {
    void setChanged();

    void setDeleted();

    void setVisible(boolean visible);

    boolean isVisible();
}
