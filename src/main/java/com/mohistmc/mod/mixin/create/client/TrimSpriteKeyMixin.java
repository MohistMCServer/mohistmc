package com.mohistmc.mod.mixin.create.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mohistmc.mod.module.create.content.equipment.armor.AllEquipmentAssetKeys;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

@Mixin(targets = "net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer$TrimSpriteKey")
public class TrimSpriteKeyMixin {
    @Final
    @Shadow
    private ResourceKey<EquipmentAsset> equipmentAssetId;

    @ModifyReturnValue(method = "spriteId()Lnet/minecraft/resources/Identifier;", at = @At("RETURN"))
    private Identifier getTexture(Identifier id) {
        if (equipmentAssetId == AllEquipmentAssetKeys.CARDBOARD && id.getNamespace().equals("minecraft")) {
            return Identifier.fromNamespaceAndPath(MOD_ID, id.getPath());
        }
        return id;
    }
}
