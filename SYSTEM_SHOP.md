# 系统商店模块（System Shop）

基于自动售货机（`vending_machine`）的完整商店系统：数字余额货币、分类/搜索、库存与自动补货、防伪造购买验证。

## 功能总览

| 功能 | 说明 |
|---|---|
| 货币系统 | 数字余额（Attachment 持久化到玩家存档），货币类型可切换（图标 + 名称联动） |
| 商品目录 | 84 个商品（作物/谷物/草药/装备/农夫乐事食物），硬编码于 `ShopData` |
| 商店界面 | 全屏现代风格：左侧分类 Tab 竖列 + 商品网格（正方形徽章格子）+ 搜索框 + 右侧操作面板 |
| 购买流程 | 选中商品 → 详情面板调数量（`[-]` 输入框 `[+]`）→ 确认弹窗 → 服务端校验扣款发货 |
| 安全验证 | 必须右键售货机打开商店且仍在售货机 8 格内才能购买（防伪造数据包） |
| 库存机制 | 商品可设库存上限（默认无限），售罄显示「售罄」并禁用购买 |
| 自动补货 | 可选：每日 04:00 / 每周一 04:00 重置库存（现实时间），右上角显示秒级倒计时 |
| 余额命令 | `/money give <玩家> <数量>`（管理员，查余额见 ESC 界面与商店） |
| 背包校验 | 背包放不下直接拦截提示，不扣款不掉物 |

## 使用入口

放置**自动售货机**（创造标签页 `mohistmc_tab`）→ 右键打开商店。

- **ESC 界面**：金币行显示当前余额（打开时自动请求同步）
- **商店界面**：右上角商店名 + 补货倒计时、余额卡片

## 模块结构

```
src/main/java/com/mohistmc/mod/module/shop/
├── Shop.java                        # 模块入口（注册 Attachment、命令、会话清理）
├── common/
│   ├── ShopSession.java             # 购买会话（售货机位置登记 + 距离校验）
│   ├── attachment/                  # 余额系统（PlayerBalanceData / ModAttachments / PlayerBalance）
│   ├── command/ModCommands.java     # /money give 命令
│   ├── config/                      # （已移除，货币直接代码指定）
│   ├── data/
│   │   ├── Currency.java            # 当前货币工具（图标贴图 / 显示名）
│   │   ├── CurrencyType.java        # 货币类型（GOLD，绑定 jinbi.png）
│   │   ├── RestockCycle.java        # 补货周期（NONE / DAILY / WEEKLY）
│   │   ├── RestockTimer.java        # 补货倒计时计算与格式化
│   │   ├── ShopCategory.java        # 商品类别（蔬菜/果实/草药/装备/食物）
│   │   ├── ShopData.java            # 商品目录（84 个）
│   │   ├── ShopProduct.java         # 商品条目（id / 物品 / 价格 / 类别 / 库存 / 补货周期）
│   │   └── ShopStock.java           # 服务端库存（惰性补货重置）
│   └── network/
│       ├── ShopNetworking.java      # 网络注册 + 服务端购买校验
│       └── payload/                 # OpenShop / Buy / BuyResult / BalanceRequest / BalanceSync
└── client/
    ├── network/ShopClientPayloadHandler.java  # 客户端收包（开屏/余额/库存刷新）
    └── gui/
        ├── ShopScreen.java          # 商店主界面
        └── ShopCard.java            # 商品格子（图标 + 价格徽章 + 售罄态）
```

组件库新增（`api/gui/`）：

| 组件 | 说明 |
|---|---|
| `GridScrollList` | 多列卡片网格（正方形单元格、内边距、平滑滚动、滚动条拖拽、附加行高） |
| `Badge` | 徽章组件（背景条 + 图标 + 文本，实例/静态两种用法） |

## 配置说明

### 切换货币（代码）

`Currency.java` 中修改：

```java
public static CurrencyType CURRENT = CurrencyType.GOLD; // 默认金币（jinbi.png）
```

新增货币类型：`CurrencyType` 枚举加一项（lang key + 贴图路径 + 贴图边长），并在 `textures/ui/` 提供贴图。

### 商品 / 库存 / 补货

`ShopData.java` 中商品条目：

```java
// 无限库存、不补货（默认）
new ShopProduct(id, stack, price, category)

// 有限库存、不补货
new ShopProduct(id, stack, price, category, stock)

// 有限库存 + 每周一 04:00 补货
new ShopProduct(id, stack, price, category, stock, RestockCycle.WEEKLY)
```

- `stock = -1` 无限；`0` 初始即售罄；`> 0` 有限
- `RestockCycle.DAILY` 每日 04:00 重置；`WEEKLY` 每周一 04:00 重置

### 命令

```
/money give <玩家> <数量>   # 管理员加钱（权限同 /give）
```

## 购买流程与安全

```
右键售货机
  ├─ 服务端：ShopSession.open(玩家, 售货机位置) → 发 OpenShopPayload（含余额）
  └─ 客户端：打开商店界面
选中商品 → 确认购买 → BuyPayload(itemId, quantity)
  → 服务端校验（依序）：
      1. 会话有效且玩家距售货机 ≤ 8 格（防伪造）
      2. 商品存在
      3. 金额足够（long 防溢出）
      4. 库存充足（到期自动补货后判断）
      5. 主背包 36 格可完整容纳（getNonEquipmentItems 遍历）
  → 扣库存、扣款、发物 → BuyResultPayload(成功/失败 + 新余额 + 剩余库存)
```

库存为服务端内存态（重启重置）；补货按**现实时间**（`LocalDateTime`，04:00 为重置时刻），购买时惰性检查跨过重置点即补满。

## 语言文件

所有文案集中在 `assets/mohistmc/lang/en_us.json`（`gui.mohistmc.shop.*`、`command.mohistmc.money.*`、`currency.mohistmc.*`）。

## 验证

```bash
gradlew.bat build      # 编译
gradlew.bat runClient  # 运行
```

手工清单：打开商店（分类/搜索/购买/余额刷新）、售罄锁定、跨 04:00 补货、背包满拦截、ESC 余额显示、`/money give`。
