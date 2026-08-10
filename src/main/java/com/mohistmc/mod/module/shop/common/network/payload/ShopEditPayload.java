package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端 → 服务端：商店编辑请求（管理员操作）
 * <p>action: 0=编辑, 1=新增, 2=删除, 3=新增类别, 4=修改类别, 5=删除类别, 6=新增商店
 *
 * @param action      操作类型
 * @param shopId      所属商店 ID
 * @param itemId      商品 ID（编辑/删除时使用）
 * @param stack       物品（新增时使用）
 * @param price       价格（编辑/新增时使用）
 * @param stock       库存（编辑/新增时使用，-1=无限）
 * @param categoryId  类别 ID（编辑/新增时使用）
 * @param restockCycle 补货周期 ordinal（新增时使用）
 * @param name        名称（新增/修改类别或商店时使用）
 * @param langKey     语言键（新增/修改类别时使用）
 * @author Mgazul
 * @date 2026/8/10
 */
public record ShopEditPayload(int action, String shopId, int itemId, ItemStack stack, int price, int stock,
                              int categoryId, int restockCycle, String name, String langKey) implements CustomPacketPayload {

    public static final int ACTION_EDIT = 0;
    public static final int ACTION_ADD = 1;
    public static final int ACTION_DELETE = 2;
    public static final int ACTION_ADD_CATEGORY = 3;
    public static final int ACTION_EDIT_CATEGORY = 4;
    public static final int ACTION_DELETE_CATEGORY = 5;
    public static final int ACTION_ADD_SHOP = 6;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "shop_edit");
    public static final Type<ShopEditPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopEditPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ShopEditPayload::action,
            ByteBufCodecs.STRING_UTF8, ShopEditPayload::shopId,
            ByteBufCodecs.INT, ShopEditPayload::itemId,
            ItemStack.OPTIONAL_STREAM_CODEC, ShopEditPayload::stack,
            ByteBufCodecs.INT, ShopEditPayload::price,
            ByteBufCodecs.INT, ShopEditPayload::stock,
            ByteBufCodecs.INT, ShopEditPayload::categoryId,
            ByteBufCodecs.INT, ShopEditPayload::restockCycle,
            ByteBufCodecs.STRING_UTF8, ShopEditPayload::name,
            ByteBufCodecs.STRING_UTF8, ShopEditPayload::langKey,
            ShopEditPayload::new
    );

    /** 编辑请求快捷构造 */
    public static ShopEditPayload edit(String shopId, int itemId, int price, int stock, int categoryId) {
        return new ShopEditPayload(ACTION_EDIT, shopId, itemId, ItemStack.EMPTY, price, stock, categoryId, 0, "", "");
    }

    /** 新增请求快捷构造 */
    public static ShopEditPayload add(String shopId, ItemStack stack, int price, int stock, int categoryId, int restockCycle) {
        return new ShopEditPayload(ACTION_ADD, shopId, -1, stack, price, stock, categoryId, restockCycle, "", "");
    }

    /** 删除请求快捷构造 */
    public static ShopEditPayload delete(int itemId) {
        return new ShopEditPayload(ACTION_DELETE, "", itemId, ItemStack.EMPTY, 0, 0, 0, 0, "", "");
    }

    /** 新增类别快捷构造 */
    public static ShopEditPayload addCategory(String shopId, String name, String langKey) {
        return new ShopEditPayload(ACTION_ADD_CATEGORY, shopId, -1, ItemStack.EMPTY, 0, 0, 0, 0, name, langKey);
    }

    /** 修改类别快捷构造 */
    public static ShopEditPayload editCategory(String shopId, int categoryId, String name, String langKey) {
        return new ShopEditPayload(ACTION_EDIT_CATEGORY, shopId, -1, ItemStack.EMPTY, 0, 0, categoryId, 0, name, langKey);
    }

    /** 删除类别快捷构造 */
    public static ShopEditPayload deleteCategory(String shopId, int categoryId) {
        return new ShopEditPayload(ACTION_DELETE_CATEGORY, shopId, -1, ItemStack.EMPTY, 0, 0, categoryId, 0, "", "");
    }

    /** 新增商店快捷构造 */
    public static ShopEditPayload addShop(String shopId, String name) {
        return new ShopEditPayload(ACTION_ADD_SHOP, shopId, -1, ItemStack.EMPTY, 0, 0, 0, 0, name, "");
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}