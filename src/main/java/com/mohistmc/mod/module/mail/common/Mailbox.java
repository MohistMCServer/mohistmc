package com.mohistmc.mod.module.mail.common;

import com.mohistmc.mod.module.mail.common.attachment.ModAttachments;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

/**
 * 邮箱服务端工具（仿 {@code shop.common.attachment.PlayerBalance} 的静态工具类）。
 * <p>数据一律挂在主世界 ServerLevel Attachment 上并就地修改（自动落盘）；
 * 所有业务校验（上限/背包 canFit/防跨玩家）都在服务端完成，客户端只渲染与发起请求。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class Mailbox {

    private Mailbox() {
    }

    public static MailboxData of(MinecraftServer server) {
        return server.overworld().getData(ModAttachments.LEVEL_MAILBOX);
    }

    /** 当前玩家邮箱中新→旧排序的副本（不含空桶） */
    public static List<MailEntry> listFor(MailboxData data, UUID player) {
        List<MailEntry> bucket = data.get(player);
        if (bucket == null) {
            return List.of();
        }
        List<MailEntry> sorted = new ArrayList<>(bucket);
        sorted.sort(Comparator.comparingLong(MailEntry::getTimestamp).reversed());
        return sorted;
    }

    /**
     * 解析收件人：在线优先 {@code getPlayerByName}，否则走 {@code nameToIdCache}
     * （MC 26.2 已无 GameProfileCache）；返回 null 表示未找到（支持离线收件人）。
     */
    public static UUID resolveRecipient(MinecraftServer server, String name) {
        ServerPlayer online = server.getPlayerList().getPlayerByName(name);
        if (online != null) {
            return online.getUUID();
        }
        Optional<NameAndId> resolved = server.services().nameToIdCache().get(name);
        return resolved.map(NameAndId::id).orElse(null);
    }

    /** 标记单封已读（仅请求者自己桶内的邮件；选中查看时由客户端发起） */
    public static void markRead(ServerPlayer player, long mailId) {
        MailboxData data = of(player.level().getServer());
        List<MailEntry> bucket = data.get(player.getUUID());
        if (bucket == null) {
            return;
        }
        for (MailEntry mail : bucket) {
            if (mail.getId() == mailId) {
                mail.setRead(true);
                return;
            }
        }
    }

    /**
     * 发信（无上限）；返回 null 表示成功，失败时返回文案 key（预留扩展，当前无失败场景）。
     * <p>发送者名完全由调用方自定义——玩家名、控制台名或 NPC/系统名均可，
     * 收件人传已解析的 UUID（在线/离线皆可）。NPC 等系统发信直接调用本方法。
     * 附件列表由调用方传入（命令层已从手持堆叠 copy）；纯文本邮件（附件为空）创建时即已领取。
     */
    public static String send(MinecraftServer server, String senderName, UUID recipient,
                              String text, List<ItemStack> attachments) {
        MailboxData data = of(server);
        List<MailEntry> bucket = data.getOrCreate(recipient);
        boolean hasAttachments = attachments != null && !attachments.isEmpty();
        bucket.add(new MailEntry(
                data.nextMailId(),
                senderName,
                System.currentTimeMillis(),
                text,
                hasAttachments ? attachments : List.of(),
                false,
                !hasAttachments));
        return null;
    }

    /** 便捷重载：纯文本邮件（无附件），NPC/系统发信可直接使用 */
    public static String send(MinecraftServer server, String senderName, UUID recipient, String text) {
        return send(server, senderName, recipient, text, List.of());
    }

    /** 所有玩家 UUID：在线玩家 + playerdata 目录中曾登录过的离线玩家（去重，群发用） */
    public static List<UUID> allPlayerUuids(MinecraftServer server) {
        LinkedHashSet<UUID> uuids = new LinkedHashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            uuids.add(player.getUUID());
        }
        Path playerDataDir = server.getWorldPath(LevelResource.PLAYER_DATA_DIR);
        if (Files.isDirectory(playerDataDir)) {
            try (var stream = Files.list(playerDataDir)) {
                stream.forEach(path -> {
                    String fileName = path.getFileName().toString();
                    if (fileName.endsWith(".dat")) {
                        try {
                            uuids.add(UUID.fromString(fileName.substring(0, fileName.length() - 4)));
                        } catch (IllegalArgumentException ignored) {
                            // 非 UUID 命名的文件（如本地化缓存），跳过
                        }
                    }
                });
            } catch (IOException ignored) {
                // 目录不可读时仅在线玩家
            }
        }
        return new ArrayList<>(uuids);
    }

    /**
     * 领取单封（all-or-nothing：背包放不下整封拒绝）；返回 null 表示成功，否则为失败文案 key。
     * mailId 必须在请求者自己的桶中（防跨玩家领取）。
     */
    public static String claimSingle(ServerPlayer player, long mailId) {
        MailboxData data = of(player.level().getServer());
        List<MailEntry> bucket = data.get(player.getUUID());
        if (bucket == null) {
            return "gui.mohistmc.mail.fail.invalid";
        }
        for (MailEntry mail : bucket) {
            if (mail.getId() != mailId) {
                continue;
            }
            if (mail.isClaimed()) {
                return "gui.mohistmc.mail.fail.claimed";
            }
            if (!canFitAll(player.getInventory(), mail.getAttachments())) {
                return "gui.mohistmc.mail.fail.inventory";
            }
            // canFitAll 已保证可完整放入（主背包 36 槽）；失败分支仅作防御
            for (ItemStack stack : mail.getAttachments()) {
                if (!player.getInventory().add(stack.copy())) {
                    return "gui.mohistmc.mail.fail.inventory";
                }
            }
            // 只置已领取，附件保留供界面展示（防重复领取靠 claimed 标记 + 上面的校验）
            mail.setClaimed(true);
            return null;
        }
        return "gui.mohistmc.mail.fail.invalid";
    }

    /**
     * 领取所有可领邮件：按封尝试，能领的领、不能的跳过（背包满的部分领取粒度 = 按封，腾出空间后可重试）。
     *
     * @return [成功数, 跳过数]
     */
    public static int[] claimAll(ServerPlayer player) {
        MailboxData data = of(player.level().getServer());
        List<MailEntry> bucket = data.get(player.getUUID());
        if (bucket == null) {
            return new int[]{0, 0};
        }
        int claimed = 0;
        int skipped = 0;
        for (MailEntry mail : bucket) {
            if (mail.isClaimed()) {
                continue;
            }
            if (!canFitAll(player.getInventory(), mail.getAttachments())) {
                skipped++;
                continue;
            }
            // canFitAll 已保证可完整放入；add 失败仅作防御，跳过本封
            boolean added = true;
            for (ItemStack stack : mail.getAttachments()) {
                if (!player.getInventory().add(stack.copy())) {
                    added = false;
                    break;
                }
            }
            if (!added) {
                skipped++;
                continue;
            }
            // 只置已领取，附件保留供界面展示
            mail.setClaimed(true);
            claimed++;
        }
        return new int[]{claimed, skipped};
    }

    /** 清空已读且已领取的邮件；返回清空后新→旧排序的列表副本 */
    public static List<MailEntry> clearRead(ServerPlayer player) {
        MailboxData data = of(player.level().getServer());
        UUID uuid = player.getUUID();
        List<MailEntry> bucket = data.get(uuid);
        if (bucket == null) {
            return List.of();
        }
        bucket.removeIf(mail -> mail.isRead() && mail.isClaimed());
        data.removeIfEmpty(uuid);
        return listFor(data, uuid);
    }

    /** 未读邮件数（登录提醒用） */
    public static int unreadCount(MailboxData data, UUID player) {
        List<MailEntry> bucket = data.get(player);
        if (bucket == null) {
            return 0;
        }
        int count = 0;
        for (MailEntry mail : bucket) {
            if (!mail.isRead()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 检查主背包能否完整容纳整批物品（空槽 + 可堆叠槽容量，按物品顺序累加模拟）。
     * 注意：只用主背包槽（getNonEquipmentItems，36 格）——getContainerSize 含盔甲/副手（41 格），
     * 空装备槽会让 add 实际失败导致物品丢失（与商店 handleBuy 同款约定）。
     */
    private static boolean canFitAll(Inventory inventory, List<ItemStack> stacks) {
        List<ItemStack> slots = new ArrayList<>();
        for (ItemStack slot : inventory.getNonEquipmentItems()) {
            slots.add(slot.copy());
        }
        for (ItemStack stack : stacks) {
            ItemStack remaining = stack.copy();
            for (ItemStack slot : slots) {
                if (remaining.isEmpty()) {
                    break;
                }
                if (slot.isEmpty()) {
                    slot.setCount(remaining.getCount());
                    remaining = ItemStack.EMPTY;
                    break;
                }
                if (ItemStack.isSameItemSameComponents(slot, remaining) && slot.getCount() < slot.getMaxStackSize()) {
                    int room = slot.getMaxStackSize() - slot.getCount();
                    int take = Math.min(room, remaining.getCount());
                    slot.grow(take);
                    remaining.shrink(take);
                }
            }
            if (!remaining.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
