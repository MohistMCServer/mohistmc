package com.mohistmc.mod.module.curios.api.common;

import com.mohistmc.mod.module.curios.api.CurioAttributeModifiers;
import com.mohistmc.mod.module.curios.api.event.CurioAttributeModifierEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Hooks for firing events and other logic in Curios that occurs both client-side and server-side.
 */
public class CuriosCommonHooks {

    /**
     * Fires the event for {@link CurioAttributeModifierEvent} to modify curio attribute modifiers on
     * an ItemStack.
     *
     * @param stack            The ItemStack being calculated for curio attribute modifiers.
     * @param defaultModifiers The default modifiers found on the stack.
     * @return The result of the modifiers after the event has been posted.
     */
    public static CurioAttributeModifiers computeModifiedAttributes(
            ItemStack stack, CurioAttributeModifiers defaultModifiers) {
        CurioAttributeModifierEvent event = new CurioAttributeModifierEvent(stack, defaultModifiers);
        NeoForge.EVENT_BUS.post(event);
        return event.build();
    }
}
