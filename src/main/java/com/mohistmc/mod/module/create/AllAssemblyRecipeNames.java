package com.mohistmc.mod.module.create;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mohistmc.mod.module.create.content.fluids.transfer.FillingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;

public class AllAssemblyRecipeNames {
    private static final Map<String, BiFunction<DynamicOps<JsonElement>, JsonObject, Component>> ALL = new HashMap<>();

    public static Component get(DynamicOps<JsonElement> ops, JsonElement json) {
        JsonObject object = (JsonObject) json;
        String type = object.get("type").getAsString();
        BiFunction<DynamicOps<JsonElement>, JsonObject, Component> factory = ALL.get(type);
        if (factory != null) {
            return factory.apply(ops, object);
        }
        String name;
        if (type.startsWith("mohistmc:")) {
            name = type.replaceFirst("mohistmc:", "");
        } else {
            name = type.replaceFirst(":", ".");
        }
        return Component.translatable("create.recipe.assembly." + name);
    }

    public static void register(RecipeType<?> id, BiFunction<DynamicOps<JsonElement>, JsonObject, Component> factory) {
        ALL.put(id.toString(), factory);
    }

    public static void register() {
        register(AllRecipeTypes.DEPLOYING.get(), DeployerApplicationRecipe::getDescriptionForAssembly);
        register(AllRecipeTypes.FILLING.get(), FillingRecipe::getDescriptionForAssembly);
    }
}
