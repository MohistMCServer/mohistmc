package com.mohistmc.mod.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/4/15 23:57
 */
@Mixin(Component.class)
public interface MixinComponent {

    @Inject(at = @At("HEAD"),  method = "translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", cancellable = true)
    private static void levelFix(String key, CallbackInfoReturnable<MutableComponent> cir) {
        if (key != null && key.startsWith("enchantment.level.")) {
            try {
                String levelStr = key.substring("enchantment.level.".length());
                int level = Integer.parseInt(levelStr);
                if (level > 10) {
                    cir.setReturnValue(Component.literal(mohistmc$toRoman(level)));
                }
            } catch (NumberFormatException e) {
                // 如果解析失败，使用默认行为
            }
        }
    }

    @Unique
    private static String mohistmc$toRoman(int number) {
        if (number <= 0 || number > 3999) {
            return String.valueOf(number);
        }

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanNumerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                roman.append(romanNumerals[i]);
                number -= values[i];
            }
        }

        return roman.toString();
    }
}
