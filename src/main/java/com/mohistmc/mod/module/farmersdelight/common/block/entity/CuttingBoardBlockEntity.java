package com.mohistmc.mod.module.farmersdelight.common.block.entity;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.block.CuttingBoardBlock;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.inventory.RecipeWrapper;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipeInput;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModAdvancements;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlockEntityTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModRecipeTypes;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModSounds;
import com.mohistmc.mod.module.farmersdelight.common.tag.CommonTags;
import com.mohistmc.mod.module.farmersdelight.common.utility.ItemUtils;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@EventBusSubscriber(modid = FarmersDelight.MODID)
public class CuttingBoardBlockEntity extends SyncedBlockEntity implements Clearable
{
	private final ItemStacksResourceHandler inventory;
	private final RecipeManager.CachedCheck<CuttingBoardRecipeInput, CuttingBoardRecipe> quickCheck;
	private Identifier lastRecipeID;
	private boolean isItemCarvingBoard;

	public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.CUTTING_BOARD.get(), pos, state);
		inventory = createHandler();
		isItemCarvingBoard = false;
		quickCheck = RecipeManager.createCheck(ModRecipeTypes.CUTTING.get());
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntityTypes.CUTTING_BOARD.get(), (cuttingBoard, side) -> cuttingBoard.inventory);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		isItemCarvingBoard = input.getBooleanOr("IsItemCarved", false);
		inventory.deserialize(input.childOrEmpty("Inventory"));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		inventory.serialize(output.child("Inventory"));
		output.putBoolean("IsItemCarved", isItemCarvingBoard);
	}

	public boolean processStoredItemUsingTool(ItemStack toolStack, @Nullable Player player) {
		if (level == null) return false;

		if (isItemCarvingBoard) return false;

		Optional<RecipeHolder<CuttingBoardRecipe>> matchingRecipe = getMatchingRecipe(toolStack, player);

		matchingRecipe.ifPresent(recipe -> {
			List<ItemStack> results = recipe.value().rollResults(level.getRandom(), ItemUtils.getValidatedEnchantmentLevel(Enchantments.FORTUNE, level.registryAccess(), toolStack), new RecipeWrapper(inventory));
			for (ItemStack resultStack : results) {
				Direction direction = getBlockState().getValue(CuttingBoardBlock.FACING).getCounterClockWise();
				ItemUtils.spawnItemEntity(level, resultStack.copy(),
						worldPosition.getX() + 0.5 + (direction.getStepX() * 0.2), worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.2),
						direction.getStepX() * 0.2F, 0.0F, direction.getStepZ() * 0.2F);
			}
			if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
				toolStack.hurtAndBreak(1, serverLevel, player instanceof ServerPlayer serverPlayer ? serverPlayer : null, (item) -> {
				});
				if (player != null) {
					player.awardStat(Stats.ITEM_USED.get(toolStack.getItem()));
				}
			}
			if (level instanceof ServerLevel serverLevel) {
				spawnCuttingParticles(serverLevel, getBlockPos(), getStoredItem());
			}
			playProcessingSound(recipe.value().getSoundEvent().orElse(null), toolStack, getStoredItem());

			Transaction transaction = Transaction.openRoot();
			inventory.extract(inventory.getResource(0), 1, transaction);
			transaction.commit();

			if (player instanceof ServerPlayer) {
				ModAdvancements.USE_CUTTING_BOARD.get().trigger((ServerPlayer) player);
				if (!getStoredItem().isEmpty()) {
					player.sendOverlayMessage(TextUtils.block("cutting_board.remaining_items", getStoredItem().getCount()));
				} else {
					player.sendOverlayMessage(Component.empty());
				}
			}
		});

		return matchingRecipe.isPresent();
	}

	private Optional<RecipeHolder<CuttingBoardRecipe>> getMatchingRecipe(ItemStack toolStack, @Nullable Player player) {
		if (level == null) return Optional.empty();
		if (!(level instanceof ServerLevel serverLevel)) return Optional.empty();

		Optional<RecipeHolder<CuttingBoardRecipe>> recipe = quickCheck.getRecipeFor(new CuttingBoardRecipeInput(getStoredItem(), toolStack), serverLevel);
		if (recipe.isPresent()) {
			if (recipe.get().value().getTool().test(toolStack)) {
				return recipe;
			} else if (player != null) {
				player.sendOverlayMessage(TextUtils.block("cutting_board.invalid_item"));
			}
		} else if (player != null) {
			player.sendOverlayMessage(TextUtils.block("cutting_board.invalid_tool"));
		}

		return Optional.empty();
	}

	public void spawnCuttingParticles(ServerLevel level, BlockPos pos, ItemStack stack) {
		level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromStack(stack)), pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0.05D);
	}

	public void playProcessingSound(@Nullable Holder<SoundEvent> sound, ItemStack tool, ItemStack boardItem) {
		if (sound != null) {
			playSound(sound.value(), 1.0F, 1.0F);
		} else if (tool.is(Tags.Items.TOOLS_SHEAR)) {
			playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
		} else if (tool.is(CommonTags.Items.TOOLS_KNIFE)) {
			playSound(ModSounds.BLOCK_CUTTING_BOARD_KNIFE.get(), 0.8F, 1.0F);
		} else if (boardItem.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			SoundType soundType = block.defaultBlockState().getSoundType();
			playSound(soundType.getBreakSound(), 1.0F, 0.8F);
		} else {
			playSound(SoundEvents.WOOD_BREAK, 1.0F, 0.8F);
		}
	}

	public void playSound(SoundEvent sound, float volume, float pitch) {
		if (level != null)
			level.playSound(null, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F, sound, SoundSource.BLOCKS, volume, pitch);
	}

	public boolean canAddItem(ItemStack addedStack) {
		if (isItemCarvingBoard || addedStack.isEmpty()) {
			return false;
		}
		Transaction transaction = Transaction.openRoot();
		int amount = inventory.insert(ItemResource.of(addedStack.copy()), addedStack.getCount(), transaction);
		transaction.close();
		return amount > 0;
	}

	public ItemStack addItem(ItemStack addedStack) {
		if (!isItemCarvingBoard) {
			Transaction transaction = Transaction.openRoot();
			int inserted = inventory.insert(ItemResource.of(addedStack.copy()), addedStack.getCount(), transaction);
			transaction.commit();
			return addedStack.copyWithCount(addedStack.count() - inserted);
		}
		return addedStack;
	}

	public ItemStack removeItem() {
		isItemCarvingBoard = false;

		Transaction transaction = Transaction.openRoot();
		ItemResource type = inventory.getResource(0);
		int amount = inventory.extract(type, getMaxStackSize(), transaction);
		transaction.commit();

		return type.toStack(amount);
	}

	public boolean carveToolOnBoard(ItemStack toolStack) {
		if (toolStack.isDamageableItem() || toolStack.getItem() instanceof TridentItem || toolStack.getItem() instanceof ShearsItem) {
			if (addItem(toolStack) == ItemStack.EMPTY) {
				isItemCarvingBoard = true;
				return true;
			}
		}
		return false;
	}

	public ItemStacksResourceHandler getInventory() {
		return inventory;
	}

	public ItemStack getStoredItem() {
		return inventory.getResource(0).toStack();
	}

	public int getMaxStackSize() {
		return inventory.getCapacityAsInt(0, inventory.getResource(0));
	}

	public boolean isEmpty() {
		return inventory.getAmountAsInt(0) == 0;
	}

	public boolean isItemCarvingBoard() {
		return isItemCarvingBoard;
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
	}

	private ItemStacksResourceHandler createHandler() {
		return new ItemStacksResourceHandler(1)
		{
			@Override
			protected void onContentsChanged(int index, ItemStack previousContents) {
				inventoryChanged();
			}
		};
	}

	@Override
	public void clearContent() {
		ItemUtils.clearItems(inventory);
	}
}
