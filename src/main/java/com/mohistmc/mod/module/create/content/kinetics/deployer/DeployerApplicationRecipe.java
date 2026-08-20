package com.mohistmc.mod.module.create.content.kinetics.deployer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mohistmc.mod.module.create.AllRecipeSerializers;
import com.mohistmc.mod.module.create.AllRecipeTypes;
import com.mohistmc.mod.module.create.content.processing.recipe.ProcessingOutput;
import com.mohistmc.mod.module.create.foundation.recipe.IngredientText;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public record DeployerApplicationRecipe(List<ProcessingOutput> results, boolean keepHeldItem, Ingredient target,
                                        Ingredient ingredient) implements ItemApplicationRecipe {
    public static final MapCodec<DeployerApplicationRecipe> MAP_CODEC = ItemApplicationRecipe.createCodec(
        DeployerApplicationRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, DeployerApplicationRecipe> STREAM_CODEC = ItemApplicationRecipe.createStreamCodec(
        DeployerApplicationRecipe::new);
    public static final RecipeSerializer<DeployerApplicationRecipe> SERIALIZER = new RecipeSerializer<>(
        MAP_CODEC,
        STREAM_CODEC
    );

    @Override
    public RecipeSerializer<DeployerApplicationRecipe> getSerializer() {
        return AllRecipeSerializers.DEPLOYING.get();
    }

    @Override
    public RecipeType<DeployerApplicationRecipe> getType() {
        return AllRecipeTypes.DEPLOYING.get();
    }

    public static Component getDescriptionForAssembly(DynamicOps<JsonElement> ops, JsonObject object) {
        return Ingredient.CODEC.parse(ops, object.get("ingredient")).result().map(ingredient -> Component.translatable(
            "create.recipe.assembly.deploying_item",
            new IngredientText(ingredient)
        )).orElseGet(() -> Component.literal("Invalid"));
    }
}