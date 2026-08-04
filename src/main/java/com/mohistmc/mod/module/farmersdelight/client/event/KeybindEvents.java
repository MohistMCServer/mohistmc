package com.mohistmc.mod.module.farmersdelight.client.event;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.item.SkilletItem;
import com.mohistmc.mod.module.farmersdelight.common.network.payload.FlipSkilletPayload;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = FarmersDelight.MODID, value = Dist.CLIENT)
public class KeybindEvents
{
	@SubscribeEvent
	public static void preClientTick(ClientTickEvent.Pre event) { // Run this on pre so inputs don't get eaten up.
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player != null && player.isUsingItem()) {
			ItemStack useItem = player.getUseItem();
			if (useItem.getItem() instanceof SkilletItem && !useItem.has(ModDataComponents.SKILLET_FLIP_TIMESTAMP.get())) {
				while (mc.options.keyAttack.consumeClick()) {
					ClientPacketDistributor.sendToServer(FlipSkilletPayload.INSTANCE);
				}
			}
		}
	}
}
