# 邮箱模块（Mail）

管理员发放制邮件系统：文本 + 可选物品附件，支持离线收信；玩家侧 `/mail` 全屏邮箱界面（邮件列表 / 领取所有 / 清空已读）。

## 功能总览

| 功能 | 说明 |
|---|---|
| 管理员发信 | `/mail send <玩家> <消息...>`（权限同 `/give`，控制台可用）；发送者手持物品时整堆自动作为附件并消耗（防无限复制），空手/控制台为纯文本 |
| 离线收信 | 邮件数据挂在**主世界 ServerLevel Attachment**（`level_mailbox`）上，随世界存档落盘——收件人离线也能收到，上线后登录提醒 |
| 邮件上限 | 每人 50 封（`MailboxData.MAX_MAILS_PER_PLAYER`），满则拒信 |
| 邮箱界面 | 全屏自定义 Screen：邮件卡片列表（发送者/时间/正文预览/附件图标/状态条）+ 右侧操作列 |
| 领取 | 单封「领取」按钮（all-or-nothing：背包放不下整封拒绝）；「领取所有」按封尝试，放不下的跳过并计数 |
| 清空已读 | 只删「已读且已领取」的邮件，删除前弹确认弹窗 |
| 已读语义 | `/mail` 打开时服务端一次性把全部未读标记为已读（无逐封往返） |
| 登录提醒 | 上线时有未读邮件 → 热键栏提示「你有 N 封未读邮件」 |
| 防作弊 | 领取归属按请求者自己的桶校验（防跨玩家）；背包校验复用商店 36 格主背包算法；附件领取后清空防重复领 |

## 使用入口

```
/mail                                    # 所有玩家：打开邮箱界面
/mail send <玩家> <消息...>              # 管理员发信（手持物品自动附加，控制台为纯文本）
```

- 发信给**在线**玩家：即时系统消息提醒「你收到了一封来自 %s 的邮件」
- 发信给**离线**玩家：`server.services().nameToIdCache()` 解析 UUID（MC 26.2 已无 GameProfileCache）
- 纯文本邮件创建即「已领取」（无可领），打开阅读后即可被「清空已读」删掉

## 模块结构

```
src/main/java/com/mohistmc/mod/module/mail/
├── Mail.java                        # 模块入口（注册 Attachment、命令、登录未读提醒）
├── common/
│   ├── MailEntry.java               # 单封邮件（id/发送者/时间/文本/附件/已读/已领取）+ 网络编解码
│   ├── MailboxData.java             # ServerLevel Attachment 值类（Map<UUID, List<MailEntry>> + nextId）
│   ├── Mailbox.java                 # 服务端工具（开箱/发信/单封领取/全部领取/清空已读/未读数）
│   ├── attachment/ModAttachments.java  # 注册 level_mailbox Attachment
│   ├── command/ModCommands.java     # /mail、/mail send
│   └── network/
│       ├── MailNetworking.java      # 网络注册 + 服务端权威校验
│       └── payload/                 # OpenMailbox / ClaimMail / ClaimAllMail / ClearRead / MailboxSync / UnreadNotification
└── client/
    ├── network/MailClientPayloadHandler.java  # 客户端收包（开屏/刷新/登录提醒）
    └── gui/
        ├── MailScreen.java          # 邮箱主界面（列表 + 右侧操作列 + 详情/确认 Modal）
        └── MailCard.java            # 邮件卡片（状态条/发送者/时间/正文预览/附件图标/领取按钮）
```

改动：`MohistMC.java`（第 60 行 `new Mail(...)`）、`assets/mohistmc/lang/en_us.json`（+31 key）。

## 数据模型与存储

