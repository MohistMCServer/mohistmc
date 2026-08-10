package com.mohistmc.mod.module.shop.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mohistmc.mod.MohistMC;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 商店数据 JSON 序列化/反序列化工具
 * <p>负责将 ShopData 与本地 JSON 文件互相转换，支持默认数据资源加载。
 *
 * @author Mgazul
 * @date 2026/8/10
 */
public final class ShopDataSerializer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ShopDataSerializer() {
    }

    /** 序列化结构：商品 */
    public record ProductJson(int id, String itemId, int price, int categoryId,
                              int stock, String restockCycle) {
    }

    /** 序列化结构：类别 */
    public record CategoryJson(int id, String name, String langKey) {
    }

    /** 序列化结构：商店 */
    public record ShopJson(String id, String name, int nextCategoryId,
                           List<CategoryJson> categories, List<ProductJson> products) {
    }

    /** 序列化结构：整体数据 */
    public record ShopDataJson(List<ShopJson> shops) {
    }

    // ======== 序列化：内存 → JSON ========

    /** 将内存中的所有商店序列化为 JSON */
    public static ShopDataJson serialize(List<Shop> shops) {
        List<ShopJson> shopList = new ArrayList<>();
        for (var shop : shops) {
            List<CategoryJson> cats = new ArrayList<>();
            for (var cat : shop.getCategories()) {
                cats.add(new CategoryJson(cat.getId(), cat.getName(), cat.getLangKey()));
            }
            List<ProductJson> prods = new ArrayList<>();
            for (var product : shop.getProducts()) {
                String itemId = product.stack().getItem().builtInRegistryHolder().getKey().identifier().toString();
                prods.add(new ProductJson(
                        product.id(), itemId, product.price(),
                        product.categoryId(), product.stock(),
                        product.restockCycle().name()
                ));
            }
            shopList.add(new ShopJson(shop.getId(), shop.getName(), shop.getNextCategoryId(), cats, prods));
        }
        return new ShopDataJson(shopList);
    }

    /** 序列化为 JSON 字符串 */
    public static String toJson(List<Shop> shops) {
        return GSON.toJson(serialize(shops));
    }

    /** 写入 JSON 文件 */
    public static void writeToFile(List<Shop> shops, Path file) throws IOException {
        var json = serialize(shops);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }
    }

    // ======== 反序列化：JSON → 内存 ========

    /** 从 JSON 反序列化为 Shop 列表 */
    public static List<Shop> deserialize(ShopDataJson json) {
        List<Shop> shops = new ArrayList<>();
        for (var sj : json.shops()) {
            var shop = new Shop(sj.id(), sj.name());
            // 恢复 nextCategoryId
            shop.setNextCategoryId(sj.nextCategoryId());
            // 恢复类别
            for (var cj : sj.categories()) {
                shop.addCategory(cj.id(), cj.name(), cj.langKey());
            }
            // 恢复商品
            for (var pj : sj.products()) {
                ItemStack stack = resolveItemStack(pj.itemId());
                if (stack.isEmpty()) continue;
                RestockCycle cycle = RestockCycle.valueOf(pj.restockCycle());
                var product = new ShopProduct(pj.id(), sj.id(), stack, pj.price(),
                        pj.categoryId(), pj.stock(), cycle);
                shop.addProduct(product);
            }
            shops.add(shop);
        }
        return shops;
    }

    /** 从 JSON 字符串解析 */
    public static List<Shop> fromJson(String json) {
        var data = GSON.fromJson(json, ShopDataJson.class);
        return data != null ? deserialize(data) : new ArrayList<>();
    }

    /** 从文件加载 */
    public static List<Shop> readFromFile(Path file) throws IOException {
        if (!Files.exists(file)) return new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            var data = GSON.fromJson(reader, ShopDataJson.class);
            return data != null ? deserialize(data) : new ArrayList<>();
        }
    }

    /** 从 mod 资源加载默认数据 */
    public static List<Shop> loadDefaultFromResources() {
        // NeoForge 中资源路径以 / 开头
        try (InputStream is = MohistMC.class.getResourceAsStream("/data/shop/shop_data_default.json")) {
            if (is == null) return new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                var data = GSON.fromJson(reader, ShopDataJson.class);
                return data != null ? deserialize(data) : new ArrayList<>();
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // ======== 辅助方法 ========

    /** 从物品 ID 字符串解析 ItemStack */
    private static ItemStack resolveItemStack(String itemId) {
        try {
            var id = Identifier.parse(itemId.toLowerCase());
            var opt = BuiltInRegistries.ITEM.get(id);
            if (opt.isPresent()) {
                return new ItemStack(opt.get().value(), 1);
            }
        } catch (Exception ignored) {
        }
        return ItemStack.EMPTY;
    }
}