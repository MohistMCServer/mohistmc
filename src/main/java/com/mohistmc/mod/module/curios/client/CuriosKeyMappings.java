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

package com.mohistmc.mod.module.curios.client;

import com.mohistmc.mod.module.curios.api.CuriosResources;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CuriosKeyMappings {

    public static final KeyMapping.Category CURIOS_KEY_CATEGORY = new KeyMapping.Category(
            CuriosResources.resource("key.curios.category"));

    public static final KeyMapping OPEN_CURIOS_INVENTORY =
            new KeyMapping("key.curios.open.desc", GLFW.GLFW_KEY_G, CURIOS_KEY_CATEGORY);
}
