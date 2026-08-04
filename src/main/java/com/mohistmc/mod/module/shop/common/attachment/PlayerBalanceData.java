package com.mohistmc.mod.module.shop.common.attachment;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * 玩家数字余额的持久化载体（随玩家 .dat 自动存档）
 *
 * @author Mgazul
 * @date 2026/8/5
 */
public final class PlayerBalanceData implements ValueIOSerializable {

    private int balance;

    public PlayerBalanceData() {
        this(0);
    }

    public PlayerBalanceData(int balance) {
        this.balance = balance;
    }

    public int get() {
        return balance;
    }

    public void set(int balance) {
        this.balance = balance;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("balance", balance);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.balance = input.getIntOr("balance", 0);
    }
}
