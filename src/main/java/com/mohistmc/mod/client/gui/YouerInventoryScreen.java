package com.mohistmc.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author Mgazul
 */
@OnlyIn(Dist.CLIENT)
public class YouerInventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {

    // ==================== 常量 ====================

    private static final int TITLE_COLOR = -12566464;
    private static final int TITLE_LABEL_X = 97;

    private static final int RECIPE_BUTTON_X = 104;
    private static final int RECIPE_BUTTON_Y = -22;

    private static final int ENTITY_BOX_X0 = 26;
    private static final int ENTITY_BOX_Y0 = 8;
    private static final int ENTITY_BOX_X1 = 75;
    private static final int ENTITY_BOX_Y1 = 78;
    private static final int ENTITY_SIZE = 30;
    private static final float ENTITY_OFFSET_Y = 0.0625F;
    private static final float ENTITY_ANGLE_DIVISOR = 40.0F;
    private static final float ENTITY_ANGLE_MULTIPLIER = 20.0F;

    // ==================== 字段 ====================

    private float mouseX;
    private float mouseY;
    private boolean recipeButtonClicked;
    private final EffectsInInventory effects;

    // ==================== 构造 ====================

    public YouerInventoryScreen(Player player) {
        super(player.inventoryMenu, new CraftingRecipeBookComponent(player.inventoryMenu), player.getInventory(), Component.translatable("container.crafting"));
        this.titleLabelX = TITLE_LABEL_X;
        this.effects = new EffectsInInventory(this);
    }

    // ==================== 生命周期 ====================

    @Override
    protected void init() {
        if (this.minecraft.player.hasInfiniteMaterials()) {
            this.openCreativeScreen();
        } else {
            super.init();
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.minecraft.player.hasInfiniteMaterials()) {
            this.openCreativeScreen();
        }
    }

    // ==================== 渲染 ====================

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.effects.extractRenderState(graphics, mouseX, mouseY);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, BACKGROUND_TEXTURE_WIDTH, BACKGROUND_TEXTURE_HEIGHT);
        renderEntityInInventoryFollowsMouse(graphics,
                xo + ENTITY_BOX_X0, yo + ENTITY_BOX_Y0,
                xo + ENTITY_BOX_X1, yo + ENTITY_BOX_Y1,
                ENTITY_SIZE, ENTITY_OFFSET_Y,
                this.mouseX, this.mouseY,
                this.minecraft.player);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, TITLE_COLOR, false);
    }

    // ==================== 配方书 ====================

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + RECIPE_BUTTON_X, this.height / 2 + RECIPE_BUTTON_Y);
    }

    @Override
    protected void onRecipeBookButtonClick() {
        this.recipeButtonClicked = true;
    }

    @Override
    protected boolean isBiggerResultSlot() {
        return false;
    }

    // ==================== 输入 ====================

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.recipeButtonClicked) {
            this.recipeButtonClicked = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    // ==================== 状态 ====================

    @Override
    public boolean showsActiveEffects() {
        return this.effects.canSeeEffects();
    }

    // ==================== 私有 ====================

    private void openCreativeScreen() {
        this.minecraft.gui.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()));
    }

    // ==================== 静态工具 ====================

    public static void renderEntityInInventoryFollowsMouse(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int size, float offsetY, float mouseX, float mouseY, LivingEntity entity) {
        float centerX = (x0 + x1) / 2.0F;
        float centerY = (y0 + y1) / 2.0F;
        float angleX = (float) Math.atan((centerX - mouseX) / ENTITY_ANGLE_DIVISOR);
        float angleY = (float) Math.atan((centerY - mouseY) / ENTITY_ANGLE_DIVISOR);
        renderEntityInInventoryFollowsAngle(graphics, x0, y0, x1, y1, size, offsetY, angleX, angleY, entity);
    }

    public static void renderEntityInInventoryFollowsAngle(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int size, float offsetY, float angleX, float angleY, LivingEntity entity) {
        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf rotationX = new Quaternionf().rotateX(angleY * ENTITY_ANGLE_MULTIPLIER * (float) (Math.PI / 180.0));
        rotation.mul(rotationX);
        EntityRenderState renderState = createRenderState(entity);
        if (renderState instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = 180.0F + angleX * ENTITY_ANGLE_MULTIPLIER;
            livingState.yRot = angleX * ENTITY_ANGLE_MULTIPLIER;
            if (livingState.pose != Pose.FALL_FLYING) {
                livingState.xRot = -angleY * ENTITY_ANGLE_MULTIPLIER;
            } else {
                livingState.xRot = 0.0F;
            }
            livingState.boundingBoxWidth = livingState.boundingBoxWidth / livingState.scale;
            livingState.boundingBoxHeight = livingState.boundingBoxHeight / livingState.scale;
            livingState.scale = 1.0F;
        }
        Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + offsetY, 0.0F);
        graphics.entity(renderState, size, translation, rotation, rotationX, x0, y0, x1, y1);
    }

    private static EntityRenderState createRenderState(LivingEntity entity) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        return renderState;
    }
}
