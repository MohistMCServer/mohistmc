/*
 * Copyright (c) 2018-2024 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.mohistmc.mod.module.curios.api.internal;

import com.mohistmc.mod.module.curios.api.internal.services.ICuriosCodecs;
import com.mohistmc.mod.module.curios.api.internal.services.ICuriosExtensions;
import com.mohistmc.mod.module.curios.api.internal.services.ICuriosNetwork;
import com.mohistmc.mod.module.curios.api.internal.services.ICuriosRegistry;
import com.mohistmc.mod.module.curios.api.internal.services.ICuriosSlots;
import java.util.ServiceLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CuriosServices {

    public static final ICuriosCodecs CODECS = load(ICuriosCodecs.class);
    public static final ICuriosSlots SLOTS = load(ICuriosSlots.class);
    public static final ICuriosRegistry REGISTRY = load(ICuriosRegistry.class);
    public static final ICuriosExtensions EXTENSIONS = load(ICuriosExtensions.class);
    public static final ICuriosNetwork NETWORK = load(ICuriosNetwork.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(
                        () -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
