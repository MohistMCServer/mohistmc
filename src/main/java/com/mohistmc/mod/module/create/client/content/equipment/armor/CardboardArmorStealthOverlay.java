package com.mohistmc.mod.module.create.client.content.equipment.armor;

import com.mohistmc.mod.module.create.catnip.animation.LerpedFloat;
import com.mohistmc.mod.module.create.catnip.animation.LerpedFloat.Chaser;
import com.mohistmc.mod.module.create.content.equipment.armor.CardboardArmorHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class CardboardArmorStealthOverlay {
    private static final LerpedFloat opacity = LerpedFloat.linear().startWithValue(0).chase(0, 0.25f, Chaser.EXP);

    public static void clientTick(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        opacity.tickChaser();
        opacity.updateChaseTarget(CardboardArmorHandler.testForStealth(player) ? 1 : 0);
    }

    public static float getOverlayOpacity(DeltaTracker tickCounter) {
        return opacity.getValue(tickCounter.getGameTimeDeltaPartialTick(true));
    }
}
