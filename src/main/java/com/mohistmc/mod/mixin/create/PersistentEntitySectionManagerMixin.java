package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.content.contraptions.ContraptionHandler;
import com.mohistmc.mod.module.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.mohistmc.mod.module.create.content.logistics.depot.EjectorItemEntity;
import com.mohistmc.mod.module.create.foundation.item.EntityItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {
    @ModifyVariable(method = "addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private EntityAccess addEntity(EntityAccess entity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (entity instanceof EjectorItemEntity) {
                return entity;
            }
            ItemStack stack = itemEntity.getItem();
            if (stack.getItem() instanceof EntityItem item) {
                Entity newEntity = item.createEntity(itemEntity.level(), itemEntity, stack);
                if (newEntity != null) {
                    itemEntity.discard();
                    return newEntity;
                }
            }
        }
        return entity;
    }

    @Inject(method = "addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z", at = @At("HEAD"))
    private void addEntity(EntityAccess entity, boolean loaded, CallbackInfoReturnable<Boolean> cir) {
        ContraptionHandler.addSpawnedContraptionsToCollisionList(entity);
        if (!loaded) {
            CapabilityMinecartController.attach(entity);
        }
    }
}
