package com.mohistmc.mod.module.farmersdelight.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.block.CookingPotBlock;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.container.CookingPotMenu;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.inventory.CookingPotItemHandler;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.item.component.ItemStackWrapper;
import com.mohistmc.mod.module.farmersdelight.common.registry.*;
import com.mohistmc.mod.module.farmersdelight.common.utility.ItemUtils;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;

import static java.util.Map.entry;

@EventBusSubscriber(modid = FarmersDelight.MODID)
public class CookingPotBlockEntity extends SyncedBlockEntity implements MenuProvider, HeatableBlockEntity, Nameable, RecipeCraftingHolder, Clearable
{
	public static final int MEAL_DISPLAY_SLOT = 6;
	public static final int CONTAINER_SLOT = 7;
	public static final int OUTPUT_SLOT = 8;
	public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;

	public static final Map<Item, Item> INGREDIENT_REMAINDER_OVERRIDES = Map.ofEntries(
		entry(Items.POWDER_SNOW_BUCKET, Items.BUCKET),
		entry(Items.AXOLOTL_BUCKET, Items.BUCKET),
		entry(Items.COD_BUCKET, Items.BUCKET),
		entry(Items.PUFFERFISH_BUCKET, Items.BUCKET),
		entry(Items.SALMON_BUCKET, Items.BUCKET),
		entry(Items.TROPICAL_FISH_BUCKET, Items.BUCKET),
		entry(Items.SUSPICIOUS_STEW, Items.BOWL),
		entry(Items.MUSHROOM_STEW, Items.BOWL),
		entry(Items.RABBIT_STEW, Items.BOWL),
		entry(Items.BEETROOT_SOUP, Items.BOWL),
		entry(Items.POTION, Items.GLASS_BOTTLE),
		entry(Items.SPLASH_POTION, Items.GLASS_BOTTLE),
		entry(Items.LINGERING_POTION, Items.GLASS_BOTTLE),
		entry(Items.EXPERIENCE_BOTTLE, Items.GLASS_BOTTLE)
	);

	private final ItemStacksResourceHandler inventory;
	private final ResourceHandler<ItemResource> inputHandler;
	private final ResourceHandler<ItemResource> outputHandler;

	private int cookTime;
	private int cookTimeTotal;
	private ItemStack mealContainerStack;
	private Component customName;

	protected final ContainerData cookingPotData;
	private final Object2IntOpenHashMap<Identifier> usedRecipeTracker;

	private final RecipeManager.CachedCheck<RecipeInput, CookingPotRecipe> quickCheck;

