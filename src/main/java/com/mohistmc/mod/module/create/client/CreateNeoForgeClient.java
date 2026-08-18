package com.mohistmc.mod.module.create.client;

import com.zurrtum.create.client.catnip.gui.render.BlockTransformElementRenderer;
import com.zurrtum.create.client.catnip.gui.render.BlockTransformRenderState;
import com.zurrtum.create.client.catnip.gui.render.EntityBlockRenderState;
import com.zurrtum.create.client.catnip.gui.render.EntityBlockRenderer;
import com.zurrtum.create.client.catnip.gui.render.ItemTransformElementRenderer;
import com.zurrtum.create.client.catnip.gui.render.ItemTransformRenderState;
import com.zurrtum.create.client.catnip.gui.render.PartialElementRenderer;
import com.zurrtum.create.client.catnip.gui.render.PartialRenderState;
import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderState;
import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderer;
import com.zurrtum.create.client.foundation.gui.render.BlazeBurnerElementRenderer;
import com.zurrtum.create.client.foundation.gui.render.BlazeBurnerRenderState;
import com.zurrtum.create.client.foundation.gui.render.CrafterRenderState;
import com.zurrtum.create.client.foundation.gui.render.CrafterRenderer;
import com.zurrtum.create.client.foundation.gui.render.CrushWheelRenderState;
import com.zurrtum.create.client.foundation.gui.render.CrushWheelRenderer;
import com.zurrtum.create.client.foundation.gui.render.DeployerRenderState;
import com.zurrtum.create.client.foundation.gui.render.DeployerRenderer;
import com.zurrtum.create.client.foundation.gui.render.DrainRenderState;
import com.zurrtum.create.client.foundation.gui.render.DrainRenderer;
import com.zurrtum.create.client.foundation.gui.render.FanRenderState;
import com.zurrtum.create.client.foundation.gui.render.FanRenderer;
import com.zurrtum.create.client.foundation.gui.render.ManualBlockRenderState;
import com.zurrtum.create.client.foundation.gui.render.ManualBlockRenderer;
import com.zurrtum.create.client.foundation.gui.render.MillstoneRenderState;
import com.zurrtum.create.client.foundation.gui.render.MillstoneRenderer;
import com.zurrtum.create.client.foundation.gui.render.MixingBasinRenderState;
import com.zurrtum.create.client.foundation.gui.render.MixingBasinRenderer;
import com.zurrtum.create.client.foundation.gui.render.PressBasinRenderState;
import com.zurrtum.create.client.foundation.gui.render.PressBasinRenderer;
import com.zurrtum.create.client.foundation.gui.render.PressRenderState;
import com.zurrtum.create.client.foundation.gui.render.PressRenderer;
import com.zurrtum.create.client.foundation.gui.render.SandPaperRenderState;
import com.zurrtum.create.client.foundation.gui.render.SandPaperRenderer;
import com.zurrtum.create.client.foundation.gui.render.SawRenderState;
import com.zurrtum.create.client.foundation.gui.render.SawRenderer;
import com.zurrtum.create.client.foundation.gui.render.SpoutRenderState;
import com.zurrtum.create.client.foundation.gui.render.SpoutRenderer;
import com.zurrtum.create.client.ponder.enums.PonderKeybinds;
import com.zurrtum.create.client.ponder.foundation.render.SceneRenderState;
import com.zurrtum.create.client.ponder.foundation.render.SceneRenderer;
import com.zurrtum.create.client.ponder.foundation.render.TitleTextRenderState;
import com.zurrtum.create.client.ponder.foundation.render.TitleTextRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@Mod(value = com.zurrtum.create.Create.MOD_ID, dist = Dist.CLIENT)
public final class CreateNeoForgeClient {
    public CreateNeoForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerKeyMappings);
        modEventBus.addListener(this::registerPictureInPictureRenderers);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> new Create().onInitializeClient());
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        AllKeys.ALL.forEach(event::register);
        event.register(PonderKeybinds.PONDER);
    }

    private void registerPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event) {
        event.register(ItemTransformRenderState.class, ItemTransformElementRenderer::new);
        event.register(BlockTransformRenderState.class, BlockTransformElementRenderer::new);
        event.register(EntityBlockRenderState.class, EntityBlockRenderer::new);
        event.register(PartialRenderState.class, PartialElementRenderer::new);
        event.register(BlazeBurnerRenderState.class, BlazeBurnerElementRenderer::new);
        event.register(PressBasinRenderState.class, PressBasinRenderer::new);
        event.register(PressRenderState.class, PressRenderer::new);
        event.register(MixingBasinRenderState.class, MixingBasinRenderer::new);
        event.register(BasinBlazeBurnerRenderState.class, BasinBlazeBurnerRenderer::new);
        event.register(MillstoneRenderState.class, MillstoneRenderer::new);
        event.register(SawRenderState.class, SawRenderer::new);
        event.register(CrushWheelRenderState.class, CrushWheelRenderer::new);
        event.register(DeployerRenderState.class, DeployerRenderer::new);
        event.register(ManualBlockRenderState.class, ManualBlockRenderer::new);
        event.register(SpoutRenderState.class, SpoutRenderer::new);
        event.register(CrafterRenderState.class, CrafterRenderer::new);
        event.register(DrainRenderState.class, DrainRenderer::new);
        event.register(SandPaperRenderState.class, SandPaperRenderer::new);
        event.register(TitleTextRenderState.class, TitleTextRenderer::new);
        event.register(SceneRenderState.class, SceneRenderer::new);
        event.register(FanRenderState.class, FanRenderer::new);
    }
}
