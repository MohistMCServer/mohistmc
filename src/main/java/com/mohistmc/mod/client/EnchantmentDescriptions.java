package com.mohistmc.mod.client;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

/**
 * @author Mgazul
 * @date 2026/4/15 22:38
 */
public class EnchantmentDescriptions {

    public static String[] KEY_TYPES = new String[] { "desc", "description", "info" };

    public static ItemStack getHoveredStack() {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen instanceof AbstractContainerScreen<?> accessor) {
            Slot slot = accessor.getHoveredSlot();
            if (slot != null && slot.hasItem()) {
                return slot.getItem();
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean canDisplayDescription() {
        return hasEnchantments(getHoveredStack());
    }

    public static boolean hasEnchantments(ItemStack stack) {
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        return (enchantments != null && !enchantments.isEmpty()) || (stored != null && !stored.isEmpty());
    }

    public static Component getKeybindText() {
        return Component.literal("按住Shift查看魔咒描述。");
    }

    public static boolean isKeybindConditionMet() {
        return Minecraft.getInstance().hasShiftDown();
    }

    public static void insertDescriptions(Holder<Enchantment> enchantment, int level, Consumer<Component> lines) {
        if (canDisplayDescription()) {
            MutableComponent description = getDescription(enchantment, enchantment.unwrapKey().orElseThrow().identifier(), level);
            if (description != null) {
                description = ComponentUtils.mergeStyles(description, Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
                lines.accept(Component.empty().copy().append(description).append(Component.empty()));
            }
        }
    }

    @Nullable
    private static MutableComponent getDescription(Holder<Enchantment> enchantment, Identifier id, int level) {
        MutableComponent description = getDescription("enchantment." + id.getNamespace() + "." + id.getPath(), level);
        if (description == null) {
            ComponentContents contents = enchantment.value().description().getContents();
            if (contents instanceof TranslatableContents translatable) {
                description = getDescription(translatable.getKey(), level);
            }
        }
        return description;
    }

    @Nullable
    private static MutableComponent getDescription(String baseKey, int level) {
        String[] key_TYPES = EnchantmentDescriptions.KEY_TYPES;
        for (int length = key_TYPES.length, i = 0; i < length; ++i) {
            String keyType = key_TYPES[i];
            String descriptionKey = baseKey + "." + keyType;
            Optional<MutableComponent> optionalDescription = Optional.of(Component.translatable(descriptionKey))
                    .filter(ComponentUtils::isTranslationResolvable);
            if (optionalDescription.isPresent()) {
                return Component.translatable(descriptionKey);
            }
            descriptionKey = descriptionKey + "." + level;
            optionalDescription = Optional.of(Component.translatable(descriptionKey))
                    .filter(ComponentUtils::isTranslationResolvable);
            if (optionalDescription.isPresent()) {
                return Component.translatable(descriptionKey);
            }
        }
        return null;
    }
}
