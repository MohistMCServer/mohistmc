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
import com.mohistmc.mod.module.curios.impl.CuriosCodecs;
import com.mohistmc.mod.module.curios.impl.CuriosExtensions;
import com.mohistmc.mod.module.curios.impl.CuriosNetwork;
import com.mohistmc.mod.module.curios.impl.CuriosRegistry;
import com.mohistmc.mod.module.curios.impl.CuriosSlots;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CuriosServices {

    public static final ICuriosCodecs CODECS = new CuriosCodecs();
    public static final ICuriosSlots SLOTS = new CuriosSlots();
    public static final ICuriosRegistry REGISTRY = new CuriosRegistry();
    public static final ICuriosExtensions EXTENSIONS = new CuriosExtensions();
    public static final ICuriosNetwork NETWORK = new CuriosNetwork();
}
