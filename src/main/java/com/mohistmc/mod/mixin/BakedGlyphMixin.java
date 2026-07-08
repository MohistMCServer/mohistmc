package com.mohistmc.mod.mixin;

import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({BakedSheetGlyph.class})
public class BakedGlyphMixin {
    @ModifyArg(method = {"renderChar"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedSheetGlyph;render(ZFFFLorg/joml/Matrix4fc;Lcom/mojang/blaze3d/vertex/VertexConsumer;IZI)V"), index = 1, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedSheetGlyph;render(ZFFFLorg/joml/Matrix4fc;Lcom/mojang/blaze3d/vertex/VertexConsumer;IZI)V", ordinal = 0), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedSheetGlyph;render(ZFFFLorg/joml/Matrix4fc;Lcom/mojang/blaze3d/vertex/VertexConsumer;IZI)V", ordinal = 1)))
    private float alternateTextShadowGlyph(final float shadowOffset) {
        return (shadowOffset - 1.0f);
    }
}

