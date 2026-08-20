package com.mohistmc.mod.module.farmersdelight.client.event;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.client.gui.CookingPotScreen;
import com.mohistmc.mod.module.farmersdelight.client.gui.CookingPotTooltip;
import com.mohistmc.mod.module.farmersdelight.client.gui.HUDOverlays;
import com.mohistmc.mod.module.farmersdelight.client.particle.SparkleParticle;
import com.mohistmc.mod.module.farmersdelight.client.particle.StarParticle;
import com.mohistmc.mod.module.farmersdelight.client.particle.SteamParticle;
import com.mohistmc.mod.module.farmersdelight.client.renderer.CanvasSignRenderer;
import com.mohistmc.mod.module.farmersdelight.client.renderer.CuttingBoardRenderer;
import com.mohistmc.mod.module.farmersdelight.client.renderer.DefaultStoveRenderer;
import com.mohistmc.mod.module.farmersdelight.client.renderer.HangingCanvasSignRenderer;
import com.mohistmc.mod.module.farmersdelight.client.renderer.SkilletItemRenderer;
import com.mohistmc.mod.module.farmersdelight.client.renderer.SkilletRenderer;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlockEntityTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModEntityTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModMenuTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModParticleTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(modid = FarmersDelight.MODID, value = Dist.CLIENT)
public class ClientSetupEvents
{

	@SubscribeEvent
	public static void registerCustomTooltipRenderers(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(CookingPotTooltip.CookingPotTooltipComponent.class, CookingPotTooltip::new);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		HUDOverlays.register(event);
	}

	@SubscribeEvent
	public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
		event.register(Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "skillet_ingredient"), SkilletItemRenderer.Unbaked.MAP_CODEC);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntityTypes.ROTTEN_TOMATO.get(), ThrownItemRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CUTTING_BOARD.get(), CuttingBoardRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.SKILLET.get(), SkilletRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.STOVE.get(), DefaultStoveRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CANVAS_SIGN.get(), CanvasSignRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.HANGING_CANVAS_SIGN.get(), HangingCanvasSignRenderer::new);
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenuTypes.COOKING_POT.get(), CookingPotScreen::new);
	}

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.STAR.get(), StarParticle.Factory::new);
		event.registerSpriteSet(ModParticleTypes.STEAM.get(), SteamParticle.Factory::new);
		event.registerSpriteSet(ModParticleTypes.SPARKLE.get(), SparkleParticle.Factory::new);
	}
}
