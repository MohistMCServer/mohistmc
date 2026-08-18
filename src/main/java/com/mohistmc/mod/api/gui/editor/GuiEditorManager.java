package com.mohistmc.mod.api.gui.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.api.gui.EnhancedScreen;
import com.mohistmc.mod.api.gui.Panel;
import com.mohistmc.mod.api.gui.PositionedWidget;
import com.mohistmc.mod.api.gui.SaveFileNameModal;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

/**
 * GUI 编辑器 — 在任意 EnhancedScreen 上启用编辑模式，支持拖拽调整组件位置/大小并保存配置。
 *
 * <p>使用方式：按 F8 切换编辑模式，点击选中组件，拖拽手柄调整大小，拖拽组件本体移动位置，
 * Ctrl+S 保存布局（默认使用屏幕类名，可通过 Ctrl+Shift+S 自定义文件名），
 * 布局文件保存在 {@code config/mohistmc/gui_layouts/} 目录下。</p>
 */
public class GuiEditorManager {

    private static boolean editorMode = false;
    @Nullable private static EnhancedScreen currentScreen;
    @Nullable private static PositionedWidget selectedWidget;
    @Nullable private static PositionedWidget hoveredWidget;

    /** 当前拖拽的手柄索引，-1=未拖拽，0-7=手柄编号 */
    private static int dragHandle = -1;
    /** 拖拽模式：移动组件 */
    private static boolean draggingWidget = false;
    private static int dragStartX, dragStartY;
    private static int dragOrigX, dragOrigY, dragOrigW, dragOrigH;

    /** 自定义保存文件名（null 时使用屏幕类名） */
    @Nullable private static String saveFileName = null;
    /** 另存为悬浮窗（Ctrl+Shift+S 弹出） */
    @Nullable private static SaveFileNameModal saveModal;

    /** 手柄大小（逻辑像素） */
    private static final int HANDLE_SZ = 8;
    /** 手柄半宽 */
    private static final int HANDLE_HALF = HANDLE_SZ / 2;

    /** 8 个手柄的网格位置：{col, row}，col/row ∈ {0,1,2} */
    private static final int[][] HANDLE_GRID = {
        {0, 0}, // 0: 左上
        {1, 0}, // 1: 中上
        {2, 0}, // 2: 右上
        {0, 1}, // 3: 左中
        {2, 1}, // 4: 右中
        {0, 2}, // 5: 左下
        {1, 2}, // 6: 中下
        {2, 2}, // 7: 右下
    };

    /** 手柄对应的光标样式（GLFW 标准光标） */
    private static final int[] HANDLE_CURSORS = {
        0, // 0: 左上 → arrow
        1, // 1: 中上 → arrow  (text I-beam)
        2, // 2: 右上 → arrow
        3, // 3: 左中 → arrow  (hand)
        4, // 4: 右中 → arrow  (hand)
        5, // 5: 左下 → arrow
        6, // 6: 中下 → arrow  (text I-beam)
        7, // 7: 右下 → arrow
    };

    // ======== 编辑器状态 ========

