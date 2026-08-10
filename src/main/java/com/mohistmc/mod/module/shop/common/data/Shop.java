package com.mohistmc.mod.module.shop.common.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 商店（多商店架构中的单个商店）
 * <p>每个商店拥有独立的商品列表和类别列表。
 *
 * @author Mgazul
 * @date 2026/8/10
 */
public final class Shop {

    private final String id;
    private String name;
    private final List<ShopProduct> products = new ArrayList<>();
    private final List<ShopCategory> categories = new ArrayList<>();
    private int nextCategoryId = 0;

    public Shop(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShopProduct> getProducts() {
        return products;
    }

    public List<ShopCategory> getCategories() {
        return categories;
    }

    // ======== 商品操作 ========

    public ShopProduct getProductById(int productId) {
        for (var p : products) {
            if (p.id() == productId) return p;
        }
        return null;
    }

    public int getNextProductId() {
        int max = -1;
        for (var p : products) {
            if (p.id() > max) max = p.id();
        }
        return max + 1;
    }

    public ShopProduct addProduct(ShopProduct product) {
        products.add(product);
        return product;
    }

    /** 按指定 ID 添加商品（反序列化时使用） */
    public ShopProduct addProduct(int id, ShopProduct product) {
        products.add(product);
        return product;
    }

    public ShopProduct modifyProduct(int productId, int price, int stock, int categoryId) {
        for (int i = 0; i < products.size(); i++) {
            var p = products.get(i);
            if (p.id() == productId) {
                var updated = new ShopProduct(productId, id, p.stack(), Math.max(1, price), categoryId, stock, p.restockCycle());
                products.set(i, updated);
                return updated;
            }
        }
        return null;
    }

    public boolean removeProduct(int productId) {
        return products.removeIf(p -> p.id() == productId);
    }

    // ======== 类别操作 ========

    public int getNextCategoryId() {
        return nextCategoryId;
    }

    public int getAndIncrementNextCategoryId() {
        return nextCategoryId++;
    }

    public void setNextCategoryId(int nextId) {
        this.nextCategoryId = Math.max(nextCategoryId, nextId);
    }

    public ShopCategory addCategory(String name, String langKey) {
        int catId = getAndIncrementNextCategoryId();
        var cat = new ShopCategory(catId, name, langKey);
        categories.add(cat);
        return cat;
    }

    /** 按指定 ID 添加类别（反序列化时使用） */
    public ShopCategory addCategory(int id, String name, String langKey) {
        setNextCategoryId(id + 1);
        var cat = new ShopCategory(id, name, langKey);
        categories.add(cat);
        return cat;
    }

    public ShopCategory getCategory(int categoryId) {
        for (var c : categories) {
            if (c.getId() == categoryId) return c;
        }
        return null;
    }

    public ShopCategory modifyCategory(int categoryId, String newName, String newLangKey) {
        for (var c : categories) {
            if (c.getId() == categoryId) {
                c.setName(newName);
                c.setLangKey(newLangKey);
                return c;
            }
        }
        return null;
    }

    public boolean removeCategory(int categoryId) {
        return categories.removeIf(c -> c.getId() == categoryId);
    }
}