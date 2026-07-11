package com.mohistmc.mod.module.farmersdelight.client.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookSearchCategoriesEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.Nullable;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.client.gui.CookingPotScreen;
import com.mohistmc.mod.module.farmersdelight.client.gui.CookingPotTooltip;
import com.mohistmc.mod.module.farmersdelight.client.gui.HUDOverlays;
import com.mohistmc.mod.module.farmersdelight.client.particle.SparkleParticle;
import com.mohistmc.mod.module.farmersdelight.client.particle.StarParticle;
import com.mohistmc.mod.module.farmersdelight.client.particle.SteamParticle;
import com.mohistmc.mod.module.farmersdelight.client.renderer.*;
import com.mohistmc.mod.module.farmersdelight.common.EnumParameters;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.StoveBlockEntity;
import com.mohistmc.mod.module.farmersdelight.common.registry.*;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeCategories;

@EventBusSubscriber(modid = FarmersDelight.MODID, value = Dist.CLIENT)
public class ClientSetupEvents
{
	// TODO unnecessary? data component covers this.
//	public static void init(final FMLClientSetupEvent event) {
//
//		event.enqueueWork(() -> ItemProperties.register(ModItems.SKILLET.get(), Identifier.withDefaultNamespace("cooking"),
//			(stack, world, entity, s) -> stack.getOrDefault(ModDataComponents.SKILLET_INGREDIENT, ItemStackWrapper.EMPTY).getStack().isEmpty() ? 0 : 1)
//		);
//	}

	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions()
		{
			@Override
			public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity living, InteractionHand hand, ItemStack stack) {
				return stack.has(ModDataComponents.SKILLET_FLIP_TIMESTAMP.get()) ? EnumParameters.PROXY_SKILLET_FLIP.getValue() : null;
			}
		}, ModItems.SKILLET.get());
	}

	@SubscribeEvent
	public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
		event.register(
			Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "skillet_special"),
			SkilletItemRenderer.Unbaked.MAP_CODEC
		);
	}

	@SubscribeEvent
	public static void registerRecipeBookCategories(RegisterRecipeBookSearchCategoriesEvent event) {
		ModRecipeCategories.init(event);
	}

	@SubscribeEvent
	public static void registerCustomTooltipRenderers(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(CookingPotTooltip.CookingPotTooltipComponent.class, CookingPotTooltip::new);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		HUDOverlays.register(event);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntityTypes.ROTTEN_TOMATO.get(), ThrownItemRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.STOVE.get(), DefaultStoveRenderer<StoveBlockEntity>::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CUTTING_BOARD.get(), CuttingBoardRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CANVAS_SIGN.get(), CanvasSignRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.HANGING_CANVAS_SIGN.get(), HangingCanvasSignRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.SKILLET.get(), SkilletRenderer::new);
	}

	@SubscribeEvent
	public static void registerMenuScreens(RegisterMenuScreensEvent event) {
		event.register(ModMenuTypes.COOKING_POT.get(), CookingPotScreen::new);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.STAR.get(), StarParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.STEAM.get(), SteamParticle.Provider::new);
		event.registerSpriteSet(ModParticleTypes.SPARKLE.get(), SparkleParticle.Provider::new);
	}
}
