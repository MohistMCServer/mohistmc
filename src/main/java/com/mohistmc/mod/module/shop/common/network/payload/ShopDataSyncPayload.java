package com.mohistmc.mod.module.shop.common.network.payload;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.module.shop.common.data.Shop;
import com.mohistmc.mod.module.shop.common.data.ShopCategory;
import com.mohistmc.mod.module.shop.common.data.ShopData;
import com.mohistmc.mod.module.shop.common.data.ShopProduct;
import com.mohistmc.mod.module.shop.common.data.RestockCycle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：同步商店数据（编辑后全量同步）
 * <p>包含所有商店的元数据、类别列表和商品列表。
 *
 * @param shops 商店列表（序列化后的完整数据）
 * @author Mgazul
 * @date 2026/8/10
 */
public record ShopDataSyncPayload(List<SerializedShop> shops) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MohistMC.MODID, "shop_data_sync");
    public static final Type<ShopDataSyncPayload> TYPE = new Type<>(ID);

    /** 商店序列化数据 */
    public record SerializedShop(String id, String name, int nextCategoryId,
                                 List<SerializedCategory> categories, List<ShopProduct> products) {}

    /** 类别序列化数据 */
    public record SerializedCategory(int id, String name, String langKey) {}

    // ======== 编解码器 ========

    private static final StreamCodec<RegistryFriendlyByteBuf, SerializedCategory> CATEGORY_STREAM_CODEC = StreamCodec.of(
            (buf, cat) -> {
                buf.writeInt(cat.id());
                buf.writeUtf(cat.name());
                buf.writeUtf(cat.langKey());
            },
            (buf) -> new SerializedCategory(buf.readInt(), buf.readUtf(), buf.readUtf())
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<SerializedCategory>> CATEGORY_LIST_STREAM_CODEC =
            ByteBufCodecs.collection(ArrayList::new, CATEGORY_STREAM_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, ShopProduct> PRODUCT_STREAM_CODEC = StreamCodec.of(
            (buf, product) -> {
                buf.writeInt(product.id());
                buf.writeUtf(product.shopId());
                ItemStack.STREAM_CODEC.encode(buf, product.stack());
                buf.writeInt(product.price());
                buf.writeInt(product.categoryId());
                buf.writeInt(product.stock());
                buf.writeInt(product.restockCycle().ordinal());
            },
            (buf) -> {
                int id = buf.readInt();
                String shopId = buf.readUtf();
                ItemStack stack = ItemStack.STREAM_CODEC.decode(buf);
                int price = buf.readInt();
                int categoryId = buf.readInt();
                int stock = buf.readInt();
                int restockCycleOrdinal = buf.readInt();
                return new ShopProduct(id, shopId, stack, price, categoryId, stock,
                        RestockCycle.values()[restockCycleOrdinal]);
            }
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<ShopProduct>> PRODUCT_LIST_STREAM_CODEC =
            ByteBufCodecs.collection(ArrayList::new, PRODUCT_STREAM_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, SerializedShop> SHOP_STREAM_CODEC = StreamCodec.of(
            (buf, shop) -> {
                buf.writeUtf(shop.id());
                buf.writeUtf(shop.name());
                buf.writeInt(shop.nextCategoryId());
                CATEGORY_LIST_STREAM_CODEC.encode(buf, shop.categories());
                PRODUCT_LIST_STREAM_CODEC.encode(buf, shop.products());
            },
            (buf) -> {
                String id = buf.readUtf();
                String name = buf.readUtf();
                int nextCategoryId = buf.readInt();
                var categories = CATEGORY_LIST_STREAM_CODEC.decode(buf);
                var products = PRODUCT_LIST_STREAM_CODEC.decode(buf);
                return new SerializedShop(id, name, nextCategoryId, categories, products);
            }
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<SerializedShop>> SHOP_LIST_STREAM_CODEC =
            ByteBufCodecs.collection(ArrayList::new, SHOP_STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
            SHOP_LIST_STREAM_CODEC, ShopDataSyncPayload::shops,
            ShopDataSyncPayload::new
    );

    /** 从当前 ShopData 构建同步包 */
    public static ShopDataSyncPayload fromCurrentData() {
        List<SerializedShop> shopList = new ArrayList<>();
        for (var shop : ShopData.getAllShops()) {
            List<SerializedCategory> cats = new ArrayList<>();
            for (var cat : shop.getCategories()) {
                cats.add(new SerializedCategory(cat.getId(), cat.getName(), cat.getLangKey()));
            }
            shopList.add(new SerializedShop(shop.getId(), shop.getName(), shop.getNextCategoryId(),
                    cats, List.copyOf(shop.getProducts())));
        }
        return new ShopDataSyncPayload(shopList);
    }

    /** 将同步包数据写入 ShopData（客户端使用） */
    public void applyToClient() {
        List<Shop> newShops = new ArrayList<>();
        for (var serializedShop : shops) {
            var shop = new Shop(serializedShop.id(), serializedShop.name());
            shop.setNextCategoryId(serializedShop.nextCategoryId());
            shop.getProducts().addAll(serializedShop.products());
            for (var cat : serializedShop.categories()) {
                shop.getCategories().add(new ShopCategory(cat.id(), cat.name(), cat.langKey()));
            }
            newShops.add(shop);
        }
        ShopData.clearAndRebuild(newShops);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}