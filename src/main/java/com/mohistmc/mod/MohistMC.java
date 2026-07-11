package com.mohistmc.mod;

import com.mohistmc.mod.client.gui.EscGui;
import com.mohistmc.mod.client.gui.FakeMainGui;
import com.mohistmc.mod.client.renderer.BulletRenderer;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.register.BlockRegister;
import com.mohistmc.mod.register.ItemRegister;
import com.mohistmc.mod.register.ModEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MohistMC.MODID)
public class MohistMC {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mohistmc";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

     public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mohistmc")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ItemRegister.LOGO.get().getDefaultInstance()).displayItems((parameters, output) -> {
        ItemRegister.ALL_ITEMS.forEach(itemSupplier -> output.accept(itemSupplier.get()));
    }).build());

    public MohistMC(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BlockRegister.BLOCKS.register(modEventBus);
        ItemRegister.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        new FarmersDelight(modEventBus, modContainer);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent // on the mod event bus only on the physical client
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.BULLET.get(), BulletRenderer::new);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        }

        @SubscribeEvent
        public static void onGuiOpen(ScreenEvent.Opening e) {
            if (e.getScreen().getClass() == TitleScreen.class) {
                Minecraft.getInstance().getTutorial().setStep(TutorialSteps.NONE);
                e.setNewScreen(new FakeMainGui());
            }
            if (e.getScreen().getClass() == PauseScreen.class) {
                e.setNewScreen(new EscGui());
            }
        }
    }
}
