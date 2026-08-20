package com.mohistmc.mod.module.curios.api.internal;

import com.mohistmc.mod.module.curios.api.internal.services.client.ICuriosClientExtensions;
import com.mohistmc.mod.module.curios.impl.CuriosClientExtensions;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CuriosClientServices {

    public static final ICuriosClientExtensions EXTENSIONS = new CuriosClientExtensions();
}
