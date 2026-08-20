package com.mohistmc.mod.module.curios.api.internal;

import com.mohistmc.mod.module.curios.api.internal.services.client.ICuriosClientExtensions;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CuriosClientServices {

    public static final ICuriosClientExtensions EXTENSIONS =
            CuriosServices.load(ICuriosClientExtensions.class);
}
