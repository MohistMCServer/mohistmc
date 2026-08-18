package com.mohistmc.mod.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mohistmc.mod.module.create.infrastructure.items.EnchantmentExtend;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<Enchantment> wrap(
        Function<RecordCodecBuilder.Instance<Enchantment>, ? extends App<RecordCodecBuilder.Mu<Enchantment>, Enchantment>> builder,
        Operation<Codec<Enchantment>> original
    ) {
        return new EnchantmentExtend(original.call(builder));
    }
}
