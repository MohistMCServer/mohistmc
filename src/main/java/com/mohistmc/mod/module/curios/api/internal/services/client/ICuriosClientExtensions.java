package com.mohistmc.mod.module.curios.api.internal.services.client;

import com.mohistmc.mod.module.curios.api.client.ICurioRenderer;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ICuriosClientExtensions {

    void registerCurioRenderer(Item item, Supplier<ICurioRenderer> curioRenderer);

    ICurioRenderer getCurioRenderer(Item item);
}
