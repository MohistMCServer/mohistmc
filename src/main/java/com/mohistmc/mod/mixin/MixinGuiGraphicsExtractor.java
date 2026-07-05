package com.mohistmc.mod.mixin;

import com.mohistmc.mod.client.renderer.ItemBorderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/4/16 00:42
 */
@Mixin(GuiGraphicsExtractor.class)
public abstract class MixinGuiGraphicsExtractor {

    // @formatter:off
    @Shadow @Final private Minecraft minecraft;
    @Shadow public abstract Matrix3x2fStack pose();
    @Shadow public abstract void map(MapRenderState mapRenderState);
    // @formatter:on

    @Unique
    private final MapRenderState mohistmc$mapRenderState = new MapRenderState();


    @Inject(method = "fakeItem(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void renderFakeItemWithRarityBorder(ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (!itemStack.isEmpty()) {
            GuiGraphicsExtractor guiGraphics = (GuiGraphicsExtractor)(Object)this;
            ItemBorderRenderer.renderRarityBorder(guiGraphics, itemStack, x, y);
        }
    }

    // 背包内地图渲染
    @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "HEAD"))
    private void drawMap(Font font, ItemStack itemStack, int x, int y, String countText, CallbackInfo ci) {
        if (!itemStack.is(Items.FILLED_MAP)) return;

        var mapId = itemStack.get(DataComponents.MAP_ID);
        MapItemSavedData savedData = null;
        if (this.minecraft.level != null) {
            savedData = MapItem.getSavedData(mapId, this.minecraft.level);
        }

        if (savedData == null) return;

        this.pose().pushMatrix();
        this.pose().translate(x, y);
        this.pose().scale(0.125F, 0.125F);

        this.minecraft.getMapRenderer().extractRenderState(mapId, savedData, this.mohistmc$mapRenderState);
        this.map(this.mohistmc$mapRenderState);

        this.pose().popMatrix();
    }
}