	public CookingPotBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.COOKING_POT.get(), pos, state);
		this.inventory = createHandler();
		this.inputHandler = new CookingPotItemHandler(inventory, Direction.UP);
		this.outputHandler = new CookingPotItemHandler(inventory, Direction.DOWN);
		this.mealContainerStack = ItemStack.EMPTY;
		this.cookingPotData = createIntArray();
		this.usedRecipeTracker = new Object2IntOpenHashMap<>();
		this.quickCheck = RecipeManager.createCheck(ModRecipeTypes.COOKING.get());
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(
			Capabilities.Item.BLOCK,
			ModBlockEntityTypes.COOKING_POT.get(),
			(be, context) -> {
				if (context == Direction.UP) {
					return be.inputHandler;
				}
				return be.outputHandler;
			}
		);
	}

	public static ItemStack getMealFromItem(ItemStack cookingPotStack) {
		if (!cookingPotStack.is(ModItems.COOKING_POT.get())) {
			return ItemStack.EMPTY;
		}
		return cookingPotStack.getOrDefault(ModDataComponents.MEAL, ItemStackWrapper.EMPTY).getStack();
	}

	public static void takeServingFromItem(ItemStack cookingPotStack) {
		if (!cookingPotStack.is(ModItems.COOKING_POT.get())) {
			return;
		}
		ItemStack mealStack = cookingPotStack.getOrDefault(ModDataComponents.MEAL, ItemStackWrapper.EMPTY).getStack();
		mealStack.shrink(1);
		cookingPotStack.set(ModDataComponents.MEAL, new ItemStackWrapper(mealStack));
	}

	public static ItemStack getContainerFromItem(ItemStack cookingPotStack) {
		if (!cookingPotStack.is(ModItems.COOKING_POT.get())) {
			return ItemStack.EMPTY;
		}
		return cookingPotStack.getOrDefault(ModDataComponents.CONTAINER.get(), ItemStackWrapper.EMPTY).getStack();
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		inventory.deserialize(input.childOrEmpty("Inventory"));
		cookTime = input.getIntOr("CookTime", 0);
		cookTimeTotal = input.getIntOr("CookTimeTotal", 0);
		input.read("Container", ItemStack.OPTIONAL_CODEC).ifPresent(stack -> mealContainerStack = stack);
		input.child("RecipesUsed").ifPresent(recipesInput -> {
			for (String key : recipesInput.keySet()) {
				usedRecipeTracker.put(Identifier.parse(key), recipesInput.getIntOr(key, 0));
			}
		});
	}

	@Override
	public void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("CookTime", cookTime);
		output.putInt("CookTimeTotal", cookTimeTotal);
		output.store("Container", ItemStack.OPTIONAL_CODEC, mealContainerStack);
		inventory.serialize(output.child("Inventory"));
		ValueOutput recipesOutput = output.child("RecipesUsed");
		usedRecipeTracker.forEach((recipeId, craftedAmount) -> recipesOutput.putInt(recipeId.toString(), craftedAmount));
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveWithoutMetadata(registries);
	}

	public ItemStack getAsItem() {
		ItemStack stack = new ItemStack(ModItems.COOKING_POT.get());
		stack.applyComponents(collectComponents());
		return stack;
	}

	public static void cookingTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot) {
		boolean isHeated = cookingPot.isHeated(level, pos);
		boolean didInventoryChange = false;

		if (isHeated && cookingPot.hasInput()) {
			Optional<RecipeHolder<CookingPotRecipe>> recipe = cookingPot.getMatchingRecipe(new RecipeWrapper(IItemHandler.of(cookingPot.inventory)));
			if (recipe.isPresent() && cookingPot.canCook(recipe.get().value())) {
				didInventoryChange = cookingPot.processCooking(recipe.get(), cookingPot);
			} else {
				cookingPot.cookTime = Mth.clamp(cookingPot.cookTime - 2, 0, cookingPot.cookTimeTotal);
			}
		} else if (cookingPot.cookTime > 0) {
			cookingPot.cookTime = Mth.clamp(cookingPot.cookTime - 2, 0, cookingPot.cookTimeTotal);
		}

		ItemStack mealStack = cookingPot.getMeal();
		if (!mealStack.isEmpty()) {
			if (!cookingPot.doesMealHaveContainer(mealStack)) {
				cookingPot.moveMealToOutput();
				didInventoryChange = true;
			} else if (!cookingPot.inventory.getResource(CONTAINER_SLOT).isEmpty()) {
				cookingPot.useStoredContainersOnMeal();
				didInventoryChange = true;
			}
		}

		if (didInventoryChange) {
			cookingPot.inventoryChanged();
		}
	}

	public static void animationTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity cookingPot) {
		if (cookingPot.isHeated(level, pos)) {
			RandomSource random = level.getRandom();
			if (random.nextFloat() < 0.2F) {
				double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
				double y = (double) pos.getY() + 0.7D;
				double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.6D - 0.3D);
				level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0.0D, 0.0D, 0.0D);
			}
			if (random.nextFloat() < 0.05F) {
				double x = (double) pos.getX() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
				double y = (double) pos.getY() + 0.5D;
				double z = (double) pos.getZ() + 0.5D + (random.nextDouble() * 0.4D - 0.2D);
				double motionY = random.nextBoolean() ? 0.015D : 0.005D;
				level.addParticle(ModParticleTypes.STEAM.get(), x, y, z, 0.0D, motionY, 0.0D);
			}
		}
	}

	private Optional<RecipeHolder<CookingPotRecipe>> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
		if (level == null) return Optional.empty();
		if (!hasInput()) return Optional.empty();
		if (level instanceof ServerLevel serverLevel) {
			return quickCheck.getRecipeFor(inventoryWrapper, serverLevel);
		}
		return Optional.empty();
	}

	public ItemStack getContainer() {
		ItemStack mealStack = getMeal();
		if (!mealStack.isEmpty() && !mealContainerStack.isEmpty()) {
			return mealContainerStack;
		}
		Item item = mealStack.getItem();
		ItemStackTemplate remainder = item.getCraftingRemainder();
		if (remainder != null) {
			return remainder.create();
		}
		return ItemStack.EMPTY;
	}

	private boolean hasInput() {
		for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
			if (!inventory.getResource(i).isEmpty()) return true;
		}
		return false;
	}

	protected boolean canCook(CookingPotRecipe recipe) {
		if (level == null) return false;
		if (hasInput()) {
			ItemStack resultStack = recipe.assemble(new RecipeWrapper(IItemHandler.of(inventory)));
			if (resultStack.isEmpty()) {
				return false;
			} else {
				ItemStack storedMealStack = ItemUtil.getStack(inventory, MEAL_DISPLAY_SLOT);
				if (storedMealStack.isEmpty()) {
					return true;
				} else if (!ItemStack.isSameItem(storedMealStack, resultStack)) {
					return false;
				} else if (storedMealStack.getCount() + resultStack.getCount() <= Math.max(64, storedMealStack.getMaxStackSize())) {
					return true;
				} else {
					return storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
				}
			}
		} else {
			return false;
		}
	}

	private boolean processCooking(RecipeHolder<CookingPotRecipe> recipe, CookingPotBlockEntity cookingPot) {
		if (level == null) return false;

		++cookTime;
		cookTimeTotal = recipe.value().getCookTime();
		if (cookTime < cookTimeTotal) {
			return false;
		}

		cookTime = 0;
		mealContainerStack = recipe.value().getOutputContainer();
		ItemStack resultStack = recipe.value().assemble(new RecipeWrapper(IItemHandler.of(inventory)));
		ItemStack storedMealStack = ItemUtil.getStack(inventory, MEAL_DISPLAY_SLOT);
		if (storedMealStack.isEmpty()) {
			inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(resultStack), resultStack.getCount());
		} else if (ItemStack.isSameItem(storedMealStack, resultStack)) {
			int newCount = storedMealStack.getCount() + resultStack.getCount();
			inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(resultStack), newCount);
		}
		cookingPot.setRecipeUsed(recipe);

		for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
			ItemStack slotStack = ItemUtil.getStack(inventory, i);
			if (!slotStack.isEmpty()) {
				Item item = slotStack.getItem();
				ItemStackTemplate remainder = item.getCraftingRemainder();
				if (remainder != null) {
					ejectIngredientRemainder(remainder.create());
				} else if (INGREDIENT_REMAINDER_OVERRIDES.containsKey(item)) {
					ejectIngredientRemainder(INGREDIENT_REMAINDER_OVERRIDES.get(item).getDefaultInstance());
				}
				int newCount = slotStack.getCount() - 1;
				if (newCount <= 0) {
					inventory.set(i, ItemResource.EMPTY, 0);
				} else {
					inventory.set(i, ItemResource.of(slotStack), newCount);
				}
			}
		}
		return true;
	}

	protected void ejectIngredientRemainder(ItemStack remainderStack) {
		Direction direction = getBlockState().getValue(CookingPotBlock.FACING).getCounterClockWise();
		double x = worldPosition.getX() + 0.5 + (direction.getStepX() * 0.25);
		double y = worldPosition.getY() + 0.7;
		double z = worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.25);
		ItemUtils.spawnItemEntity(level, remainderStack, x, y, z,
			direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F);
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
		if (recipe != null) {
			Identifier recipeID = recipe.id().identifier();
			usedRecipeTracker.addTo(recipeID, 1);
		}
	}

	@Nullable
	@Override
	public RecipeHolder<?> getRecipeUsed() {
		return null;
	}

	@Override
	public void awardUsedRecipes(Player player, List<ItemStack> items) {
		List<RecipeHolder<?>> usedRecipes = getUsedRecipesAndPopExperience(player.level(), player.position());
		player.awardRecipes(usedRecipes);
		usedRecipeTracker.clear();
	}

	public List<RecipeHolder<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
		List<RecipeHolder<?>> list = Lists.newArrayList();

		for (Object2IntMap.Entry<Identifier> entry : usedRecipeTracker.object2IntEntrySet()) {
			ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, entry.getKey());
			if (level instanceof ServerLevel serverLevel) {
				serverLevel.recipeAccess().byKey(recipeKey).ifPresent((recipe) -> {
					list.add(recipe);
					splitAndSpawnExperience(serverLevel, pos, entry.getIntValue(), ((CookingPotRecipe) recipe.value()).getExperience());
				});
			}
		}

		return list;
	}

	private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
		int expTotal = Mth.floor((float) craftedAmount * experience);
		float expFraction = Mth.frac((float) craftedAmount * experience);
		if (expFraction != 0.0F && Math.random() < (double) expFraction) {
			++expTotal;
		}
		ExperienceOrb.award(level, pos, expTotal);
	}

	public boolean isHeated() {
		if (level == null) return false;
		return this.isHeated(level, worldPosition);
	}

	public ItemStacksResourceHandler getInventory() {
		return inventory;
	}

	public ItemStack getMeal() {
		return ItemUtil.getStack(inventory, MEAL_DISPLAY_SLOT);
	}

	public NonNullList<ItemStack> getDroppableInventory() {
		NonNullList<ItemStack> drops = NonNullList.create();
		for (int i = 0; i < INVENTORY_SIZE; ++i) {
			if (i != MEAL_DISPLAY_SLOT) {
				drops.add(ItemUtil.getStack(inventory, i));
			}
		}
		return drops;
	}

	private void moveMealToOutput() {
		ItemStack mealStack = ItemUtil.getStack(inventory, MEAL_DISPLAY_SLOT);
		ItemStack outputStack = ItemUtil.getStack(inventory, OUTPUT_SLOT);
		int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
		if (outputStack.isEmpty()) {
			ItemStack moved = mealStack.split(mealCount);
			inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(mealStack), mealStack.getCount());
			inventory.set(OUTPUT_SLOT, ItemResource.of(moved), moved.getCount());
		} else if (ItemStack.isSameItem(mealStack, outputStack)) {
			mealStack.shrink(mealCount);
			inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(mealStack), mealStack.getCount());
			outputStack.grow(mealCount);
			inventory.set(OUTPUT_SLOT, ItemResource.of(outputStack), outputStack.getCount());
		}
	}

	private void useStoredContainersOnMeal() {
		ItemStack mealStack = ItemUtil.getStack(inventory, MEAL_DISPLAY_SLOT);
		ItemStack containerInputStack = ItemUtil.getStack(inventory, CONTAINER_SLOT);
		ItemStack outputStack = ItemUtil.getStack(inventory, OUTPUT_SLOT);

		if (isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxStackSize()) {
			int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
			int mealCount = Math.min(smallerStackCount, mealStack.getMaxStackSize() - outputStack.getCount());
			if (outputStack.isEmpty()) {
				containerInputStack.shrink(mealCount);
				ItemStack moved = mealStack.split(mealCount);
				inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(mealStack), mealStack.getCount());
				inventory.set(CONTAINER_SLOT, ItemResource.of(containerInputStack), containerInputStack.getCount());
				inventory.set(OUTPUT_SLOT, ItemResource.of(moved), moved.getCount());
			} else if (ItemStack.isSameItem(outputStack, mealStack)) {
				mealStack.shrink(mealCount);
				containerInputStack.shrink(mealCount);
				outputStack.grow(mealCount);
				inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(mealStack), mealStack.getCount());
				inventory.set(CONTAINER_SLOT, ItemResource.of(containerInputStack), containerInputStack.getCount());
				inventory.set(OUTPUT_SLOT, ItemResource.of(outputStack), outputStack.getCount());
			}
		}
	}

	public ItemStack useHeldItemOnMeal(ItemStack container) {
		if (isContainerValid(container) && !getMeal().isEmpty()) {
			container.shrink(1);
			inventoryChanged();
			return getMeal().split(1);
		}
		return ItemStack.EMPTY;
	}

	private boolean doesMealHaveContainer(ItemStack meal) {
		if (!mealContainerStack.isEmpty()) return true;
		ItemStackTemplate remainder = meal.getItem().getCraftingRemainder();
		return remainder != null && !remainder.create().isEmpty();
	}

	public boolean isContainerValid(ItemStack containerItem) {
		if (containerItem.isEmpty()) return false;
		if (!mealContainerStack.isEmpty()) return ItemStack.isSameItem(mealContainerStack, containerItem);
		return false;
	}

	@Override
	public Component getName() {
		return customName != null ? customName : TextUtils.container("cooking_pot");
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	@Nullable
	public Component getCustomName() {
		return customName;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
		return new CookingPotMenu(id, player, this, cookingPotData);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter componentInput) {
		super.applyImplicitComponents(componentInput);
		this.customName = componentInput.get(DataComponents.CUSTOM_NAME);
		ItemStack meal = componentInput.getOrDefault(ModDataComponents.MEAL, ItemStackWrapper.EMPTY).getStack();
		if (!meal.isEmpty()) {
			inventory.set(MEAL_DISPLAY_SLOT, ItemResource.of(meal), meal.getCount());
		}
		this.mealContainerStack = componentInput.getOrDefault(ModDataComponents.CONTAINER, ItemStackWrapper.EMPTY).getStack();
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		components.set(DataComponents.CUSTOM_NAME, this.customName);
		if (!getMeal().isEmpty()) {
			components.set(ModDataComponents.MEAL, new ItemStackWrapper(getMeal()));
		}
		if (!getContainer().isEmpty()) {
			components.set(ModDataComponents.CONTAINER, new ItemStackWrapper(getContainer()));
		}
	}

	private ItemStacksResourceHandler createHandler() {
		return new ItemStacksResourceHandler(INVENTORY_SIZE)
		{
			@Override
			protected int getCapacity(int index, ItemResource resource) {
				if (index == MEAL_DISPLAY_SLOT) {
					return Integer.MAX_VALUE; // Allow the meal slot to hold up to Integer.MAX_VALUE items
				}
				return super.getCapacity(index, resource);
			}

			@Override
			protected void onContentsChanged(int index, ItemStack previousContents) {
				inventoryChanged();
			}
		};
	}

	private ContainerData createIntArray() {
		return new ContainerData()
		{
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> CookingPotBlockEntity.this.cookTime;
					case 1 -> CookingPotBlockEntity.this.cookTimeTotal;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> CookingPotBlockEntity.this.cookTime = value;
					case 1 -> CookingPotBlockEntity.this.cookTimeTotal = value;
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		};
	}

	@Override
	public void clearContent() {
		ItemUtils.clearItems(inventory);
	}
}
