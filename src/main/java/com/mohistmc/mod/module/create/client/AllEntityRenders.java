package com.mohistmc.mod.module.create.client;

import com.mohistmc.mod.module.create.AllEntityTypes;
import com.mohistmc.mod.module.create.client.content.contraptions.actors.seat.SeatRenderer;
import com.mohistmc.mod.module.create.client.content.contraptions.glue.SuperGlueRenderer;
import com.mohistmc.mod.module.create.client.content.contraptions.render.*;
import com.mohistmc.mod.module.create.client.content.equipment.blueprint.BlueprintRenderer;
import com.mohistmc.mod.module.create.client.content.equipment.potatoCannon.PotatoProjectileRenderer;
import com.mohistmc.mod.module.create.client.content.logistics.box.PackageRenderer;
import com.mohistmc.mod.module.create.client.content.logistics.box.PackageVisual;
import com.mohistmc.mod.module.create.client.content.logistics.depot.EjectorItemEntityRenderer;
import com.mohistmc.mod.module.create.client.content.trains.entity.CarriageContraptionEntityRenderer;
import com.mohistmc.mod.module.create.client.content.trains.entity.CarriageContraptionVisual;
import com.mohistmc.mod.module.flywheel.lib.visualization.SimpleEntityVisualizer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class AllEntityRenders {
    private static <T extends Entity, P extends T> void visual(
        EntityType<P> type,
        EntityRendererProvider<T> rendererFactory,
        SimpleEntityVisualizer.Factory<P> visualizerFactory
    ) {
        EntityRenderers.register(type, rendererFactory);
        SimpleEntityVisualizer.builder(type).factory(visualizerFactory).skipVanillaRender(blockEntity -> false).apply();
    }

    public static <T extends Entity> void render(EntityType<? extends T> type, EntityRendererProvider<T> factory) {
        EntityRenderers.register(type, factory);
    }

    public static void register() {
        render(AllEntityTypes.EJECTOR_ITEM, EjectorItemEntityRenderer::new);
        visual(
            AllEntityTypes.ORIENTED_CONTRAPTION,
            OrientedContraptionEntityRenderer::new,
            OrientedContraptionVisual::new
        );
        visual(
            AllEntityTypes.CONTROLLED_CONTRAPTION,
            ControlledContraptionEntityRenderer::new,
            ControlledContraptionVisual::new
        );
        visual(
            AllEntityTypes.CARRIAGE_CONTRAPTION,
            CarriageContraptionEntityRenderer::new,
            CarriageContraptionVisual::new
        );
        render(AllEntityTypes.SUPER_GLUE, SuperGlueRenderer::new);
        visual(AllEntityTypes.GANTRY_CONTRAPTION, ContraptionEntityRenderer::new, ContraptionVisual::new);
        render(AllEntityTypes.SEAT, SeatRenderer::new);
        render(AllEntityTypes.POTATO_PROJECTILE, PotatoProjectileRenderer::new);
        visual(AllEntityTypes.PACKAGE, PackageRenderer::new, PackageVisual::new);
        render(AllEntityTypes.CRAFTING_BLUEPRINT, BlueprintRenderer::new);
    }
}
