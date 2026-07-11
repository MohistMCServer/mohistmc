package com.mohistmc.mod.module.farmersdelight.common.crafting.ingredient;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModIngredientTypes;

/**
 * Ingredient that checks if the given stack can perform a ItemAbility from Forge.
 */
@ParametersAreNonnullByDefault
public class ItemAbilityIngredient implements ICustomIngredient
{
	public static final MapCodec<ItemAbilityIngredient> CODEC = RecordCodecBuilder.mapCodec(inst ->
			inst.group(ItemAbility.CODEC.fieldOf("action").forGetter(ItemAbilityIngredient::getItemAbility)
			).apply(inst, ItemAbilityIngredient::new));

	protected final ItemAbility itemAbility;
	protected Stream<Holder<Item>> itemHolders;

	public ItemAbilityIngredient(ItemAbility itemAbility) {
		this.itemAbility = itemAbility;
	}

	protected void dissolve() {
		if (this.itemHolders == null) {
			itemHolders = BuiltInRegistries.ITEM.stream()
					.filter(item -> item.canPerformAction(new ItemStack(item), itemAbility))
					.map(BuiltInRegistries.ITEM::wrapAsHolder);
		}
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && stack.canPerformAction(itemAbility);
	}

	@Override
	public Stream<Holder<Item>> items() {
		dissolve();
		return itemHolders;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	public ItemAbility getItemAbility() {
		return itemAbility;
	}

	public IngredientType<?> getType() {
		return ModIngredientTypes.ITEM_ABILITY_INGREDIENT.get();
	}
}
