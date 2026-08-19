package com.mohistmc.mod.module.farmersdelight.common.crafting;

import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeSerializers;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jspecify.annotations.Nullable;

public class CookingPotRecipe implements Recipe<RecipeInput>
{
	public static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Codec.STRING.optionalFieldOf("group", "").forGetter(CookingPotRecipe::group),
			Ingredient.CODEC.listOf(1, CookingPotRecipe.INPUT_SLOTS).fieldOf("ingredients").xmap(ingredients -> {
				NonNullList<Ingredient> nonNullList = NonNullList.create();
				nonNullList.addAll(ingredients);
				return nonNullList;
			}, ingredients -> ingredients).forGetter(CookingPotRecipe::getIngredients),
			ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.output),
			ItemStackTemplate.CODEC.optionalFieldOf("container").forGetter(CookingPotRecipe::getContainerOverride),
			Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(CookingPotRecipe::getExperience),
			Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(CookingPotRecipe::getCookTime)
	).apply(inst, CookingPotRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC =
			StreamCodec.of(CodecHelpers::toNetwork, CodecHelpers::fromNetwork);

	public static final int INPUT_SLOTS = 6;

	private final String group;
	private final NonNullList<Ingredient> inputItems;
	private final ItemStackTemplate output;
	private final @Nullable ItemStackTemplate container;
	private final float experience;
	private final int cookTime;

	public CookingPotRecipe(String group, NonNullList<Ingredient> inputItems, ItemStackTemplate output, Optional<ItemStackTemplate> container, float experience, int cookTime) {
		this.group = group;
		this.inputItems = inputItems;
		this.output = output;
		this.container = container.orElse(null);
		this.experience = experience;
		this.cookTime = cookTime;
	}

	public String getGroup() {
		return this.group;
	}

	@Override
	public String group() {
		return this.group;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.inputItems;
	}

	public ItemStack getOutputContainer() {
		return this.container != null ? this.container.create() : ItemStack.EMPTY;
	}

	public Optional<ItemStackTemplate> getContainerOverride() {
		return Optional.ofNullable(this.container);
	}

	@Override
	public ItemStack assemble(RecipeInput inv) {
		return this.output.create();
	}

	public float getExperience() {
		return this.experience;
	}

	public int getCookTime() {
		return this.cookTime;
	}

	@Override
	public boolean matches(RecipeInput inv, Level level) {
		List<ItemStack> inputs = new java.util.ArrayList<>();
		int i = 0;

		for (int j = 0; j < INPUT_SLOTS; ++j) {
			ItemStack itemstack = inv.getItem(j);
			if (!itemstack.isEmpty()) {
				++i;
				inputs.add(itemstack);
			}
		}
		return i == this.inputItems.size() && RecipeMatcher.findMatches(inputs, this.inputItems) != null;
	}

	@Override
	public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
		return ModRecipeSerializers.COOKING.get();
	}

	@Override
	public RecipeType<? extends Recipe<RecipeInput>> getType() {
		return ModRecipeTypes.COOKING.get();
	}

	@Override
	public boolean showNotification() {
		return true;
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.create(this.inputItems);
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(new ShapelessCraftingRecipeDisplay(
				this.inputItems.stream().map(Ingredient::display).toList(),
				new SlotDisplay.ItemStackSlotDisplay(this.output),
				new SlotDisplay.ItemSlotDisplay(ModItems.COOKING_POT.get())
		));
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CAMPFIRE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CookingPotRecipe that = (CookingPotRecipe) o;

		if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
		if (getCookTime() != that.getCookTime()) return false;
		if (!getGroup().equals(that.getGroup())) return false;
		if (!inputItems.equals(that.inputItems)) return false;
		if (!output.equals(that.output)) return false;
		return container.equals(that.container);
	}

	@Override
	public int hashCode() {
		int result = getGroup().hashCode();
		result = 31 * result + inputItems.hashCode();
		result = 31 * result + output.hashCode();
		result = 31 * result + container.hashCode();
		result = 31 * result + (getExperience() != 0.0f ? Float.floatToIntBits(getExperience()) : 0);
		result = 31 * result + getCookTime();
		return result;
	}

	private static class CodecHelpers
	{
		private static CookingPotRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String group = buffer.readUtf();
			int i = buffer.readVarInt();
			NonNullList<Ingredient> inputItems = NonNullList.create();
			for (int j = 0; j < i; ++j) {
				readIngredient(buffer).ifPresent(inputItems::add);
			}
			ItemStackTemplate output = ItemStackTemplate.STREAM_CODEC.decode(buffer);
			Optional<ItemStackTemplate> container = ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(buffer);
			float experience = buffer.readFloat();
			int cookTime = buffer.readVarInt();
			return new CookingPotRecipe(group, inputItems, output, container, experience, cookTime);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, CookingPotRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeVarInt(recipe.inputItems.size());
			for (Ingredient ingredient : recipe.inputItems) {
				writeIngredient(buffer, ingredient);
			}
			ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
			ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode(buffer, Optional.ofNullable(recipe.container));
			buffer.writeFloat(recipe.experience);
			buffer.writeVarInt(recipe.cookTime);
		}

		private static Optional<Ingredient> readIngredient(RegistryFriendlyByteBuf buffer) {
			int size = buffer.readVarInt();
			if (size <= 0) {
				return Optional.empty();
			}
			List<ItemStack> stacks = new ArrayList<>(size);
			for (int j = 0; j < size; j++) {
				ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
				if (!stack.isEmpty()) {
					stacks.add(stack);
				}
			}
			return stacks.isEmpty() ? Optional.empty() : Optional.of(Ingredient.of(stacks.stream().map(ItemStack::getItem)));
		}

		private static void writeIngredient(RegistryFriendlyByteBuf buffer, Ingredient ingredient) {
			List<ItemStack> stacks = ingredient.items().map(Holder::value).map(ItemStack::new).toList();
			buffer.writeVarInt(stacks.size());
			for (ItemStack stack : stacks) {
				ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, stack);
			}
		}
	}
}
