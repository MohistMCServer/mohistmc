package com.mohistmc.mod.module.shop.common.data;

/**
 * 商品类别（可动态自定义，不再使用枚举）
 * <p>每个商店拥有独立的类别列表；id = -1 表示"全部"筛选。
 *
 * @author Mgazul
 * @date 2026/8/10
 */
public final class ShopCategory {

    /** 特殊 ID：用于"全部"筛选（不持久化） */
    public static final int ALL_ID = -1;

    private int id;
    private String name;
    private String langKey;

    public ShopCategory(int id, String name, String langKey) {
        this.id = id;
        this.name = name;
        this.langKey = langKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }
}