package com.mohistmc.mod.module.shop.common.data;

import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.register.ItemRegister;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

/**
 * 系统商店商品目录（v1 硬编码，客户端/服务端同一来源；服务端按 id 校验仍为权威）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class ShopData {

    public static final List<ShopProduct> PRODUCTS = List.of(
            // —— 蔬菜 ——
            new ShopProduct(0, new ItemStack(ItemRegister.FAN_SHU.get()), 10, ShopCategory.VEGETABLE),
            new ShopProduct(1, new ItemStack(ItemRegister.YU_MI.get()), 15, ShopCategory.VEGETABLE),
            new ShopProduct(2, new ItemStack(ItemRegister.HUA_SHENG.get()), 20, ShopCategory.VEGETABLE),
            new ShopProduct(3, new ItemStack(ItemRegister.BO_CAI.get()), 12, ShopCategory.VEGETABLE),
            new ShopProduct(4, new ItemStack(ItemRegister.XIANG_CAI.get()), 9, ShopCategory.VEGETABLE),
            new ShopProduct(5, new ItemStack(ItemRegister.SHENG_CAI.get()), 11, ShopCategory.VEGETABLE),
            new ShopProduct(6, new ItemStack(ItemRegister.QIN_CAI.get()), 10, ShopCategory.VEGETABLE),
            new ShopProduct(7, new ItemStack(ItemRegister.JUAN_XIN_CAI.get()), 13, ShopCategory.VEGETABLE),
            new ShopProduct(8, new ItemStack(ItemRegister.ZI_SU_YE.get()), 8, ShopCategory.VEGETABLE),
            new ShopProduct(9, new ItemStack(ItemRegister.HUANG_GUA.get()), 12, ShopCategory.VEGETABLE),
            new ShopProduct(10, new ItemStack(ItemRegister.LIAN_OU.get()), 22, ShopCategory.VEGETABLE),
            new ShopProduct(11, new ItemStack(ItemRegister.BAI_LUO_BO.get()), 15, ShopCategory.VEGETABLE),
            new ShopProduct(12, new ItemStack(ItemRegister.DA_SUAN.get()), 18, ShopCategory.VEGETABLE),
            new ShopProduct(13, new ItemStack(ItemRegister.SHENG_JIANG.get()), 20, ShopCategory.VEGETABLE),
            new ShopProduct(14, new ItemStack(ItemRegister.YU_TOU.get()), 16, ShopCategory.VEGETABLE),
            new ShopProduct(15, new ItemStack(ItemRegister.LA_JIAO.get()), 14, ShopCategory.VEGETABLE),
            new ShopProduct(16, new ItemStack(ItemRegister.QIE_ZI.get()), 17, ShopCategory.VEGETABLE),
            new ShopProduct(17, new ItemStack(ItemRegister.WO_JU.get()), 13, ShopCategory.VEGETABLE),
            new ShopProduct(18, new ItemStack(ItemRegister.JUE.get()), 11, ShopCategory.VEGETABLE),
            // —— 谷物 ——
            new ShopProduct(19, new ItemStack(ItemRegister.SHUI_DAO.get()), 25, ShopCategory.VEGETABLE),
            new ShopProduct(20, new ItemStack(ItemRegister.XIAO_MAI.get()), 28, ShopCategory.VEGETABLE),
            new ShopProduct(21, new ItemStack(ItemRegister.YAN_MAI.get()), 30, ShopCategory.VEGETABLE),
            new ShopProduct(22, new ItemStack(ItemRegister.GAO_LIANG.get()), 32, ShopCategory.VEGETABLE),
            // —— 果实 ——
            new ShopProduct(23, new ItemStack(ItemRegister.PU_TAO.get()), 25, ShopCategory.FRUIT),
            new ShopProduct(24, new ItemStack(ItemRegister.SHI_LIU.get()), 30, ShopCategory.FRUIT),
            new ShopProduct(25, new ItemStack(ItemRegister.BO_LUO.get()), 35, ShopCategory.FRUIT),
            // —— 草药 ——
            new ShopProduct(26, new ItemStack(ItemRegister.GAN_CAO.get()), 60, ShopCategory.HERB),
            new ShopProduct(27, new ItemStack(ItemRegister.SHA_SHEN.get()), 70, ShopCategory.HERB),
            new ShopProduct(28, new ItemStack(ItemRegister.BAI_TOU_WENG.get()), 75, ShopCategory.HERB),
            new ShopProduct(29, new ItemStack(ItemRegister.DI_HUANG.get()), 80, ShopCategory.HERB),
            new ShopProduct(30, new ItemStack(ItemRegister.MA_SHENG.get()), 85, ShopCategory.HERB),
            new ShopProduct(31, new ItemStack(ItemRegister.BAI_ZHI.get()), 88, ShopCategory.HERB),
            new ShopProduct(32, new ItemStack(ItemRegister.HUANG_JING.get()), 90, ShopCategory.HERB),
            new ShopProduct(33, new ItemStack(ItemRegister.SHUI_XIAN.get()), 95, ShopCategory.HERB),
            new ShopProduct(34, new ItemStack(ItemRegister.HUANG_LIAN.get()), 100, ShopCategory.HERB),
            new ShopProduct(35, new ItemStack(ItemRegister.JIE_GENG.get()), 105, ShopCategory.HERB),
            new ShopProduct(36, new ItemStack(ItemRegister.DAN_SHEN.get()), 110, ShopCategory.HERB),
            new ShopProduct(37, new ItemStack(ItemRegister.HUANG_QI.get()), 120, ShopCategory.HERB),
            new ShopProduct(38, new ItemStack(ItemRegister.DANG_GUI.get()), 130, ShopCategory.HERB),
            new ShopProduct(39, new ItemStack(ItemRegister.SAN_QI.get()), 150, ShopCategory.HERB),
            new ShopProduct(40, new ItemStack(ItemRegister.JI_GUAN.get()), 65, ShopCategory.HERB),
            new ShopProduct(41, new ItemStack(ItemRegister.REN_SHEN.get()), 200, ShopCategory.HERB, 30),
            // —— 装备武器（原版） ——
            new ShopProduct(42, new ItemStack(Items.IRON_SWORD), 400, ShopCategory.EQUIPMENT, 10, RestockCycle.WEEKLY),
            new ShopProduct(43, new ItemStack(Items.DIAMOND_SWORD), 800, ShopCategory.EQUIPMENT, 5, RestockCycle.WEEKLY),
            new ShopProduct(44, new ItemStack(Items.NETHERITE_SWORD), 3000, ShopCategory.EQUIPMENT),
            new ShopProduct(45, new ItemStack(Items.BOW), 500, ShopCategory.EQUIPMENT),
            new ShopProduct(46, new ItemStack(Items.CROSSBOW), 700, ShopCategory.EQUIPMENT),
            new ShopProduct(47, new ItemStack(Items.SHIELD), 350, ShopCategory.EQUIPMENT),
            new ShopProduct(48, new ItemStack(Items.IRON_CHESTPLATE), 600, ShopCategory.EQUIPMENT),
            new ShopProduct(49, new ItemStack(Items.DIAMOND_CHESTPLATE), 1200, ShopCategory.EQUIPMENT, 5),
            new ShopProduct(50, new ItemStack(Items.NETHERITE_CHESTPLATE), 2500, ShopCategory.EQUIPMENT),
            new ShopProduct(51, new ItemStack(Items.TRIDENT), 2000, ShopCategory.EQUIPMENT, 3, RestockCycle.WEEKLY),
            new ShopProduct(52, new ItemStack(Items.MACE), 1800, ShopCategory.EQUIPMENT),
            new ShopProduct(53, new ItemStack(Items.ELYTRA), 5000, ShopCategory.EQUIPMENT, 3),
            // —— 食物（农夫乐事） ——
            new ShopProduct(54, new ItemStack(ModItems.CABBAGE.get()), 15, ShopCategory.FOOD),
            new ShopProduct(55, new ItemStack(ModItems.TOMATO.get()), 18, ShopCategory.FOOD),
            new ShopProduct(56, new ItemStack(ModItems.ONION.get()), 20, ShopCategory.FOOD),
            new ShopProduct(57, new ItemStack(ModItems.RICE.get()), 12, ShopCategory.FOOD),
            new ShopProduct(58, new ItemStack(ModItems.FRIED_EGG.get()), 25, ShopCategory.FOOD),
            new ShopProduct(59, new ItemStack(ModItems.HOT_COCOA.get()), 40, ShopCategory.FOOD),
            new ShopProduct(60, new ItemStack(ModItems.APPLE_CIDER.get()), 35, ShopCategory.FOOD),
            new ShopProduct(61, new ItemStack(ModItems.SWEET_BERRY_COOKIE.get()), 22, ShopCategory.FOOD),
            new ShopProduct(62, new ItemStack(ModItems.HONEY_COOKIE.get()), 25, ShopCategory.FOOD),
            new ShopProduct(63, new ItemStack(ModItems.MELON_POPSICLE.get()), 28, ShopCategory.FOOD),
            new ShopProduct(64, new ItemStack(ModItems.APPLE_PIE.get()), 120, ShopCategory.FOOD),
            new ShopProduct(65, new ItemStack(ModItems.CHOCOLATE_PIE.get()), 150, ShopCategory.FOOD),
            new ShopProduct(66, new ItemStack(ModItems.SWEET_BERRY_CHEESECAKE.get()), 130, ShopCategory.FOOD),
            new ShopProduct(67, new ItemStack(ModItems.HAMBURGER.get()), 80, ShopCategory.FOOD),
            new ShopProduct(68, new ItemStack(ModItems.EGG_SANDWICH.get()), 55, ShopCategory.FOOD),
            new ShopProduct(69, new ItemStack(ModItems.DUMPLINGS.get()), 60, ShopCategory.FOOD),
            new ShopProduct(70, new ItemStack(ModItems.BEEF_STEW.get()), 95, ShopCategory.FOOD),
            new ShopProduct(71, new ItemStack(ModItems.CHICKEN_SOUP.get()), 85, ShopCategory.FOOD),
            new ShopProduct(72, new ItemStack(ModItems.FRIED_RICE.get()), 75, ShopCategory.FOOD),
            new ShopProduct(73, new ItemStack(ModItems.NOODLE_SOUP.get()), 88, ShopCategory.FOOD),
            new ShopProduct(74, new ItemStack(ModItems.BACON.get()), 45, ShopCategory.FOOD),
            new ShopProduct(75, new ItemStack(ModItems.SMOKED_HAM.get()), 85, ShopCategory.FOOD),
            new ShopProduct(76, new ItemStack(ModItems.STUFFED_POTATO.get()), 50, ShopCategory.FOOD),
            new ShopProduct(77, new ItemStack(ModItems.SALMON_ROLL.get()), 90, ShopCategory.FOOD),
            new ShopProduct(78, new ItemStack(ModItems.FISH_STEW.get()), 90, ShopCategory.FOOD),
            new ShopProduct(79, new ItemStack(ModItems.PUMPKIN_SOUP.get()), 65, ShopCategory.FOOD),
            new ShopProduct(80, new ItemStack(ModItems.ROAST_CHICKEN.get()), 200, ShopCategory.FOOD, 20, RestockCycle.WEEKLY),
            new ShopProduct(81, new ItemStack(ModItems.SHEPHERDS_PIE.get()), 180, ShopCategory.FOOD),
            new ShopProduct(82, new ItemStack(ModItems.HONEY_GLAZED_HAM.get()), 300, ShopCategory.FOOD, 10, RestockCycle.WEEKLY),
            new ShopProduct(83, new ItemStack(ModItems.STEAK_AND_POTATOES.get()), 120, ShopCategory.FOOD),
            // —— 售罄演示（库存 0） ——
            new ShopProduct(84, new ItemStack(Items.DIAMOND_HELMET), 800, ShopCategory.EQUIPMENT, 0),
            new ShopProduct(85, new ItemStack(ModItems.PUMPKIN_PIE_SLICE.get()), 30, ShopCategory.FOOD, 0)
    );

    private ShopData() {
    }

    @Nullable
    public static ShopProduct getById(int id) {
        for (var product : PRODUCTS) {
            if (product.id() == id) {
                return product;
            }
        }
        return null;
    }
}