- **载体**：`server.overworld().getData(ModAttachments.LEVEL_MAILBOX)` → `MailboxData`（`ValueIOSerializable`）
- **自动落盘**：`LevelAttachmentsSavedData.isDirty()` 恒 true → 就地修改返回值即持久化，无需手动 setData
- **结构**：按收件人 UUID 分桶 `Map<UUID, List<MailEntry>>`；全局 `nextId` 保证邮件 id 全服务器唯一（领取按 id 定位）
- **序列化**：`output.childrenList("Boxes")` 逐字段写；附件用 `ItemStack.OPTIONAL_CODEC.listOf()`（注意：`ItemStack.LIST_CODEC` 不存在）；读取逐封 try/catch——附件因 mod 卸载无法解码时该封跳过打日志，不炸整个邮箱
- **注意**：只挂主世界，勿挂其他维度（每维度独立 SavedData）

## 网络协议（版本串 "1"）

| Payload | 方向 | 字段 |
|---|---|---|
| `OpenMailboxPayload` | S→C | 邮件列表（`MailEntry.LIST_STREAM_CODEC`） |
| `ClaimMailPayload` | C→S | `long mailId` |
| `ClaimAllMailPayload` | C→S | 无（unit） |
| `ClearReadPayload` | C→S | 无（unit） |
| `MailboxSyncPayload` | S→C | `success / message / claimed / skipped / 全量列表` |
| `UnreadNotificationPayload` | S→C | `int unread` |

`MailEntry` 编解码：字段级 composite，附件用已验证的 `ItemStack.OPTIONAL_LIST_STREAM_CODEC`。

## 关键业务决策

- **附件来源**：命令参数无法传物品 → 发送者（OP）主手持物品整个堆叠 `copy()` 作附件；**发信成功后才 `shrink` 消耗手持**（失败不吞物品）
- **领取语义**：单封 all-or-nothing；领取所有按封跳过（部分领取粒度 = 按封，腾空间后可重试）
- **背包校验**：`getNonEquipmentItems()` 36 格主背包模拟累加（空槽 + 可堆叠槽），与商店 handleBuy 同款约定（防盔甲槽 add 失败丢物）
- **清空已读**：`read && claimed` 才删；桶删空后 `remove` 掉防幽灵 key

## 语言文件

文案集中在 `assets/mohistmc/lang/en_us.json`：`gui.mohistmc.mail.*`（界面）、`command.mohistmc.mail.*`（命令/提醒），共 31 个 key。

## 已知限制

- 在线服发给**从未登录过**的名字 → `nameToIdCache` 回退离线推导 UUID，产生幻影邮箱（MC 26.2 解析机制，无法区分命中与回退；离线服玩家上线后同名即命中，完全可用）
- 附件物品因 mod 卸载无法解码 → 该封整体跳过（逐封容错，不影响其余邮件）

## 当前进度

- [x] common 层（MailEntry / MailboxData / ModAttachments / Mailbox）
- [x] 命令（/mail、/mail send，含离线解析与手持附件消耗）
- [x] 网络层（6 payload + MailNetworking 服务端校验）
- [x] 客户端 GUI（MailScreen / MailCard / MailClientPayloadHandler）
- [x] 接线（Mail.java、MohistMC.java、en_us.json 31 key）
- [x] `./gradlew compileJava` + `./gradlew build` 通过（2026-08-05）
- [ ] **手工测试（runClient）**：
  1. `/mail` 空箱 → 「暂无邮件」
  2. `/mail send <自己> 你好`（纯文本）→ 收到提醒、界面出现邮件
  3. 手持钻石 `/mail send <自己> 测试附件` → 手持被消耗、邮件带附件图标
  4. `/mail`：单封「领取」、右侧「领取所有」（含 skipped 计数）、背包塞满验证拒绝/跳过
  5. 「清空已读」：确认弹窗 → 只删已读已领取的
  6. 控制台 `/mail send`、发给离线玩家、重新登录未读提醒
  7. 50 封上限拒绝、邮件详情 Modal（点击卡片）

## 验证命令

```bash
./gradlew build        # 编译（Git Bash；cmd 用 gradlew.bat）
./gradlew runClient    # 运行（run/ 目录已有存档）
```
