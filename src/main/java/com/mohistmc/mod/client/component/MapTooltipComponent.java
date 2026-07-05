package com.mohistmc.mod.client.component;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mgazul
 * @date 2026/4/16 03:08
 */
public class MapTooltipComponent implements ClientTooltipComponent, TooltipComponent
{
    private final Integer id;
    private static final Identifier MAP_SPRITE = Identifier.withDefaultNamespace("textures/map/map_background.png");

    public MapTooltipComponent(ItemStack item) {
        this.id = item.get(DataComponents.MAP_ID).id();
    }

    public void extractImage(@NotNull Font font, int x, int y, int w, int h, @NotNull GuiGraphicsExtractor graphics) {
        render(this.id, x, y, graphics);
    }

    public int getHeight(@NotNull Font font) {
        return 66;
    }

    public int getWidth(@NotNull Font font) {
        return 66;
    }

    public static void render(Integer id, int x, int y, GuiGraphicsExtractor graphics) {
        if (id == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) {
            return;
        }
        MapItemSavedData data = level.getMapData(new MapId((int)id));
        if (data == null) {
            return;
        }
        int offsetX = 0;
        int offsetY = -2;
        renderBackground(x + offsetX, y + offsetY, graphics);
        renderMap(id, data, x + offsetX + 4, y + offsetY + 4, graphics);
    }

    public static void renderBackground(int x, int y, GuiGraphicsExtractor graphics) {
        graphics.pose().pushMatrix();
        graphics.blit(RenderPipelines.GUI_TEXTURED, MAP_SPRITE, x, y, 0.0f, 0.0f, 66, 66, 66, 66);
        graphics.pose().popMatrix();
    }

    public static void renderMap(Integer id, MapItemSavedData data, int x, int y, GuiGraphicsExtractor graphics) {
        Minecraft mc = Minecraft.getInstance();
        graphics.pose().pushMatrix();
        graphics.pose().translate((float)x, (float)y);
        graphics.pose().scale(0.45f, 0.45f);
        MapRenderState mapRenderState = new MapRenderState();
        mc.getMapRenderer().extractRenderState(new MapId((int)id), data, mapRenderState);
        graphics.map(mapRenderState);
        graphics.pose().popMatrix();
    }
}
