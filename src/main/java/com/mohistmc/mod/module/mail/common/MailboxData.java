package com.mohistmc.mod.module.mail.common;

import com.mohistmc.mod.MohistMC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * 邮箱数据的持久化载体（挂在主世界 ServerLevel 的 Attachment 上，随世界存档自动落盘）。
 * <p>按收件人 UUID 分桶：{@code Map<UUID, List<MailEntry>>}，打开邮箱 O(1) 取桶（无数量上限）；
 * 全局 {@code nextId} 保证邮件 id 全服务器唯一（领取按 id 定位）。
 * <p>序列化：childrenList 逐字段写；读取时逐封 try/catch 容错——附件物品因 mod 卸载无法解码时
 * 该封整体跳过并打日志，不影响其余邮件（已知限制，见 {@link #deserialize}）。
 * <p>注意：{@code LevelAttachmentsSavedData.isDirty()} 恒为 true，就地修改 getData() 返回值即自动落盘，
 * 无需手动 setData。
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class MailboxData implements ValueIOSerializable {

    private final Map<UUID, List<MailEntry>> byRecipient = new HashMap<>();
    private long nextId = 1;

    // ======== 桶操作 ========

    public List<MailEntry> getOrCreate(UUID recipient) {
        return byRecipient.computeIfAbsent(recipient, k -> new ArrayList<>());
    }

    public List<MailEntry> get(UUID recipient) {
        return byRecipient.get(recipient);
    }

    /** 移除空桶（清空已读后顺带清理，防幽灵 key） */
    public void removeIfEmpty(UUID recipient) {
        List<MailEntry> bucket = byRecipient.get(recipient);
        if (bucket != null && bucket.isEmpty()) {
            byRecipient.remove(recipient);
        }
    }

    /** 分配全局唯一邮件 id */
    public long nextMailId() {
        return nextId++;
    }

    // ======== 序列化 ========

    @Override
    public void serialize(ValueOutput output) {
        output.putLong("nextId", nextId);
        ValueOutput.ValueOutputList boxes = output.childrenList("Boxes");
        for (Map.Entry<UUID, List<MailEntry>> e : byRecipient.entrySet()) {
            ValueOutput box = boxes.addChild();
            box.putString("uuid", e.getKey().toString());
            ValueOutput.ValueOutputList mails = box.childrenList("Mails");
            for (MailEntry m : e.getValue()) {
                ValueOutput me = mails.addChild();
                me.putLong("id", m.getId());
                me.putString("sender", m.getSenderName());
                me.putLong("time", m.getTimestamp());
                me.putString("text", m.getText());
                me.store("Items", ItemStack.OPTIONAL_CODEC.listOf(), m.getAttachments());
                me.putBoolean("read", m.isRead());
                me.putBoolean("claimed", m.isClaimed());
            }
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        byRecipient.clear();
        nextId = input.getLongOr("nextId", 1);
        for (ValueInput box : input.childrenListOrEmpty("Boxes")) {
            String uuid = box.getStringOr("uuid", "");
            if (uuid.isEmpty()) {
                continue;
            }
            List<MailEntry> list = new ArrayList<>();
            for (ValueInput me : box.childrenListOrEmpty("Mails")) {
                try {
                    MailEntry entry = new MailEntry(
                            me.getLongOr("id", 0),
                            me.getStringOr("sender", "?"),
                            me.getLongOr("time", 0),
                            me.getStringOr("text", ""),
                            new ArrayList<>(me.read("Items", ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of())),
                            me.getBooleanOr("read", false),
                            me.getBooleanOr("claimed", false));
                    list.add(entry);
                    // 防御：id 回退（如旧存档手工编辑）时自愈，保证后续 id 不重复
                    nextId = Math.max(nextId, entry.getId() + 1);
                } catch (Exception ex) {
                    // 单封容错：附件物品因 mod 卸载无法解码等，坏封跳过不炸整个邮箱
                    MohistMC.LOGGER.warn("跳过损坏邮件: {}", ex.toString());
                }
            }
            try {
                byRecipient.put(UUID.fromString(uuid), list);
            } catch (IllegalArgumentException ex) {
                MohistMC.LOGGER.warn("跳过损坏邮箱桶（非法 UUID）: {}", uuid);
            }
        }
    }
}
