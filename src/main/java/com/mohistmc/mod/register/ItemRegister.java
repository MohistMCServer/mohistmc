package com.mohistmc.mod.register;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.item.BaiLuoBoItem;
import com.mohistmc.mod.item.BoCaiItem;
import com.mohistmc.mod.item.BoLuoItem;
import com.mohistmc.mod.item.DaSuanItem;
import com.mohistmc.mod.item.FanShuItem;
import com.mohistmc.mod.item.HuaShengItem;
import com.mohistmc.mod.item.HuangGuaItem;
import com.mohistmc.mod.item.JuanXinCaiItem;
import com.mohistmc.mod.item.JueItem;
import com.mohistmc.mod.item.LaJiaoItem;
import com.mohistmc.mod.item.LianOuItem;
import com.mohistmc.mod.item.PuTaoItem;
import com.mohistmc.mod.item.QieZiItem;
import com.mohistmc.mod.item.QinCaiItem;
import com.mohistmc.mod.item.ShengCaiItem;
import com.mohistmc.mod.item.ShengJiangItem;
import com.mohistmc.mod.item.ShiLiuItem;
import com.mohistmc.mod.item.WoJuItem;
import com.mohistmc.mod.item.XiangCaiItem;
import com.mohistmc.mod.item.YuMiItem;
import com.mohistmc.mod.item.YuTouItem;
import com.mohistmc.mod.item.ZiSuYeItem;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * @author Mgazul
 * @date 2026/1/6 19:24
 */
public class ItemRegister {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MohistMC.MODID);

    public static final Set<Supplier<Item>> ALL_ITEMS = new LinkedHashSet<>();

    public static final DeferredItem<Item> LOGO = registerItem("logo", Item::new, Item.Properties::new);
    // 谷物类作物
    public static final DeferredItem<Item> FAN_SHU = registerItem("fan_shu", FanShuItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> YU_MI = registerItem("yu_mi", YuMiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 坚果/调料类作物
    public static final DeferredItem<Item> HUA_SHENG = registerItem("hua_sheng", HuaShengItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> LA_JIAO = registerItem("la_jiao", LaJiaoItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 独立蔬菜类作物
    public static final DeferredItem<Item> BO_CAI = registerItem("bo_cai", BoCaiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 蔬菜类作物 - 地上部分
    public static final DeferredItem<Item> XIANG_CAI = registerItem("xiang_cai", XiangCaiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> SHENG_CAI = registerItem("sheng_cai", ShengCaiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> QIN_CAI = registerItem("qin_cai", QinCaiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> JUAN_XIN_CAI = registerItem("juan_xin_cai", JuanXinCaiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> ZI_SU_YE = registerItem("zi_su_ye", ZiSuYeItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 蔬菜类作物 - 根茎类
    public static final DeferredItem<Item> HUANG_GUA = registerItem("huang_gua", HuangGuaItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> LIAN_OU = registerItem("lian_ou", LianOuItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> BAI_LUO_BO = registerItem("bai_luo_bo", BaiLuoBoItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> DA_SUAN = registerItem("da_suan", DaSuanItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> SHENG_JIANG = registerItem("sheng_jiang", ShengJiangItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> YU_TOU = registerItem("yu_tou", YuTouItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 果实类作物
    public static final DeferredItem<Item> PU_TAO = registerItem("pu_tao", PuTaoItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> SHI_LIU = registerItem("shi_liu", ShiLiuItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> QIE_ZI = registerItem("qie_zi", QieZiItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> BO_LUO = registerItem("bo_luo", BoLuoItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> WO_JU = registerItem("wo_ju", WoJuItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<Item> JUE = registerItem("jue", JueItem::new, () -> new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredItem<Item> SHUI_DAO = registerItem("shui_dao", Item::new, Item.Properties::new);
    public static final DeferredItem<Item> XIAO_MAI = registerItem("xiao_mai", Item::new, Item.Properties::new);
    public static final DeferredItem<Item> YAN_MAI = registerItem("yan_mai", Item::new, Item.Properties::new);
    public static final DeferredItem<Item> GAO_LIANG = registerItem("gao_liang", Item::new, Item.Properties::new);

    // 草药
    public static DeferredItem<BlockItem> DI_HUANG = cao_yao(BlockRegister.DI_HUANG);
    public static DeferredItem<BlockItem> SHA_SHEN = cao_yao(BlockRegister.SHA_SHEN);
    public static DeferredItem<BlockItem> GAN_CAO = cao_yao(BlockRegister.GAN_CAO);
    public static DeferredItem<BlockItem> REN_SHEN = cao_yao(BlockRegister.REN_SHEN);
    public static DeferredItem<BlockItem> JI_GUAN = cao_yao(BlockRegister.JI_GUAN);
    public static DeferredItem<BlockItem> HUANG_JING = cao_yao(BlockRegister.HUANG_JING);
    public static DeferredItem<BlockItem> BAI_TOU_WENG = cao_yao(BlockRegister.BAI_TOU_WENG);
    public static DeferredItem<BlockItem> DAN_SHEN = cao_yao(BlockRegister.DAN_SHEN);
    public static DeferredItem<BlockItem> SAN_QI = cao_yao(BlockRegister.SAN_QI);
    public static DeferredItem<BlockItem> HUANG_LIAN = cao_yao(BlockRegister.HUANG_LIAN);
    public static DeferredItem<BlockItem> HUANG_QI = cao_yao(BlockRegister.HUANG_QI);
    public static DeferredItem<BlockItem> MA_SHENG = cao_yao(BlockRegister.MA_SHENG);
    public static DeferredItem<BlockItem> SHUI_XIAN = cao_yao(BlockRegister.SHUI_XIAN);
    public static DeferredItem<BlockItem> DANG_GUI = cao_yao(BlockRegister.DANG_GUI);
    public static DeferredItem<BlockItem> BAI_ZHI = cao_yao(BlockRegister.BAI_ZHI);
    public static DeferredItem<BlockItem> JIE_GENG = cao_yao(BlockRegister.JIE_GENG);

    public static DeferredItem<BlockItem> TEST_BLOCK_ITEM = cao_yao(BlockRegister.TEST_BLOCK);
    public static DeferredItem<BlockItem> VENDING_MACHINE_BLOCK_ITEM = cao_yao(BlockRegister.VENDING_MACHINE_BLOCK);
    public static DeferredItem<BlockItem> YU_LOU = cao_yao(BlockRegister.YU_LOU);

    private static DeferredItem<Item> registerItem(String name, java.util.function.Function<Item.Properties, Item> factory, java.util.function.Supplier<Item.Properties> properties) {
        DeferredItem<Item> item = ITEMS.registerItem(name, factory, properties);
        ALL_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<BlockItem> cao_yao(DeferredBlock<Block> block) {
        DeferredItem<BlockItem> item = ITEMS.registerSimpleBlockItem(block.getId().getPath(), block, Item.Properties::new);
        ALL_ITEMS.add((Supplier<Item>) (Supplier<?>) item);
        return item;
    }
}
