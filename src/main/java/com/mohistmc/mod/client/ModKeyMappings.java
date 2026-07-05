package com.mohistmc.mod.client;

import com.mohistmc.mod.MohistMC;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = MohistMC.MODID)
public class ModKeyMappings {

    public static KeyMapping GUN_FIRE_KEY;
    public static KeyMapping RELOAD_KEY;
    public static KeyMapping GUN_MODIFY_KEY;
    public static KeyMapping V_KEY;
    public static KeyMapping OPEN_TEAM_GUI;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        GUN_FIRE_KEY = new KeyMapping(
                "key.mohistmc.gun_fire",
                GLFW.GLFW_KEY_LEFT_CONTROL,
                KeyMapping.Category.MISC
        );

        RELOAD_KEY = new KeyMapping(
                "key.mohistmc.reload",
                GLFW.GLFW_KEY_R,
                KeyMapping.Category.MISC
        );

        GUN_MODIFY_KEY = new KeyMapping(
                "key.mohistmc.gun_modify",
                GLFW.GLFW_KEY_Z,
                KeyMapping.Category.MISC
        );

        V_KEY = new KeyMapping(
                "key.mohistmc.v",
                GLFW.GLFW_KEY_Z,
                KeyMapping.Category.MISC
        );

        OPEN_TEAM_GUI = new KeyMapping(
                "key.mohistmc.open_gui",
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.MISC
        );


        event.register(GUN_FIRE_KEY);
        event.register(RELOAD_KEY);
        event.register(GUN_MODIFY_KEY);
        event.register(V_KEY);
        event.register(OPEN_TEAM_GUI);
    }

    public static boolean isGunFireKeyDown() {
        return GUN_FIRE_KEY != null && GUN_FIRE_KEY.isDown();
    }

    public static boolean isReloadKeyDown() {
        return RELOAD_KEY != null && RELOAD_KEY.isDown();
    }

    public static boolean isGunModifyKeyDown() {
        return GUN_MODIFY_KEY != null && GUN_MODIFY_KEY.isDown();
    }

    public static boolean isVKeyDown() {
        return V_KEY != null && V_KEY.isDown();
    }

    public static boolean isOpenTeamGuiKeyDown() {
        return OPEN_TEAM_GUI != null && OPEN_TEAM_GUI.isDown();
    }
}
