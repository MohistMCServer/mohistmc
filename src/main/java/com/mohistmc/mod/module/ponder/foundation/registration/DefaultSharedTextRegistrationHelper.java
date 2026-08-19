package com.mohistmc.mod.module.ponder.foundation.registration;

import com.mohistmc.mod.module.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.Identifier;

public class DefaultSharedTextRegistrationHelper implements SharedTextRegistrationHelper {

    private final String namespace;
    private final PonderLocalization localization;

    public DefaultSharedTextRegistrationHelper(String namespace, PonderLocalization localization) {
        this.namespace = namespace;
        this.localization = localization;
    }

    @Override
    public void registerSharedText(String key, String en_us) {
        localization.registerShared(Identifier.fromNamespaceAndPath(namespace, key), en_us);
    }
}
