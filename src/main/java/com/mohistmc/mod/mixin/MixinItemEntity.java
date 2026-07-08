package com.mohistmc.mod.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinItemEntity {

    @Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
    private void onShouldShowName(Entity entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ItemEntity) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getNameTag", at = @At("HEAD"), cancellable = true)
    private void onGetNameTag(Entity entity, CallbackInfoReturnable<Component> cir) {
        if (entity instanceof ItemEntity item && !item.getItem().isEmpty()) {
            var stack = item.getItem();
            int count = stack.getCount();
            var name = stack.getHoverName();
            cir.setReturnValue(count > 1
                    ? name.copy().append(Component.literal(" × " + count))
                    : name);
        }
    }
}
