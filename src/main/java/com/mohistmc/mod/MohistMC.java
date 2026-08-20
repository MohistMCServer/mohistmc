package com.mohistmc.mod;

import com.mohistmc.mod.client.gui.EscGui;
import com.mohistmc.mod.client.gui.FakeMainGui;
import com.mohistmc.mod.client.gui.YouerInventoryScreen;
import com.mohistmc.mod.client.renderer.BulletRenderer;
import com.mohistmc.mod.module.AttributeFixMod;
import com.mohistmc.mod.module.create.Create;
import com.mohistmc.mod.module.curios.CuriosCommonMod;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.mail.Mail;
import com.mohistmc.mod.module.shop.Shop;
import com.mohistmc.mod.module.shop.common.data.ShopData;
import com.mohistmc.mod.register.BlockRegister;
import com.mohistmc.mod.register.ItemRegister;
import com.mohistmc.mod.register.ModEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.slf4j.Logger;

@Mod(MohistMC.MODID)
@EventBusSubscriber(modid = MohistMC.MODID)
public class MohistMC {
    public static final String MODID = "mohistmc";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

     public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOHISTMC_TAB = CREATIVE_MODE_TABS.register("mohistmc_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mohistmc")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ItemRegister.LOGO.get().getDefaultInstance()).displayItems((parameters, output) -> {
        ItemRegister.ITEMS.getEntries().forEach(itemSupplier -> output.accept(itemSupplier.get()));
    }).build());

    public MohistMC(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BlockRegister.BLOCKS.register(modEventBus);
        ItemRegister.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        new FarmersDelight(modEventBus, modContainer);
        new Shop(modEventBus, modContainer);
        new Mail(modEventBus, modContainer);
        new Create(modEventBus, modContainer);
        new CuriosCommonMod(modEventBus, modContainer);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event) {
        // AttributeFixMod.getInstance().init();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // 初始化商店数据文件路径
        var server = event.getServer();
        var serverDir = server.getWorldPath(LevelResource.ROOT);
        ShopData.setDataFile(serverDir);
        // 触发初始化（加载/创建数据）
        ShopData.getAllShops();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // IME 输入法上下文在 GLFW 窗口焦点变化时由 GLFW 内部管理，无需额外处理
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
            System.out.println(e.getScreen().getClass());
            if (e.getScreen().getClass() == InventoryScreen.class) {
                e.setNewScreen(new YouerInventoryScreen(Minecraft.getInstance().player));
            }
        }
    }
}
