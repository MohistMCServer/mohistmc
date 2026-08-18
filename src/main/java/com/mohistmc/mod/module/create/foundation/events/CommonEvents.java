package com.mohistmc.mod.module.create.foundation.events;

import com.mohistmc.mod.module.create.Create;
import com.mohistmc.mod.module.create.catnip.data.WorldAttached;
import com.mohistmc.mod.module.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.mohistmc.mod.module.create.content.kinetics.drill.CobbleGenOptimisation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * @author Mgazul
 * @date 2026/7/30 18:09
 */

@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        CapabilityMinecartController.onChunkUnloaded(event);
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Create.RAILWAYS.playerLogin(player);
    }

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        Create.REDSTONE_LINK_NETWORK_HANDLER.onLoadWorld(world);
        Create.TORQUE_PROPAGATOR.onLoadWorld(world);
        Create.RAILWAYS.levelLoaded(world);
        Create.LOGISTICS.levelLoaded(world);
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        LevelAccessor world = event.getLevel();
        Create.REDSTONE_LINK_NETWORK_HANDLER.onUnloadWorld(world);
        Create.TORQUE_PROPAGATOR.onUnloadWorld(world);
        WorldAttached.invalidateWorld(world);
        CobbleGenOptimisation.invalidateWorld(world);
    }
}
