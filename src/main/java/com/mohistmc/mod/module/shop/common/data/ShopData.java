package com.mohistmc.mod.module.shop.common.data;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.shop.common.network.payload.ShopDataSyncPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统商店数据（v2 多商店架构 — JSON 持久化）
 * <p>数据存储于本地 JSON 文件，启动时自动加载，修改后自动保存。
 * 服务端为权威，编辑后广播同步到所有客户端。
 *
 * @author Mgazul
 * @date 2026/8/10
 */
public final class ShopData {

    /** 默认商店 ID */
    public static final String DEFAULT_SHOP_ID = "default";

    /** 数据文件名（存于服务器 world 目录下的 mod 数据文件夹） */
    private static final String DATA_FILE_NAME = "shop_data.json";

    /** 所有商店（按插入顺序） */
    private static final Map<String, Shop> SHOPS = new LinkedHashMap<>();

    private static boolean initialized;
    /** 数据文件路径（服务端运行时设置） */
    private static Path dataFile;

    private ShopData() {
    }

    /** 初始化数据文件路径（服务端启动时调用） */
    public static void setDataFile(Path serverDir) {
        dataFile = serverDir.resolve("mods").resolve(MohistMC.MODID).resolve(DATA_FILE_NAME);
    }

    /** 获取数据文件路径 */
    @Nullable
    public static Path getDataFile() {
        return dataFile;
    }

    // ======== 初始化 ========

    private static void ensureInit() {
        if (initialized) return;
        initialized = true;

        // 1) 尝试从本地文件加载
        if (dataFile != null) {
            try {
                if (Files.exists(dataFile)) {
                    var shops = ShopDataSerializer.readFromFile(dataFile);
                    for (var shop : shops) {
                        SHOPS.put(shop.getId(), shop);
                    }
                    if (!shops.isEmpty()) return;
                }
            } catch (IOException ignored) {
            }
        }

        // 2) 尝试从 mod 资源加载默认数据
        var shops = ShopDataSerializer.loadDefaultFromResources();
        if (!shops.isEmpty()) {
            for (var shop : shops) {
                SHOPS.put(shop.getId(), shop);
            }
            // 首次加载默认数据，保存到本地
            save();
            return;
        }

        // 3) 兜底：创建空的默认商店
        var shop = new Shop(DEFAULT_SHOP_ID, "系统商店");
        SHOPS.put(DEFAULT_SHOP_ID, shop);
        save();
    }

    /** 保存所有数据到本地 JSON 文件 */
    public static void save() {
        if (dataFile == null) return;
        try {
            Files.createDirectories(dataFile.getParent());
            ShopDataSerializer.writeToFile(SHOPS.values().stream().toList(), dataFile);
        } catch (IOException ignored) {
        }
    }

    // ======== 商店查询 ========

    public static Collection<Shop> getAllShops() {
        ensureInit();
        return SHOPS.values();
    }

    @Nullable
    public static Shop getShop(String shopId) {
        ensureInit();
        return SHOPS.get(shopId);
    }

    public static Shop getDefaultShop() {
        return getShop(DEFAULT_SHOP_ID);
    }

    // ======== 商店管理 ========

    public static Shop createShop(String id, String name) {
        ensureInit();
        if (SHOPS.containsKey(id)) {
            throw new IllegalArgumentException("Shop already exists: " + id);
        }
        var shop = new Shop(id, name);
        SHOPS.put(id, shop);
        save();
        return shop;
    }

    public static void deleteShop(String id) {
        ensureInit();
        SHOPS.remove(id);
        save();
    }

    // ======== 商品操作（便捷方法，通过 shopId 路由到对应商店） ========

    @Nullable
    public static ShopProduct getById(int id) {
        ensureInit();
        for (var shop : SHOPS.values()) {
            var p = shop.getProductById(id);
            if (p != null) return p;
        }
        return null;
    }

    @Nullable
    public static ShopProduct getById(String shopId, int productId) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        return shop != null ? shop.getProductById(productId) : null;
    }

    public static int getNextId(String shopId) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        return shop != null ? shop.getNextProductId() : 0;
    }

    @Nullable
    public static ShopProduct modifyProduct(String shopId, int id, int price, int stock, int categoryId) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        var result = shop != null ? shop.modifyProduct(id, price, stock, categoryId) : null;
        if (result != null) save();
        return result;
    }

    public static ShopProduct addProduct(String shopId, ItemStack stack, int price, int categoryId, int stock, RestockCycle restockCycle) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        if (shop == null) return null;
        int id = shop.getNextProductId();
        var product = new ShopProduct(id, shopId, stack, Math.max(1, price), categoryId, stock, restockCycle);
        shop.addProduct(product);
        save();
        return product;
    }

    public static boolean removeProduct(String shopId, int id) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        boolean result = shop != null && shop.removeProduct(id);
        if (result) save();
        return result;
    }

    // ======== 类别操作 ========

    @Nullable
    public static ShopCategory addCategory(String shopId, String name, String langKey) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        var result = shop != null ? shop.addCategory(name, langKey) : null;
        if (result != null) save();
        return result;
    }

    @Nullable
    public static ShopCategory modifyCategory(String shopId, int categoryId, String newName, String newLangKey) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        var result = shop != null ? shop.modifyCategory(categoryId, newName, newLangKey) : null;
        if (result != null) save();
        return result;
    }

    public static boolean removeCategory(String shopId, int categoryId) {
        ensureInit();
        var shop = SHOPS.get(shopId);
        boolean result = shop != null && shop.removeCategory(categoryId);
        if (result) save();
        return result;
    }

    // ======== 客户端同步 ========

    /** 清空并重建商店数据（客户端同步用） */
    public static void clearAndRebuild(Collection<Shop> newShops) {
        SHOPS.clear();
        for (var shop : newShops) {
            SHOPS.put(shop.getId(), shop);
        }
        initialized = true;
    }

    /** 从网络同步数据并保存到本地 */
    public static void syncFromPayload(ShopDataSyncPayload payload) {
        payload.applyToClient();
        // applyToClient 使用 clearAndRebuild 更新内存数据
        // 这里我们额外保存一次，确保客户端数据也持久化
        save();
    }
}