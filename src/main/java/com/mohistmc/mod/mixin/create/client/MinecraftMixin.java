package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.mod.module.create.AllPackets;
import com.mohistmc.mod.module.create.catnip.data.WorldAttached;
import com.mohistmc.mod.module.create.client.Create;
import com.mohistmc.mod.module.create.client.catnip.animation.AnimationTickHolder;
import com.mohistmc.mod.module.create.client.catnip.ghostblock.GhostBlocks;
import com.mohistmc.mod.module.create.client.catnip.outliner.Outliner;
import com.mohistmc.mod.module.create.client.catnip.placement.PlacementClient;
import com.mohistmc.mod.module.create.client.content.contraptions.ContraptionHandlerClient;
import com.mohistmc.mod.module.create.client.content.contraptions.actors.seat.ContraptionPlayerPassengerRotation;
import com.mohistmc.mod.module.create.client.content.contraptions.actors.trainControls.ControlsHandler;
import com.mohistmc.mod.module.create.client.content.contraptions.chassis.ChassisRangeDisplay;
import com.mohistmc.mod.module.create.client.content.contraptions.minecart.CouplingHandlerClient;
import com.mohistmc.mod.module.create.client.content.contraptions.minecart.CouplingRenderer;
import com.mohistmc.mod.module.create.client.content.contraptions.wrench.RadialWrenchHandler;
import com.mohistmc.mod.module.create.client.content.decoration.girder.GirderWrenchBehaviorHandler;
import com.mohistmc.mod.module.create.client.content.equipment.armor.CardboardArmorStealthOverlay;
import com.mohistmc.mod.module.create.client.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.mohistmc.mod.module.create.client.content.equipment.clipboard.ClipboardValueSettingsClientHandler;
import com.mohistmc.mod.module.create.client.content.equipment.extendoGrip.ExtendoGripRenderHandler;
import com.mohistmc.mod.module.create.client.content.equipment.symmetryWand.SymmetryHandlerClient;
import com.mohistmc.mod.module.create.client.content.equipment.toolbox.ToolboxHandlerClient;
import com.mohistmc.mod.module.create.client.content.equipment.zapper.terrainzapper.WorldshaperRenderHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.KineticDebugger;
import com.mohistmc.mod.module.create.client.content.kinetics.belt.item.BeltConnectorHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.chainConveyor.ChainConveyorRidingHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.chainConveyor.ChainPackageInteractionHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.fan.AirCurrentClient;
import com.mohistmc.mod.module.create.client.content.kinetics.mechanicalArm.ArmInteractionPointHandler;
import com.mohistmc.mod.module.create.client.content.kinetics.turntable.TurntableHandler;
import com.mohistmc.mod.module.create.client.content.logistics.depot.EjectorTargetHandler;
import com.mohistmc.mod.module.create.client.content.logistics.factoryBoard.FactoryPanelConnectionHandler;
import com.mohistmc.mod.module.create.client.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import com.mohistmc.mod.module.create.client.content.logistics.packagerLink.LogisticallyLinkedClientHandler;
import com.mohistmc.mod.module.create.client.content.logistics.tableCloth.TableClothOverlayRenderer;
import com.mohistmc.mod.module.create.client.content.redstone.displayLink.ClickToLinkHandler;
import com.mohistmc.mod.module.create.client.content.redstone.link.LinkRenderer;
import com.mohistmc.mod.module.create.client.content.redstone.link.controller.LinkedControllerClientHandler;
import com.mohistmc.mod.module.create.client.content.trains.CameraDistanceModifier;
import com.mohistmc.mod.module.create.client.content.trains.GlobalRailwayManagerClient;
import com.mohistmc.mod.module.create.client.content.trains.TrainHUD;
import com.mohistmc.mod.module.create.client.content.trains.entity.TrainRelocatorClient;
import com.mohistmc.mod.module.create.client.content.trains.schedule.hat.TrainHatInfoReloadListener;
import com.mohistmc.mod.module.create.client.content.trains.track.CurvedTrackInteraction;
import com.mohistmc.mod.module.create.client.content.trains.track.TrackBlockOutline;
import com.mohistmc.mod.module.create.client.content.trains.track.TrackPlacementClient;
import com.mohistmc.mod.module.create.client.content.trains.track.TrackTargetingClient;
import com.mohistmc.mod.module.create.client.flywheel.backend.compile.FlwProgramsReloader;
import com.mohistmc.mod.module.create.client.flywheel.impl.BackendManagerImpl;
import com.mohistmc.mod.module.create.client.flywheel.impl.FlwImpl;
import com.mohistmc.mod.module.create.client.flywheel.impl.visualization.VisualizationEventHandler;
import com.mohistmc.mod.module.create.client.flywheel.lib.util.LevelAttached;
import com.mohistmc.mod.module.create.client.flywheel.lib.util.RendererReloadCache;
import com.mohistmc.mod.module.create.client.foundation.block.BigOutlines;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionRenderer;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.scrollValue.ScrollValueRenderer;
import com.mohistmc.mod.module.create.client.foundation.sound.SoundScapes;
import com.mohistmc.mod.module.create.client.foundation.utility.CameraAngleAnimationService;
import com.mohistmc.mod.module.create.client.foundation.utility.ServerSpeedProvider;
import com.mohistmc.mod.module.create.client.model.obj.ObjLoader;
import com.mohistmc.mod.module.create.client.ponder.Ponder;
import com.mohistmc.mod.module.create.client.ponder.foundation.PonderIndex;
import com.mohistmc.mod.module.create.client.ponder.foundation.PonderTooltipHandler;
import com.mohistmc.mod.module.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.mohistmc.mod.module.create.content.equipment.zapper.ZapperItem;
import com.mohistmc.mod.module.create.content.kinetics.drill.CobbleGenOptimisation;
import com.mohistmc.mod.module.create.content.redstone.link.controller.LinkedControllerItem;
import com.mohistmc.mod.module.create.foundation.utility.TickBasedCache;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameLoadCookie;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    protected abstract void startUseItem();

    @Shadow
    private int rightClickDelay;

    @Inject(method = "pick(F)V", at = @At("TAIL"))
    private void bigShapePick(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        BigOutlines.pick(mc);
        TrackBlockOutline.pickCurves(mc);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;createReload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
    private void flywheel$onBeginInitialResourceReload(GameConfig gameConfig, CallbackInfo ci) {
        FlwImpl.freezeRegistries();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateRawMouseInput(Z)V"))
    private void register(GameConfig gameConfig, CallbackInfo ci) {
        if (RenderSystem.getDevice().getDeviceInfo().backendName().equals("OpenGL")) {
            resourceManager.registerReloadListener(FlwProgramsReloader.INSTANCE);
        }
        resourceManager.registerReloadListener(ObjLoader.INSTANCE);
        resourceManager.registerReloadListener(Create.RESOURCE_RELOAD_LISTENER);
        resourceManager.registerReloadListener(TrainHatInfoReloadListener.LISTENER);
        resourceManager.registerReloadListener(Ponder.RESOURCE_RELOAD_LISTENER);
    }

    @Inject(method = "onResourceLoadFinished(Lnet/minecraft/client/GameLoadCookie;)V", at = @At("HEAD"))
    private void endReload(GameLoadCookie cookie, CallbackInfo ci) {
        BackendManagerImpl.onEndClientResourceReload();
        RendererReloadCache.onReloadLevelRenderer();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tickPre(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        AnimationTickHolder.tick(mc);
        PonderTooltipHandler.tick();
        if (level == null || player == null) {
            return;
        }
        PlacementClient.tick(mc);
        GhostBlocks.getInstance().tickGhosts();
        Outliner.getInstance().tickOutlines();
        LinkedControllerClientHandler.tick(mc);
        ControlsHandler.tick(mc);
        AirCurrentClient.tickClientPlayerSounds();
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickPost(CallbackInfo ci) {
        if (level == null || player == null) {
            return;
        }
        Minecraft mc = (Minecraft) (Object) this;
        SoundScapes.tick();
        Create.SCHEMATIC_SENDER.tick(mc);
        Create.SCHEMATIC_AND_QUILL_HANDLER.tick(mc);
        Create.GLUE_HANDLER.tick(mc);
        Create.SCHEMATIC_HANDLER.tick(mc);
        Create.ZAPPER_RENDER_HANDLER.tick();
        Create.POTATO_CANNON_RENDER_HANDLER.tick();
        Create.SOUL_PULSE_EFFECT_HANDLER.tick(level);
        GlobalRailwayManagerClient.tick(mc);
        ContraptionHandlerClient.tick(level);
        CapabilityMinecartController.tick(level);
        ServerSpeedProvider.clientTick(mc);
        BeltConnectorHandler.tick(mc);
        FilteringRenderer.tick(mc);
        LinkRenderer.tick(mc);
        ScrollValueRenderer.tick(mc);
        ChassisRangeDisplay.tick(mc);
        EdgeInteractionRenderer.tick(mc);
        GirderWrenchBehaviorHandler.tick(mc);
        WorldshaperRenderHandler.tick(mc);
        CouplingHandlerClient.tick(mc);
        CouplingRenderer.tickDebugModeRenders(mc);
        KineticDebugger.tick(mc);
        ExtendoGripRenderHandler.tick(mc);
        ArmInteractionPointHandler.tick(mc);
        EjectorTargetHandler.tick(mc);
        BlueprintOverlayRenderer.tick(mc);
        ToolboxHandlerClient.clientTick();
        RadialWrenchHandler.clientTick();
        TrackTargetingClient.clientTick(mc);
        TrackPlacementClient.clientTick(mc);
        TrainRelocatorClient.clientTick(mc);
        ClickToLinkHandler.clientTick(mc);
        CurvedTrackInteraction.clientTick(mc);
        CameraDistanceModifier.tick();
        CameraAngleAnimationService.tick(mc);
        TrainHUD.tick(mc);
        ClipboardValueSettingsClientHandler.clientTick(mc);
        Create.VALUE_SETTINGS_HANDLER.tick(mc);
        ScrollValueHandler.tick(mc);
        ContraptionPlayerPassengerRotation.tick();
        ChainConveyorInteractionHandler.clientTick(mc);
        ChainConveyorRidingHandler.clientTick(mc);
        ChainConveyorConnectionHandler.clientTick(mc);
        PackagePortTargetSelectionHandler.tick(mc);
        LogisticallyLinkedClientHandler.tick(mc);
        TableClothOverlayRenderer.tick(mc);
        CardboardArmorStealthOverlay.clientTick(mc);
        FactoryPanelConnectionHandler.clientTick(mc);
        TickBasedCache.clientTick();
        SymmetryHandlerClient.onClientTick(mc);
    }

    @Inject(method = "renderFrame(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void render(boolean advanceGameTime, CallbackInfo ci) {
        TurntableHandler.gameRenderFrame((Minecraft) (Object) this);
    }

    @Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;tick(Ljava/util/function/BooleanSupplier;)V", shift = At.Shift.AFTER))
    private void tick(CallbackInfo ci) {
        VisualizationEventHandler.onClientTick((Minecraft) (Object) this, level);
    }

    @Inject(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void doItemUse(CallbackInfo ci, @Local InteractionHand hand) {
        Minecraft mc = (Minecraft) (Object) this;
        if (hand == InteractionHand.MAIN_HAND && (CurvedTrackInteraction.onClickInput(
            mc,
            false
        ) || Create.GLUE_HANDLER.onMouseInput(
            mc,
            false
        ) || FactoryPanelConnectionHandler.onRightClick(mc) || ChainConveyorConnectionHandler.onRightClick(mc) || TrainRelocatorClient.onClicked(
            mc) || ChainConveyorInteractionHandler.onUse(mc) || PackagePortTargetSelectionHandler.onUse(mc) || ChainPackageInteractionHandler.onUse(
            mc))) {
            player.swing(InteractionHand.MAIN_HAND);
            ci.cancel();
        } else if (ContraptionHandlerClient.rightClickingOnContraptionsGetsHandledLocally(mc, hand)) {
            ci.cancel();
        }
        if (hand == InteractionHand.MAIN_HAND) {
            LinkedControllerClientHandler.deactivateInLectern(mc, player);
        }
    }

    @WrapOperation(method = "continueAttack(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean handleBlockBreaking(
        MultiPlayerGameMode instance,
        BlockPos pos,
        Direction direction,
        Operation<Boolean> original
    ) {
        Minecraft mc = (Minecraft) (Object) this;
        return CurvedTrackInteraction.onClickInput(mc, true) || Create.GLUE_HANDLER.onMouseInput(
            mc,
            true
        ) || original.call(instance, pos, direction);
    }

    @Inject(method = "startAttack()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
    private void doAttack(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = (Minecraft) (Object) this;
        if (CurvedTrackInteraction.onClickInput(mc, true) || Create.GLUE_HANDLER.onMouseInput(mc, true)) {
            player.swing(InteractionHand.MAIN_HAND);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startAttack()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
    private void missingAttack(CallbackInfoReturnable<Boolean> cir, @Local ItemStack heldItem) {
        if (heldItem.getItem() instanceof ZapperItem) {
            player.connection.send(AllPackets.LEFT_CLICK);
        }
    }

    @Inject(method = "setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V", at = @At("HEAD"))
    private void onJoinWorld(CallbackInfo ci) {
        if (level != null) {
            onUnloadWorld(null);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GameNarrator;clear()V"))
    private void onLeave(Screen screen, boolean keepResourcePacks, boolean stopSound, CallbackInfo ci) {
        Create.RAILWAYS.cleanUp();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;onDisconnected()V"))
    private void onUnloadWorld(CallbackInfo ci) {
        LevelAttached.invalidateLevel(level);
        Create.invalidateRenderers();
        Create.SOUL_PULSE_EFFECT_HANDLER.refresh();
        AnimationTickHolder.reset();
        ControlsHandler.levelUnloaded();
        WorldAttached.invalidateWorld(level);
        CobbleGenOptimisation.invalidateWorld(level);
        Ponder.invalidateRenderers();
    }

    @Inject(method = "pickBlockOrEntity()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;hasControlDown()Z"), cancellable = true)
    private void doItemPick(CallbackInfo ci) {
        if (ToolboxHandlerClient.onPickItem((Minecraft) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "run()V", at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;getRuntime()Ljava/lang/Runtime;"))
    private void run(CallbackInfo ci) {
        PonderIndex.registerAll();
    }

    @WrapOperation(method = "handleKeybinds()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z", ordinal = 2))
    private boolean onUse(KeyMapping instance, Operation<Boolean> original) {
        if (player.getActiveItem().getItem() instanceof LinkedControllerItem) {
            if (rightClickDelay == 0 && original.call(instance)) {
                startUseItem();
            }
            return true;
        }
        return original.call(instance);
    }
}
