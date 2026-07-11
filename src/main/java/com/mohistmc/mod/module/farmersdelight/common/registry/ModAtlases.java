package com.mohistmc.mod.module.farmersdelight.common.registry;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;

public class ModAtlases
{
	private static final SpriteMapper CANVAS_SIGN_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "entity/signs");
	private static final SpriteMapper HANGING_SIGN_MAPPER = new SpriteMapper(TextureAtlas.LOCATION_BLOCKS, "entity/signs/hanging");

	public static final SpriteId BLANK_CANVAS_SIGN_SPRITE = CANVAS_SIGN_MAPPER.apply(Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "canvas"));
	public static final SpriteId BLANK_HANGING_CANVAS_SIGN_SPRITE = HANGING_SIGN_MAPPER.apply(Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "canvas"));

	public static final Map<DyeColor, SpriteId> DYED_CANVAS_SIGN_SPRITES =
			Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), ModAtlases::createCanvasSignSprite));
	public static final Map<DyeColor, SpriteId> DYED_HANGING_CANVAS_SIGN_SPRITES =
			Arrays.stream(DyeColor.values()).collect(Collectors.toMap(Function.identity(), ModAtlases::createHangingCanvasSignSprite));

	public static SpriteId createCanvasSignSprite(DyeColor dyeType) {
		return CANVAS_SIGN_MAPPER.apply(Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "canvas_" + dyeType.getName()));
	}

	public static SpriteId createHangingCanvasSignSprite(DyeColor dyeType) {
		return HANGING_SIGN_MAPPER.apply(Identifier.fromNamespaceAndPath(FarmersDelight.MODID, "canvas_" + dyeType.getName()));
	}

	public static SpriteId getCanvasSignSprite(@Nullable DyeColor dyeColor) {
		return dyeColor != null ? ModAtlases.DYED_CANVAS_SIGN_SPRITES.get(dyeColor) : ModAtlases.BLANK_CANVAS_SIGN_SPRITE;
	}

	public static SpriteId getHangingCanvasSignSprite(@Nullable DyeColor dyeColor) {
		return dyeColor != null ? ModAtlases.DYED_HANGING_CANVAS_SIGN_SPRITES.get(dyeColor) : ModAtlases.BLANK_HANGING_CANVAS_SIGN_SPRITE;
	}
}
