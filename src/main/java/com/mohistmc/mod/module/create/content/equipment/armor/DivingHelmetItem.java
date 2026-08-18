package com.mohistmc.mod.module.create.content.equipment.armor;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.AllAdvancements;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

import static com.zurrtum.create.Create.MOD_ID;

@EventBusSubscriber
public class DivingHelmetItem extends Item {
    public static final EquipmentSlot SLOT = EquipmentSlot.HEAD;
    public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
        Identifier.fromNamespaceAndPath(
            MOD_ID,
            "netherite_diving_mining_speed"
    ),
        4,
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );

    public DivingHelmetItem(Properties settings) {
        super(settings);
    }

    public static ItemAttributeModifiers createAttributeModifiers(ArmorMaterial material) {
        return new ItemAttributeModifiers(ImmutableList.<ItemAttributeModifiers.Entry>builder()
            .addAll(material.createAttributes(ArmorType.HELMET).modifiers()).add(new ItemAttributeModifiers.Entry(
                Attributes.SUBMERGED_MINING_SPEED,
                SPEED_MODIFIER,
                EquipmentSlotGroup.HEAD
            )).build());
    }

    public static ItemStack getWornItem(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = livingEntity.getItemBySlot(SLOT);
        if (!(stack.getItem() instanceof DivingHelmetItem)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @SubscribeEvent
    public static void breatheUnderwater(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        ItemStack helmet = getWornItem(entity);
        if (helmet.isEmpty()) {
            return;
        }
        if (helmet.canBeHurtBy(level.damageSources().lava())) {
            return;
        }

        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(entity);
        if (backtanks.isEmpty()) {
            return;
        }
        if (entity instanceof ServerPlayer sp) AllAdvancements.DIVING_SUIT_LAVA.trigger(sp);
        if (backtanks.stream().allMatch(backtank -> backtank.canBeHurtBy(level.damageSources().lava()))) {
            return;
        }

        if (level.getGameTime() % 20 == 0) {
            BacktankUtil.consumeAir(entity, backtanks.getFirst(), 1);
        }
        if (entity instanceof ServerPlayer sp) AllAdvancements.DIVING_SUIT.trigger(sp);

        event.setCanBreathe(true);
        event.setRefillAirAmount(entity.getMaxAirSupply());
    }
}
