package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.mohistmc.mod.module.create.content.fluids.transfer.EmptyingRecipe;
import com.mohistmc.mod.module.create.content.fluids.transfer.FillingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.crusher.CrushingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.mohistmc.mod.module.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.mohistmc.mod.module.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.mohistmc.mod.module.create.content.kinetics.fan.processing.HauntingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.fan.processing.SplashingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.millstone.MillingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.mixer.CompactingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.mixer.MixingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.mixer.PotionRecipe;
import com.mohistmc.mod.module.create.content.kinetics.press.PressingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.saw.CuttingRecipe;
import com.mohistmc.mod.module.create.content.processing.sequenced.SequencedAssemblyRecipe;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
        BuiltInRegistries.RECIPE_TYPE,
        MOD_ID
    );

    public static final Supplier<RecipeType<CrushingRecipe>> CRUSHING = register("crushing");
    public static final Supplier<RecipeType<CuttingRecipe>> CUTTING = register("mechanical_cutting");
    public static final Supplier<RecipeType<MillingRecipe>> MILLING = register("milling");
    public static final Supplier<RecipeType<MixingRecipe>> MIXING = register("mixing");
    public static final Supplier<RecipeType<CompactingRecipe>> COMPACTING = register("compacting");
    public static final Supplier<RecipeType<PressingRecipe>> PRESSING = register("pressing");
    public static final Supplier<RecipeType<SandPaperPolishingRecipe>> SANDPAPER_POLISHING = register("sandpaper_polishing");
    public static final Supplier<RecipeType<SplashingRecipe>> SPLASHING = register("splashing");
    public static final Supplier<RecipeType<HauntingRecipe>> HAUNTING = register("haunting");
    public static final Supplier<RecipeType<DeployerApplicationRecipe>> DEPLOYING = register("deploying");
    public static final Supplier<RecipeType<FillingRecipe>> FILLING = register("filling");
    public static final Supplier<RecipeType<EmptyingRecipe>> EMPTYING = register("emptying");
    public static final Supplier<RecipeType<ManualApplicationRecipe>> ITEM_APPLICATION = register("item_application");
    public static final Supplier<RecipeType<MechanicalCraftingRecipe>> MECHANICAL_CRAFTING = register("mechanical_crafting");
    public static final Supplier<RecipeType<SequencedAssemblyRecipe>> SEQUENCED_ASSEMBLY = register("sequenced_assembly");
    public static final Supplier<RecipeType<PotionRecipe>> POTION = register("potion");

    private static List<RecipeType<? extends ItemApplicationRecipe>> deployerRecipes;

    public static List<RecipeType<? extends ItemApplicationRecipe>> deployerRecipes() {
        if (deployerRecipes == null) {
            // Lazily resolve to avoid touching the DeferredRegister holders before they are
            // bound by the mod event bus. Only accessed at runtime (deployer processing).
            deployerRecipes = List.of(DEPLOYING.get(), ITEM_APPLICATION.get());
        }
        return deployerRecipes;
    }

    private static final TagKey<RecipeSerializer<?>> AUTOMATION_IGNORE_TAG = TagKey.create(
        Registries.RECIPE_SERIALIZER,
        Identifier.fromNamespaceAndPath(MOD_ID, "automation_ignore")
    );
    public static final Predicate<RecipeHolder<?>> CAN_BE_AUTOMATED = r -> !r.id().identifier().getPath()
        .endsWith("_manual_only");

    public static boolean shouldIgnoreInAutomation(RecipeHolder<?> recipe) {
        RecipeSerializer<?> serializer = recipe.value().getSerializer();
        if (serializer != null && BuiltInRegistries.RECIPE_SERIALIZER.wrapAsHolder(serializer)
            .is(AUTOMATION_IGNORE_TAG)) {
            return true;
        }
        return !CAN_BE_AUTOMATED.test(recipe);
    }

    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        return RECIPE_TYPES.register(
            name,
            () -> new RecipeType<T>() {
                public String toString() {
                    return id.toString();
                }
            }
        );
    }
}