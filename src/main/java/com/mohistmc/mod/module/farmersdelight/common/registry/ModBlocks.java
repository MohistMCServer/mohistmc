package com.mohistmc.mod.module.farmersdelight.common.registry;

import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.BlockShapes;
import com.mohistmc.mod.module.farmersdelight.common.block.*;

public class ModBlocks
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FarmersDelight.MODID);

	private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
		return (state) -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
	}

	private static ToIntFunction<BlockState> glowingFeastBlockEmission() {
		return (state) -> state.getValue(FeastBlock.SERVINGS) * 3;
	}

	// Workstations
	public static final DeferredBlock<Block> STOVE = BLOCKS.registerBlock("stove", StoveBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(litBlockEmission(13)));
	public static final DeferredBlock<Block> COOKING_POT = BLOCKS.registerBlock("cooking_pot", CookingPotBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.5F, 6.0F).sound(SoundType.LANTERN));
	public static final DeferredBlock<Block> SKILLET = BLOCKS.registerBlock("skillet", SkilletBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.5F, 6.0F).sound(SoundType.LANTERN));
	public static final DeferredBlock<Block> WOODEN_BASKET = BLOCKS.registerBlock("wooden_basket", BasketBlock::new, () -> BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> BAMBOO_BASKET = BLOCKS.registerBlock("bamboo_basket", BasketBlock::new, () -> BlockBehaviour.Properties.of().strength(1.5F).sound(SoundType.BAMBOO_WOOD));
	public static final DeferredBlock<Block> CUTTING_BOARD = BLOCKS.registerBlock("cutting_board", CuttingBoardBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F).sound(SoundType.WOOD));

	// Crop Storage
	public static final DeferredBlock<Block> CARROT_CRATE = BLOCKS.registerBlock("carrot_crate",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> POTATO_CRATE = BLOCKS.registerBlock("potato_crate",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> BEETROOT_CRATE = BLOCKS.registerBlock("beetroot_crate",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> CABBAGE_CRATE = BLOCKS.registerBlock("cabbage_crate",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> TOMATO_CRATE = BLOCKS.registerBlock("tomato_crate",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> ONION_CRATE = BLOCKS.registerBlock("onion_crate",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	public static final DeferredBlock<Block> RICE_BALE = BLOCKS.registerBlock("rice_bale",
		RiceBaleBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK));
	public static final DeferredBlock<Block> RICE_BAG = BLOCKS.registerBlock("rice_bag",
		Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WOOL.white()));
	public static final DeferredBlock<Block> STRAW_BALE = BLOCKS.registerBlock("straw_bale",
		StrawBaleBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK));

	// Building
	public static final DeferredBlock<Block> ROPE = BLOCKS.registerBlock("rope",
		RopeBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CARPET.brown()).noCollision().noOcclusion().strength(0.2F).sound(SoundType.WOOL));
	public static final DeferredBlock<Block> SAFETY_NET = BLOCKS.registerBlock("safety_net",
		SafetyNetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CARPET.brown()).strength(0.2F).sound(SoundType.WOOL));
	public static final DeferredBlock<Block> ROPE_FENCE = BLOCKS.registerBlock("rope_fence",
		RopeFenceBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).strength(1.0F));
	public static final DeferredBlock<Block> ROPE_FENCE_GATE = BLOCKS.registerBlock("rope_fence_gate",
		RopeFenceGateBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).strength(1.0F));
	public static final DeferredBlock<Block> OAK_CABINET = BLOCKS.registerBlock("oak_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> SPRUCE_CABINET = BLOCKS.registerBlock("spruce_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> BIRCH_CABINET = BLOCKS.registerBlock("birch_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> JUNGLE_CABINET = BLOCKS.registerBlock("jungle_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> ACACIA_CABINET = BLOCKS.registerBlock("acacia_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> DARK_OAK_CABINET = BLOCKS.registerBlock("dark_oak_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> MANGROVE_CABINET = BLOCKS.registerBlock("mangrove_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));
	public static final DeferredBlock<Block> CHERRY_CABINET = BLOCKS.registerBlock("cherry_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).sound(SoundType.CHERRY_WOOD));
	public static final DeferredBlock<Block> BAMBOO_CABINET = BLOCKS.registerBlock("bamboo_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).sound(SoundType.BAMBOO_WOOD));
	public static final DeferredBlock<Block> CRIMSON_CABINET = BLOCKS.registerBlock("crimson_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD));
	public static final DeferredBlock<Block> WARPED_CABINET = BLOCKS.registerBlock("warped_cabinet",
		CabinetBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).sound(SoundType.NETHER_WOOD));
	public static final DeferredBlock<Block> CANVAS_RUG = BLOCKS.registerBlock("canvas_rug",
		CanvasRugBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CARPET.white()).sound(SoundType.GRASS).strength(0.2F));
	public static final DeferredBlock<Block> TATAMI = BLOCKS.registerBlock("tatami",
		TatamiBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WOOL.white()));
	public static final DeferredBlock<Block> FULL_TATAMI_MAT = BLOCKS.registerBlock("full_tatami_mat",
		TatamiMatBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WOOL.white()).strength(0.3F));
	public static final DeferredBlock<Block> HALF_TATAMI_MAT = BLOCKS.registerBlock("half_tatami_mat",
		TatamiHalfMatBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WOOL.white()).strength(0.3F).pushReaction(PushReaction.DESTROY));

	public static final DeferredBlock<Block> CANVAS_SIGN = BLOCKS.registerBlock("canvas_sign",
		props -> new StandingCanvasSignBlock(props, null), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> WHITE_CANVAS_SIGN = BLOCKS.registerBlock("white_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.WHITE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> ORANGE_CANVAS_SIGN = BLOCKS.registerBlock("orange_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.ORANGE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> MAGENTA_CANVAS_SIGN = BLOCKS.registerBlock("magenta_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.MAGENTA), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> LIGHT_BLUE_CANVAS_SIGN = BLOCKS.registerBlock("light_blue_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.LIGHT_BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> YELLOW_CANVAS_SIGN = BLOCKS.registerBlock("yellow_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.YELLOW), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> LIME_CANVAS_SIGN = BLOCKS.registerBlock("lime_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.LIME), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> PINK_CANVAS_SIGN = BLOCKS.registerBlock("pink_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.PINK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> GRAY_CANVAS_SIGN = BLOCKS.registerBlock("gray_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> LIGHT_GRAY_CANVAS_SIGN = BLOCKS.registerBlock("light_gray_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.LIGHT_GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> CYAN_CANVAS_SIGN = BLOCKS.registerBlock("cyan_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.CYAN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> PURPLE_CANVAS_SIGN = BLOCKS.registerBlock("purple_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.PURPLE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> BLUE_CANVAS_SIGN = BLOCKS.registerBlock("blue_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> BROWN_CANVAS_SIGN = BLOCKS.registerBlock("brown_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.BROWN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> GREEN_CANVAS_SIGN = BLOCKS.registerBlock("green_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.GREEN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> RED_CANVAS_SIGN = BLOCKS.registerBlock("red_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.RED), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));
	public static final DeferredBlock<Block> BLACK_CANVAS_SIGN = BLOCKS.registerBlock("black_canvas_sign",
		props -> new StandingCanvasSignBlock(props, DyeColor.BLACK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN));

	public static final DeferredBlock<Block> CANVAS_WALL_SIGN = BLOCKS.registerBlock("canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, null), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> WHITE_CANVAS_WALL_SIGN = BLOCKS.registerBlock("white_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.WHITE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(WHITE_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> ORANGE_CANVAS_WALL_SIGN = BLOCKS.registerBlock("orange_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.ORANGE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(ORANGE_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> MAGENTA_CANVAS_WALL_SIGN = BLOCKS.registerBlock("magenta_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.MAGENTA), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(MAGENTA_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> LIGHT_BLUE_CANVAS_WALL_SIGN = BLOCKS.registerBlock("light_blue_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.LIGHT_BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(LIGHT_BLUE_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> YELLOW_CANVAS_WALL_SIGN = BLOCKS.registerBlock("yellow_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.YELLOW), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(YELLOW_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> LIME_CANVAS_WALL_SIGN = BLOCKS.registerBlock("lime_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.LIME), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(LIME_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> PINK_CANVAS_WALL_SIGN = BLOCKS.registerBlock("pink_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.PINK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(PINK_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> GRAY_CANVAS_WALL_SIGN = BLOCKS.registerBlock("gray_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(GRAY_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> LIGHT_GRAY_CANVAS_WALL_SIGN = BLOCKS.registerBlock("light_gray_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.LIGHT_GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(LIGHT_GRAY_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> CYAN_CANVAS_WALL_SIGN = BLOCKS.registerBlock("cyan_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.CYAN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(CYAN_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> PURPLE_CANVAS_WALL_SIGN = BLOCKS.registerBlock("purple_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.PURPLE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(PURPLE_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> BLUE_CANVAS_WALL_SIGN = BLOCKS.registerBlock("blue_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(BLUE_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> BROWN_CANVAS_WALL_SIGN = BLOCKS.registerBlock("brown_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.BROWN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(BROWN_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> GREEN_CANVAS_WALL_SIGN = BLOCKS.registerBlock("green_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.GREEN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(GREEN_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> RED_CANVAS_WALL_SIGN = BLOCKS.registerBlock("red_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.RED), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(RED_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> BLACK_CANVAS_WALL_SIGN = BLOCKS.registerBlock("black_canvas_wall_sign",
		props -> new WallCanvasSignBlock(props, DyeColor.BLACK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN).overrideLootTable(BLACK_CANVAS_SIGN.get().getLootTable()));

	public static final DeferredBlock<Block> HANGING_CANVAS_SIGN = BLOCKS.registerBlock("hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, null), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> WHITE_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("white_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.WHITE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> ORANGE_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("orange_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.ORANGE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> MAGENTA_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("magenta_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.MAGENTA), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> LIGHT_BLUE_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("light_blue_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.LIGHT_BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> YELLOW_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("yellow_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.YELLOW), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> LIME_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("lime_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.LIME), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> PINK_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("pink_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.PINK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> GRAY_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("gray_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> LIGHT_GRAY_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("light_gray_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.LIGHT_GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> CYAN_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("cyan_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.CYAN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> PURPLE_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("purple_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.PURPLE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> BLUE_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("blue_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> BROWN_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("brown_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.BROWN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> GREEN_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("green_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.GREEN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> RED_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("red_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.RED), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));
	public static final DeferredBlock<Block> BLACK_HANGING_CANVAS_SIGN = BLOCKS.registerBlock("black_hanging_canvas_sign",
		props -> new CeilingHangingCanvasSignBlock(props, DyeColor.BLACK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN));

	public static final DeferredBlock<Block> HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, null), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> WHITE_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("white_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.WHITE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(WHITE_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> ORANGE_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("orange_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.ORANGE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(ORANGE_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> MAGENTA_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("magenta_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.MAGENTA), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(MAGENTA_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> LIGHT_BLUE_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("light_blue_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.LIGHT_BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(LIGHT_BLUE_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> YELLOW_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("yellow_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.YELLOW), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(YELLOW_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> LIME_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("lime_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.LIME), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(LIME_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> PINK_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("pink_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.PINK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(PINK_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> GRAY_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("gray_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(GRAY_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> LIGHT_GRAY_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("light_gray_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.LIGHT_GRAY), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(LIGHT_GRAY_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> CYAN_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("cyan_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.CYAN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(CYAN_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> PURPLE_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("purple_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.PURPLE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(PURPLE_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> BLUE_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("blue_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.BLUE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(BLUE_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> BROWN_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("brown_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.BROWN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(BROWN_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> GREEN_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("green_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.GREEN), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(GREEN_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> RED_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("red_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.RED), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(RED_HANGING_CANVAS_SIGN.get().getLootTable()));
	public static final DeferredBlock<Block> BLACK_HANGING_CANVAS_WALL_SIGN = BLOCKS.registerBlock("black_wall_hanging_canvas_sign",
		props -> new WallHangingCanvasSignBlock(props, DyeColor.BLACK), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN).overrideLootTable(BLACK_HANGING_CANVAS_SIGN.get().getLootTable()));

	// Composting
	public static final DeferredBlock<Block> BROWN_MUSHROOM_COLONY = BLOCKS.registerBlock("brown_mushroom_colony",
		props -> new MushroomColonyBlock(Items.BROWN_MUSHROOM.builtInRegistryHolder(), props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM));
	public static final DeferredBlock<Block> RED_MUSHROOM_COLONY = BLOCKS.registerBlock("red_mushroom_colony",
		props -> new MushroomColonyBlock(Items.RED_MUSHROOM.builtInRegistryHolder(), props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.RED_MUSHROOM));
	public static final DeferredBlock<Block> ORGANIC_COMPOST = BLOCKS.registerBlock("organic_compost",
		OrganicCompostBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).strength(1.2F).sound(SoundType.CROP));
	public static final DeferredBlock<Block> RICH_SOIL = BLOCKS.registerBlock("rich_soil",
		RichSoilBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).randomTicks());
	public static final DeferredBlock<Block> RICH_SOIL_FARMLAND = BLOCKS.registerBlock("rich_soil_farmland",
		RichSoilFarmlandBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND));

	// Pastries
	public static final DeferredBlock<Block> APPLE_PIE = BLOCKS.registerBlock("apple_pie",
		props -> new PieBlock(props, ModItems.APPLE_PIE_SLICE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
	public static final DeferredBlock<Block> SWEET_BERRY_CHEESECAKE = BLOCKS.registerBlock("sweet_berry_cheesecake",
		props -> new PieBlock(props, ModItems.SWEET_BERRY_CHEESECAKE_SLICE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
	public static final DeferredBlock<Block> CHOCOLATE_PIE = BLOCKS.registerBlock("chocolate_pie",
		props -> new PieBlock(props, ModItems.CHOCOLATE_PIE_SLICE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
	public static final DeferredBlock<Block> PUMPKIN_PIE = BLOCKS.registerBlock("pumpkin_pie",
		props -> new PieBlock(props, ModItems.PUMPKIN_PIE_SLICE)
		{
			@Override
			public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
				return new ItemStack(Items.PUMPKIN_PIE);
			}
		}, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));

	// Wild Crops
	public static final DeferredBlock<Block> SANDY_SHRUB = BLOCKS.registerBlock("sandy_shrub",
		SandyShrubBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));

	public static final DeferredBlock<Block> WILD_CABBAGES = BLOCKS.registerBlock("wild_cabbages",
		props -> new WildCropBlock(MobEffects.STRENGTH, 6, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
	public static final DeferredBlock<Block> WILD_ONIONS = BLOCKS.registerBlock("wild_onions",
		props -> new WildCropBlock(MobEffects.FIRE_RESISTANCE, 6, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
	public static final DeferredBlock<Block> WILD_TOMATOES = BLOCKS.registerBlock("wild_tomatoes",
		props -> new WildCropBlock(MobEffects.POISON, 10, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
	public static final DeferredBlock<Block> WILD_CARROTS = BLOCKS.registerBlock("wild_carrots",
		props -> new WildCropBlock(MobEffects.MINING_FATIGUE, 6, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
	public static final DeferredBlock<Block> WILD_POTATOES = BLOCKS.registerBlock("wild_potatoes",
		props -> new WildCropBlock(MobEffects.NAUSEA, 8, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
	public static final DeferredBlock<Block> WILD_BEETROOTS = BLOCKS.registerBlock("wild_beetroots",
		props -> new WildCropBlock(MobEffects.WATER_BREATHING, 8, props), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));
	public static final DeferredBlock<Block> WILD_RICE = BLOCKS.registerBlock("wild_rice",
		WildRiceBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS));

	// Crops
	public static final DeferredBlock<Block> CABBAGE_CROP = BLOCKS.registerBlock("cabbages", CabbageBlock::new, () -> BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	public static final DeferredBlock<Block> ONION_CROP = BLOCKS.registerBlock("onions", OnionBlock::new, () -> BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	public static final DeferredBlock<Block> BUDDING_TOMATO_CROP = BLOCKS.registerBlock("budding_tomatoes", BuddingTomatoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	public static final DeferredBlock<Block> TOMATO_CROP = BLOCKS.registerBlock("tomatoes", TomatoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));
	public static final DeferredBlock<Block> TOMATO_CROP_ON_ROPE = BLOCKS.registerBlock("tomatoes_on_rope", HangingTomatoBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(ModBlocks.TOMATO_CROP.get()).pushReaction(PushReaction.NORMAL));
	public static final DeferredBlock<Block> RICE_CROP = BLOCKS.registerBlock("rice", RiceBlock::new, () -> BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().strength(0.2F).sound(SoundType.CROP));
	public static final DeferredBlock<Block> RICE_CROP_PANICLES = BLOCKS.registerBlock("rice_panicles", RicePaniclesBlock::new, () -> BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP));

	// Feasts
	public static final DeferredBlock<Block> ROAST_CHICKEN_BLOCK = BLOCKS.registerBlock("roast_chicken_block",
		props -> new RotatedFeastBlock(props, ModItems.ROAST_CHICKEN, true, BlockShapes.ROAST_CHICKEN_SHAPES, BlockShapes.TRAY_SHAPE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
	public static final DeferredBlock<Block> STUFFED_PUMPKIN_BLOCK = BLOCKS.registerBlock("stuffed_pumpkin_block",
		props -> new FeastBlock(props, ModItems.STUFFED_PUMPKIN, false, true), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN));
	public static final DeferredBlock<Block> HONEY_GLAZED_HAM_BLOCK = BLOCKS.registerBlock("honey_glazed_ham_block",
		props -> new RotatedFeastBlock(props, ModItems.HONEY_GLAZED_HAM, true, BlockShapes.HONEY_GLAZED_HAM_SHAPES, BlockShapes.TRAY_SHAPE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
	public static final DeferredBlock<Block> SHEPHERDS_PIE_BLOCK = BLOCKS.registerBlock("shepherds_pie_block",
		props -> new RotatedFeastBlock(props, ModItems.SHEPHERDS_PIE, true, BlockShapes.SHEPHERDS_PIE_SHAPES, BlockShapes.TRAY_SHAPE), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
	public static final DeferredBlock<Block> GLEAMING_SALAD_BLOCK = BLOCKS.registerBlock("gleaming_salad_block",
		props -> new GleamingSaladBlock(props, ModItems.GLEAMING_SALAD, true), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).lightLevel(glowingFeastBlockEmission()));
	public static final DeferredBlock<Block> RICE_ROLL_MEDLEY_BLOCK = BLOCKS.registerBlock("rice_roll_medley_block",
		RiceRollMedleyBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE));
}
