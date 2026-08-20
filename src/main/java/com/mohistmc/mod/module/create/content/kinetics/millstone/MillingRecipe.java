package com.mohistmc.mod.module.create.content.kinetics.millstone;

import com.mohistmc.mod.module.create.AllRecipeSerializers;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.create.content.processing.recipe.ProcessingOutput;
import com.mohistmc.mod.module.create.foundation.recipe.CreateSingleStackRollableRecipe;
import com.mohistmc.mod.module.create.foundation.recipe.TimedRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record MillingRecipe(int time, List<ProcessingOutput> results,
                            Ingredient ingredient) implements CreateSingleStackRollableRecipe, TimedRecipe {
    public static final MapCodec<MillingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.fieldOf("processing_time").forGetter(MillingRecipe::time),
        ProcessingOutput.CODEC.listOf(1, 4).fieldOf("results").forGetter(MillingRecipe::results),
        Ingredient.CODEC.fieldOf("ingredient").forGetter(MillingRecipe::ingredient)
    ).apply(instance, MillingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MillingRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        MillingRecipe::time,
        ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
        MillingRecipe::results,
        Ingredient.CONTENTS_STREAM_CODEC,
        MillingRecipe::ingredient,
        MillingRecipe::new
    );
    public static final RecipeSerializer<MillingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeSerializer<MillingRecipe> getSerializer() {
        return AllRecipeSerializers.MILLING.get();
    }

    @Override
    public RecipeType<MillingRecipe> getType() {
        return AllRecipeTypes.MILLING.get();
    }
}
