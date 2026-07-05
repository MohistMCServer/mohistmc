package com.mohistmc.mod.client;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.client.component.MapTooltipComponent;
import com.mojang.datafixers.util.Either;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

/**
 * @author Mgazul
 * @date 2026/4/16 03:07
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = MohistMC.MODID)
public class MapTooltipHandler {

    @SubscribeEvent
    private static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(MapTooltipComponent.class, Function.identity());
    }

    @SubscribeEvent
    private static void renderTooltip(RenderTooltipEvent.GatherComponents event) {
        ItemStack item = event.getItemStack();
        if (!item.is(Items.FILLED_MAP)) {
            return;
        }
        event.getTooltipElements().add(1, Either.right(new MapTooltipComponent(item)));
    }
}