    /** 切换编辑模式（仅创造模式可用；无玩家环境如主菜单时放行） */
    public static void toggle() {
        var player = Minecraft.getInstance().player;
        if (player != null && !player.isCreative()) {
            player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("§c[GUI编辑器] 仅创造模式可用"));
            return;
        }
        editorMode = !editorMode;
        if (!editorMode) {
            selectedWidget = null;
            hoveredWidget = null;
            dragHandle = -1;
            draggingWidget = false;
            if (saveModal != null && saveModal.isVisible()) {
                saveModal.hide();
            }
        }
    }

    /** 编辑模式是否激活 */
    public static boolean isActive() {
        return editorMode;
    }

    /** 设置当前编辑的屏幕 */
    public static void setScreen(@Nullable EnhancedScreen screen) {
        currentScreen = screen;
        if (screen == null) {
            selectedWidget = null;
            hoveredWidget = null;
            dragHandle = -1;
            draggingWidget = false;
        }
    }

    /** 设置自定义保存文件名 */
    public static void setSaveFileName(String name) {
        saveFileName = name;
    }

    /** 重置为默认文件名（使用屏幕类名） */
    public static void resetSaveFileName() {
        saveFileName = null;
    }

    /** 获取当前保存文件名（显示用） */
    @Nullable
    public static String getSaveFileName() {
        return saveFileName;
    }

    /** 开始另存为：弹出居中的文件名输入悬浮窗 */
    public static void startSaveAs() {
        if (currentScreen == null) return;
        if (saveModal == null || !saveModal.isVisible()) {
            saveModal = new SaveFileNameModal(currentScreen, () -> {
                String name = saveModal.getEnteredName();
                saveFileName = name;
                saveLayout(name);
            }, () -> { /* 取消：不保存 */ });
        }
        currentScreen.addModalExternal(saveModal);
        saveModal.show();
    }

    // ======== 渲染 ========

    /**
     * 在 EnhancedScreen 的矩阵变换内渲染编辑器覆盖层（手柄、选中框、信息标签）。
     * 使用逻辑坐标。
     */
    public static void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!editorMode || currentScreen == null) return;

        // 非创造模式自动退出编辑模式（防止打开后切换模式）
        var player = Minecraft.getInstance().player;
        if (player != null && !player.isCreative()) {
            editorMode = false;
            selectedWidget = null;
            hoveredWidget = null;
            dragHandle = -1;
            draggingWidget = false;
            return;
        }

        // 收集所有可编辑的组件（含 Panel 子组件）
        List<PositionedWidget> allWidgets = collectAllWidgets(currentScreen);

        // 1) 绘制所有组件的轮廓（浅色虚线框）
        for (var w : allWidgets) {
            if (w == selectedWidget) continue;
            if (w.getEditorId() == null) continue;
            int ax = w.getAbsoluteX();
            int ay = w.getAbsoluteY();
            // 半透明浅蓝边框
            graphics.fill(ax, ay, ax + w.width, ay + 1, 0x44AAAAFF);
            graphics.fill(ax, ay + w.height - 1, ax + w.width, ay + w.height, 0x44AAAAFF);
            graphics.fill(ax, ay, ax + 1, ay + w.height, 0x44AAAAFF);
            graphics.fill(ax + w.width - 1, ay, ax + w.width, ay + w.height, 0x44AAAAFF);
        }

        // 2) 绘制选中组件的高亮框和手柄
        if (selectedWidget != null) {
            int ax = selectedWidget.getAbsoluteX();
            int ay = selectedWidget.getAbsoluteY();
            int aw = selectedWidget.width;
            int ah = selectedWidget.height;

            // 高亮边框（亮黄色）
            graphics.fill(ax, ay, ax + aw, ay + 2, 0xFFFFFF00);
            graphics.fill(ax, ay + ah - 2, ax + aw, ay + ah, 0xFFFFFF00);
            graphics.fill(ax, ay, ax + 2, ay + ah, 0xFFFFFF00);
            graphics.fill(ax + aw - 2, ay, ax + aw, ay + ah, 0xFFFFFF00);

            // 绘制 8 个手柄
            for (int i = 0; i < 8; i++) {
                int[] pos = getHandlePos(i, ax, ay, aw, ah);
                int hx = pos[0] - HANDLE_HALF;
                int hy = pos[1] - HANDLE_HALF;
                // 手柄填充色：白色填充，黑色边框
                int handleColor = (dragHandle == i) ? 0xFFFFAA00 : 0xFFFFFFFF;
                graphics.fill(hx, hy, hx + HANDLE_SZ, hy + HANDLE_SZ, handleColor);
                graphics.fill(hx, hy, hx + HANDLE_SZ, hy + 1, 0xFF000000);
                graphics.fill(hx, hy + HANDLE_SZ - 1, hx + HANDLE_SZ, hy + HANDLE_SZ, 0xFF000000);
                graphics.fill(hx, hy, hx + 1, hy + HANDLE_SZ, 0xFF000000);
                graphics.fill(hx + HANDLE_SZ - 1, hy, hx + HANDLE_SZ, hy + HANDLE_SZ, 0xFF000000);
            }
        }

        // 3) 绘制悬停组件信息
        if (hoveredWidget != null && hoveredWidget != selectedWidget && hoveredWidget.getEditorId() != null) {
            int ax = hoveredWidget.getAbsoluteX();
            int ay = hoveredWidget.getAbsoluteY();
            // 浅色悬停边框
            graphics.fill(ax, ay, ax + hoveredWidget.width, ay + 1, 0x88FFFFFF);
            graphics.fill(ax, ay + hoveredWidget.height - 1, ax + hoveredWidget.width, ay + hoveredWidget.height, 0x88FFFFFF);
            graphics.fill(ax, ay, ax + 1, ay + hoveredWidget.height, 0x88FFFFFF);
            graphics.fill(ax + hoveredWidget.width - 1, ay, ax + hoveredWidget.width, ay + hoveredWidget.height, 0x88FFFFFF);
        }

        // 4) 顶部信息栏
        renderInfoBar(graphics);

        // 5) 选中组件信息提示
        if (selectedWidget != null && selectedWidget.getEditorId() != null) {
            String info = selectedWidget.getEditorId()
                + "  x=" + selectedWidget.getRelativeX() + " y=" + selectedWidget.getRelativeY()
                + " w=" + selectedWidget.width + " h=" + selectedWidget.height;
            if (selectedWidget.isAnchorRight()) info += " [R]";
            if (selectedWidget.isAnchorBottom()) info += " [B]";
            int sx = GuiCoord.toScreenX(selectedWidget.getAbsoluteX());
            int sy = GuiCoord.toScreenY(selectedWidget.getAbsoluteY());
            graphics.setTooltipForNextFrame(net.minecraft.network.chat.Component.literal(info), sx, sy - 5);
        }
    }

    /** 渲染顶部信息栏（编辑模式状态） */
    private static void renderInfoBar(GuiGraphicsExtractor graphics) {
        var mc = Minecraft.getInstance();
        var font = mc.font;
        String text = "§e✎ GUI 编辑器 [F8 切换]  [Ctrl+S 保存]  [Ctrl+Shift+S 另存为]";
        if (selectedWidget != null) {
            text += "  §b选中: " + selectedWidget.getEditorId();
        }
        if (saveFileName != null) {
            text += "  §a文件: " + saveFileName;
        }
        int logicalW = currentScreen != null ? EnhancedScreen.BASE_W : 1280;
        int textW = font.width(text);
        int barH = 20;
        // 背景（使用逻辑坐标）
        graphics.fill(0, 0, logicalW, barH, 0xCC333333);
        // 文字居中
        graphics.text(font, net.minecraft.network.chat.Component.literal(text),
            (logicalW - textW) / 2, 4, 0xFFFFFFFF);
    }

    // ======== 鼠标交互 ========

    /** 处理鼠标点击（逻辑坐标），返回 true 表示已消费事件 */
    public static boolean handleClick(MouseButtonEvent event) {
        if (!editorMode || currentScreen == null) return false;

        int lx = currentScreen.toLogicalX(event.x());
        int ly = currentScreen.toLogicalY(event.y());

        // 1) 如果点击在顶部信息栏区域，不消费事件（让编辑器继续工作）
        // 2) 检查是否点击到手柄
        if (selectedWidget != null) {
            int handle = getHandleAt(lx, ly, selectedWidget);
            if (handle >= 0) {
                dragHandle = handle;
                dragStartX = lx;
                dragStartY = ly;
                dragOrigX = selectedWidget.getRelativeX();
                dragOrigY = selectedWidget.getRelativeY();
                dragOrigW = selectedWidget.width;
                dragOrigH = selectedWidget.height;
                return true;
            }
        }

        // 3) 检查是否点击到组件主体（选中或移动）
        List<PositionedWidget> allWidgets = collectAllWidgets(currentScreen);
        PositionedWidget clicked = null;
        // 反向遍历（最上层优先）
        for (int i = allWidgets.size() - 1; i >= 0; i--) {
            var w = allWidgets.get(i);
            if (w.getEditorId() == null) continue;
            if (w.isMouseOver(lx, ly)) {
                clicked = w;
                break;
            }
        }

        if (clicked != null) {
            if (clicked == selectedWidget) {
                // 如果点击的是已选中的组件，开始拖拽移动
                draggingWidget = true;
                dragStartX = lx;
                dragStartY = ly;
                // 记录绝对坐标，拖拽时以绝对坐标计算再转换回相对坐标（兼容锚定组件）
                dragOrigX = clicked.getAbsoluteX();
                dragOrigY = clicked.getAbsoluteY();
                dragOrigW = clicked.width;
                dragOrigH = clicked.height;
            } else {
                // 选中新组件
                selectedWidget = clicked;
                dragHandle = -1;
                draggingWidget = false;
            }
            return true;
        }

        // 4) 点击空白区域取消选中
        selectedWidget = null;
        dragHandle = -1;
        draggingWidget = false;
        return false;
    }

    /** 处理鼠标拖拽（逻辑坐标） */
    public static void handleDrag(int lx, int ly) {
        if (!editorMode || selectedWidget == null) return;

        if (dragHandle >= 0) {
            // 拖拽手柄调整大小
            resizeWithHandle(lx, ly);
        } else if (draggingWidget) {
            // 拖拽移动组件：在绝对坐标空间计算，鼠标向右下拖 → 组件向右下移（各方向一致）
            int newAbsX = dragOrigX + (lx - dragStartX);
            int newAbsY = dragOrigY + (ly - dragStartY);
            // 将绝对坐标转换回相对坐标（考虑父容器偏移与锚定）
            int newRelX = selectedWidget.isAnchorRight()
                    ? selectedWidget.getScreenLeft() + selectedWidget.getContentWidth() - newAbsX - selectedWidget.width
                    : newAbsX - selectedWidget.getScreenLeft();
            int newRelY = selectedWidget.isAnchorBottom()
                    ? selectedWidget.getScreenTop() + selectedWidget.getContentHeight() - newAbsY - selectedWidget.height
                    : newAbsY - selectedWidget.getScreenTop();
            selectedWidget.setRelativeX(Math.max(0, newRelX));
            selectedWidget.setRelativeY(Math.max(0, newRelY));
            selectedWidget.updateLayout(selectedWidget.getRelativeX(), selectedWidget.getRelativeY(),
                selectedWidget.width, selectedWidget.height);
        }
    }

    /** 处理鼠标释放 */
    public static void handleRelease() {
        dragHandle = -1;
        draggingWidget = false;
    }

    /** 处理鼠标移动（逻辑坐标），更新悬停组件 */
    public static void handleMouseMove(int lx, int ly) {
        if (!editorMode || currentScreen == null) return;

        // 更新悬停组件
        if (selectedWidget != null) {
            int handle = getHandleAt(lx, ly, selectedWidget);
            if (handle >= 0) {
                // 悬停在手柄上
                return;
            }
        }

        List<PositionedWidget> allWidgets = collectAllWidgets(currentScreen);
        hoveredWidget = null;
        for (int i = allWidgets.size() - 1; i >= 0; i--) {
            var w = allWidgets.get(i);
            if (w.getEditorId() == null) continue;
            if (w.isMouseOver(lx, ly)) {
                hoveredWidget = w;
                break;
            }
        }
    }

    // ======== 手柄拖拽逻辑 ========

    private static void resizeWithHandle(int lx, int ly) {
        int ax = selectedWidget.getAbsoluteX();
        int ay = selectedWidget.getAbsoluteY();
        int aw = selectedWidget.width;
        int ah = selectedWidget.height;

        int col = HANDLE_GRID[dragHandle][0];
        int row = HANDLE_GRID[dragHandle][1];

        // 在绝对坐标空间中计算，最后再转换回相对坐标
        int newAx = ax;
        int newAy = ay;
        int newW = aw;
        int newH = ah;

        // 最小尺寸限制
        int minSize = 16;

        switch (col) {
            case 0: // 左边缘 — 右边缘不动，调整左边缘位置和宽度
                int rightEdge = ax + aw;
                newW = Math.max(minSize, rightEdge - lx);
                newAx = rightEdge - newW;
                break;
            case 2: // 右边缘 — 左边缘不动，调整宽度
                newW = Math.max(minSize, lx - ax);
                break;
            // case 1: 中间列，宽度不变
        }

        switch (row) {
            case 0: // 上边缘 — 下边缘不动，调整上边缘位置和高度
                int bottomEdge = ay + ah;
                newH = Math.max(minSize, bottomEdge - ly);
                newAy = bottomEdge - newH;
                break;
            case 2: // 下边缘 — 上边缘不动，调整高度
                newH = Math.max(minSize, ly - ay);
                break;
            // case 1: 中间行，高度不变
        }

        // 将绝对坐标转换回相对坐标（考虑父容器偏移与锚定）
        int newRelX, newRelY;
        if (selectedWidget.isAnchorRight()) {
            newRelX = selectedWidget.getScreenLeft() + selectedWidget.getContentWidth() - newAx - newW;
        } else {
            newRelX = newAx - selectedWidget.getScreenLeft();
        }
        if (selectedWidget.isAnchorBottom()) {
            newRelY = selectedWidget.getScreenTop() + selectedWidget.getContentHeight() - newAy - newH;
        } else {
            newRelY = newAy - selectedWidget.getScreenTop();
        }

        selectedWidget.setRelativeX(Math.max(0, newRelX));
        selectedWidget.setRelativeY(Math.max(0, newRelY));
        selectedWidget.width = Math.max(minSize, newW);
        selectedWidget.height = Math.max(minSize, newH);
        selectedWidget.updateLayout(selectedWidget.getRelativeX(), selectedWidget.getRelativeY(),
            selectedWidget.width, selectedWidget.height);
    }

    // ======== 保存/加载 ========

    /** 保存布局到默认文件（使用屏幕类名或自定义文件名） */
    public static void saveLayout() {
        saveLayout(null);
    }

    /** 保存布局到指定文件 */
    public static void saveLayout(@Nullable String fileName) {
        if (currentScreen == null) return;

        String screenName = currentScreen.getClass().getSimpleName();
        String actualFileName = fileName != null ? fileName :
                                (saveFileName != null ? saveFileName : screenName);
        List<PositionedWidget> allWidgets = collectAllWidgets(currentScreen);
        List<GuiEditorEntry> entries = new ArrayList<>();

        for (var w : allWidgets) {
            if (w.getEditorId() == null) continue;
            String type = w.getClass().getSimpleName();
            if (w instanceof Panel) type = "Panel";
            entries.add(new GuiEditorEntry(
                w.getEditorId(), type,
                w.getRelativeX(), w.getRelativeY(), w.width, w.height,
                w.isAnchorRight(), w.isAnchorBottom()
            ));
        }

        if (entries.isEmpty()) {
            MohistMC.LOGGER.warn("[GuiEditor] No editable widgets found in {}", screenName);
            return;
        }

        try {
            File dir = new File(Minecraft.getInstance().gameDirectory, "config/mohistmc/gui_layouts");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, actualFileName + ".json");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, Object> root = new HashMap<>();
            root.put("screen", screenName);
            root.put("widgets", entries);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(root, writer);
            }

            MohistMC.LOGGER.info("[GuiEditor] Layout saved: {} ({} widgets)", file.getAbsolutePath(), entries.size());
            var player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("§a[GUI编辑器] 布局已保存: " + file.getName()));
            }
        } catch (Exception e) {
            MohistMC.LOGGER.error("[GuiEditor] Failed to save layout", e);
        }
    }

    /** 从 JSON 文件加载布局并应用到当前屏幕（使用默认文件名） */
    public static void loadLayout(EnhancedScreen screen) {
        loadLayout(screen, null);
    }

    /** 从 JSON 文件加载布局并应用到当前屏幕 */
    public static void loadLayout(EnhancedScreen screen, @Nullable String fileName) {
        String screenName = screen.getClass().getSimpleName();
        String actualFileName = fileName != null ? fileName : screenName;
        File file = new File(Minecraft.getInstance().gameDirectory, "config/mohistmc/gui_layouts/" + actualFileName + ".json");
        if (!file.exists()) {
            MohistMC.LOGGER.info("[GuiEditor] No saved layout found: {}.json", actualFileName);
            return;
        }

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> root;
            try (FileReader reader = new FileReader(file)) {
                root = gson.fromJson(reader, type);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> entries = (List<Map<String, Object>>) root.get("widgets");
            if (entries == null) return;

            List<PositionedWidget> allWidgets = collectAllWidgets(screen);
            // 建立 editorId → widget 映射
            Map<String, PositionedWidget> widgetMap = new HashMap<>();
            for (var w : allWidgets) {
                if (w.getEditorId() != null) {
                    widgetMap.put(w.getEditorId(), w);
                }
            }

            int applied = 0;
            for (var entry : entries) {
                String id = (String) entry.get("id");
                PositionedWidget w = widgetMap.get(id);
                if (w == null) continue;

                Object x = entry.get("x");
                Object y = entry.get("y");
                Object w2 = entry.get("w");
                Object h = entry.get("h");
                Object ar = entry.get("anchorRight");
                Object ab = entry.get("anchorBottom");

                if (x instanceof Number) w.setRelativeX(((Number) x).intValue());
                if (y instanceof Number) w.setRelativeY(((Number) y).intValue());
                if (w2 instanceof Number) w.width = ((Number) w2).intValue();
                if (h instanceof Number) w.height = ((Number) h).intValue();
                if (ar instanceof Boolean) w.setAnchorRight((Boolean) ar);
                if (ab instanceof Boolean) w.setAnchorBottom((Boolean) ab);

                w.updateLayout(w.getRelativeX(), w.getRelativeY(), w.width, w.height);
                applied++;
            }

            MohistMC.LOGGER.info("[GuiEditor] Layout loaded: {} ({} widgets applied)", file.getName(), applied);
        } catch (Exception e) {
            MohistMC.LOGGER.error("[GuiEditor] Failed to load layout", e);
        }
    }

    // ======== 工具方法 ========

    /** 获取手柄的逻辑坐标位置（中心点） */
    private static int[] getHandlePos(int handleIndex, int ax, int ay, int aw, int ah) {
        int col = HANDLE_GRID[handleIndex][0];
        int row = HANDLE_GRID[handleIndex][1];
        int hx = switch (col) {
            case 0 -> ax;
            case 1 -> ax + aw / 2;
            case 2 -> ax + aw;
            default -> ax;
        };
        int hy = switch (row) {
            case 0 -> ay;
            case 1 -> ay + ah / 2;
            case 2 -> ay + ah;
            default -> ay;
        };
        return new int[]{hx, hy};
    }

    /** 查找鼠标位置所在的手柄索引 */
    private static int getHandleAt(int lx, int ly, PositionedWidget w) {
        int ax = w.getAbsoluteX();
        int ay = w.getAbsoluteY();
        int aw = w.width;
        int ah = w.height;

        for (int i = 0; i < 8; i++) {
            int[] pos = getHandlePos(i, ax, ay, aw, ah);
            int hx = pos[0] - HANDLE_HALF;
            int hy = pos[1] - HANDLE_HALF;
            if (lx >= hx && lx < hx + HANDLE_SZ && ly >= hy && ly < hy + HANDLE_SZ) {
                return i;
            }
        }
        return -1;
    }

    /** 收集屏幕上所有可编辑的 PositionedWidget（含 Panel 的子组件） */
    private static List<PositionedWidget> collectAllWidgets(EnhancedScreen screen) {
        List<PositionedWidget> result = new ArrayList<>();
        collectWidgets(screen, result);
        return result;
    }

    /** 递归收集所有组件 */
    private static void collectWidgets(EnhancedScreen screen, List<PositionedWidget> out) {
        for (var w : screen.getWidgets()) {
            out.add(w);
            if (w instanceof Panel panel) {
                collectChildren(panel, out);
            }
        }
    }

    /** 递归收集 Panel 的子组件 */
    private static void collectChildren(Panel panel, List<PositionedWidget> out) {
        out.addAll(panel.collectAllChildren());
    }

    // ======== 坐标转换辅助（用于编辑器渲染） ========

    private static class GuiCoord {
        static int toScreenX(int lx) {
            if (currentScreen == null) return lx;
            return currentScreen.toScreenX(lx);
        }
        static int toScreenY(int ly) {
            if (currentScreen == null) return ly;
            return currentScreen.toScreenY(ly);
        }
    }
}