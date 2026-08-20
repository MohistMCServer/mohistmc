package com.mohistmc.mod.module.jei;

import com.google.common.collect.ImmutableList;
import com.mohistmc.mod.module.create.AllFluids;
import com.mohistmc.mod.module.create.AllItemTags;
import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.client.Create;
import com.mohistmc.mod.module.create.client.content.equipment.blueprint.BlueprintScreen;
import com.mohistmc.mod.module.create.client.content.logistics.factoryBoard.FactoryPanelSetItemScreen;
import com.mohistmc.mod.module.create.client.content.logistics.filter.AbstractFilterScreen;
import com.mohistmc.mod.module.create.client.content.logistics.redstoneRequester.RedstoneRequesterScreen;
import com.mohistmc.mod.module.create.client.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.mohistmc.mod.module.create.client.content.redstone.link.controller.LinkedControllerScreen;
import com.mohistmc.mod.module.create.client.content.trains.schedule.ScheduleScreen;
import com.mohistmc.mod.module.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.mohistmc.mod.module.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.mohistmc.mod.module.create.content.fluids.transfer.EmptyingRecipe;
import com.mohistmc.mod.module.create.content.fluids.transfer.FillingRecipe;
import com.mohistmc.mod.module.create.content.kinetics.crafter.MechanicalCraftingRecipe;
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
import com.mohistmc.mod.module.create.foundation.recipe.CreateSingleStackRollableRecipe;
import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.client.gui.CookingPotScreen;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.container.CookingPotMenu;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CookingPotRecipe;
import com.mohistmc.mod.module.farmersdelight.common.crafting.CuttingBoardRecipe;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModBlocks;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModItems;
import com.mohistmc.mod.module.farmersdelight.common.registry.ModMenuTypes;
import com.mohistmc.mod.module.farmersdelight.common.utility.TextUtils;
import com.mohistmc.mod.module.jei.category.AutoCompactingCategory;
import com.mohistmc.mod.module.jei.category.AutoMixingCategory;
import com.mohistmc.mod.module.jei.category.BlockCuttingCategory;
import com.mohistmc.mod.module.jei.category.CompactingCategory;
import com.mohistmc.mod.module.jei.category.CrushingCategory;
import com.mohistmc.mod.module.jei.category.DeployingCategory;
import com.mohistmc.mod.module.jei.category.DrainingCategory;
import com.mohistmc.mod.module.jei.category.FanBlastingCategory;
import com.mohistmc.mod.module.jei.category.FanHauntingCategory;
import com.mohistmc.mod.module.jei.category.FanSmokingCategory;
import com.mohistmc.mod.module.jei.category.FanWashingCategory;
import com.mohistmc.mod.module.jei.category.ManualApplicationCategory;
import com.mohistmc.mod.module.jei.category.MechanicalCraftingCategory;
import com.mohistmc.mod.module.jei.category.MillingCategory;
import com.mohistmc.mod.module.jei.category.MixingCategory;
import com.mohistmc.mod.module.jei.category.MysteriousItemConversionCategory;
import com.mohistmc.mod.module.jei.category.PotionCategory;
import com.mohistmc.mod.module.jei.category.PressingCategory;
import com.mohistmc.mod.module.jei.category.SandpaperPolishingCategory;
import com.mohistmc.mod.module.jei.category.SawingCategory;
import com.mohistmc.mod.module.jei.category.SequencedAssemblyCategory;
import com.mohistmc.mod.module.jei.category.SpoutFillingCategory;
import com.mohistmc.mod.module.jei.display.BlockCuttingDisplay;
import com.mohistmc.mod.module.jei.display.MysteriousItemConversionDisplay;
import com.mohistmc.mod.module.jei.farmersdelight.FDRecipes;
import com.mohistmc.mod.module.jei.farmersdelight.category.CookingRecipeCategory;
import com.mohistmc.mod.module.jei.farmersdelight.category.CuttingRecipeCategory;
import com.mohistmc.mod.module.jei.farmersdelight.category.DecompositionRecipeCategory;
import com.mohistmc.mod.module.jei.farmersdelight.resource.DecompositionDummy;
import com.mohistmc.mod.module.jei.farmersdelight.resource.DoughRecipeMaker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

@JeiPlugin
public class JeiClientPlugin implements IModPlugin {
    public static final Identifier ID = Create.asResource("jei_plugin");
    public static final IRecipeType<RecipeHolder<CraftingRecipe>> AUTOMATIC_PACKING = createRecipeHolderType(
        "automatic_packing");
    public static final IRecipeType<RecipeHolder<CompactingRecipe>> PACKING = createRecipeHolderType("packing");
    public static final IRecipeType<RecipeHolder<PressingRecipe>> PRESSING = createRecipeHolderType("pressing");
    public static final IRecipeType<RecipeHolder<ShapelessRecipe>> AUTOMATIC_SHAPELESS = createRecipeHolderType(
        "automatic_shapeless");
    public static final IRecipeType<RecipeHolder<MixingRecipe>> MIXING = createRecipeHolderType("mixing");
    public static final IRecipeType<RecipeHolder<MillingRecipe>> MILLING = createRecipeHolderType("milling");
    public static final IRecipeType<RecipeHolder<CuttingRecipe>> SAWING = createRecipeHolderType("sawing");
    public static final IRecipeType<RecipeHolder<? extends CreateSingleStackRollableRecipe>> CRUSHING = createRecipeHolderType(
        "crushing");
    public static final IRecipeType<RecipeHolder<ManualApplicationRecipe>> ITEM_APPLICATION = createRecipeHolderType(
        "item_application");
    public static final IRecipeType<RecipeHolder<? extends ItemApplicationRecipe>> DEPLOYING = createRecipeHolderType(
        "deploying");
    public static final IRecipeType<RecipeHolder<EmptyingRecipe>> DRAINING = createRecipeHolderType("draining");
    public static final IRecipeType<RecipeHolder<MechanicalCraftingRecipe>> MECHANICAL_CRAFTING = createRecipeHolderType(
        "mechanical_crafting");
    public static final IRecipeType<RecipeHolder<FillingRecipe>> SPOUT_FILLING = createRecipeHolderType("spout_filling");
    public static final IRecipeType<RecipeHolder<SandPaperPolishingRecipe>> SANDPAPER_POLISHING = createRecipeHolderType(
        "sandpaper_polishing");
    public static final IRecipeType<RecipeHolder<SequencedAssemblyRecipe>> SEQUENCED_ASSEMBLY = createRecipeHolderType(
        "sequenced_assembly");
    public static final IRecipeType<RecipeHolder<? extends SingleItemRecipe>> FAN_BLASTING = createRecipeHolderType(
        "fan_blasting");
    public static final IRecipeType<RecipeHolder<HauntingRecipe>> FAN_HAUNTING = createRecipeHolderType("fan_haunting");
    public static final IRecipeType<RecipeHolder<SmokingRecipe>> FAN_SMOKING = createRecipeHolderType("fan_smoking");
    public static final IRecipeType<RecipeHolder<SplashingRecipe>> FAN_WASHING = createRecipeHolderType("fan_washing");
    public static final IRecipeType<RecipeHolder<PotionRecipe>> AUTOMATIC_BREWING = createRecipeHolderType(
        "automatic_brewing");
    public static final IRecipeType<MysteriousItemConversionDisplay> MYSTERY_CONVERSION = IRecipeType.create(
        MOD_ID,
        "mystery_conversion",
        MysteriousItemConversionDisplay.class
    );
    public static final IRecipeType<BlockCuttingDisplay> BLOCK_CUTTING = IRecipeType.create(
        MOD_ID,
        "block_cutting",
        BlockCuttingDisplay.class
    );

    public static final IRecipeType<RecipeHolder<CookingPotRecipe>> COOKING = createRecipeHolderType("cooking");
    public static final IRecipeType<RecipeHolder<CuttingBoardRecipe>> CUTTING = createRecipeHolderType("cutting");
    public static final IRecipeType<DecompositionDummy> DECOMPOSITION = IRecipeType.create(FarmersDelight.MODID, "decomposition", DecompositionDummy.class);

    @SuppressWarnings("unchecked")
    public static <T> IRecipeType<T> createRecipeHolderType(String path) {
        Identifier uid = Identifier.fromNamespaceAndPath(MOD_ID, path);
        return (IRecipeType<T>) IRecipeType.create(uid, RecipeHolder.class);
    }

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new AutoCompactingCategory(),
            new CompactingCategory(),
            new PressingCategory(),
            new AutoMixingCategory(),
            new MixingCategory(),
            new MillingCategory(),
            new SawingCategory(),
            new CrushingCategory(),
            new MysteriousItemConversionCategory(),
            new ManualApplicationCategory(),
            new DeployingCategory(),
            new DrainingCategory(),
            new MechanicalCraftingCategory(),
            new SpoutFillingCategory(),
            new SandpaperPolishingCategory(),
            new SequencedAssemblyCategory(),
            new FanBlastingCategory(),
            new FanHauntingCategory(),
            new FanSmokingCategory(),
            new FanWashingCategory(),
            new PotionCategory(),
            new BlockCuttingCategory(),
            new CookingRecipeCategory(),
            new CuttingRecipeCategory(),
            new DecompositionRecipeCategory()
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(AUTOMATIC_PACKING, AllItems.MECHANICAL_PRESS, AllItems.BASIN);
        registration.addCraftingStation(PACKING, AllItems.MECHANICAL_PRESS, AllItems.BASIN);
        registration.addCraftingStation(PRESSING, AllItems.MECHANICAL_PRESS);
        registration.addCraftingStation(AUTOMATIC_SHAPELESS, AllItems.MECHANICAL_MIXER, AllItems.BASIN);
        registration.addCraftingStation(MIXING, AllItems.MECHANICAL_MIXER, AllItems.BASIN);
        registration.addCraftingStation(MILLING, AllItems.MILLSTONE);
        registration.addCraftingStation(SAWING, AllItems.MECHANICAL_SAW);
        registration.addCraftingStation(CRUSHING, AllItems.CRUSHING_WHEEL);
        registration.addCraftingStation(DEPLOYING, AllItems.DEPLOYER, AllItems.DEPOT, AllItems.BELT_CONNECTOR);
        registration.addCraftingStation(DRAINING, AllItems.ITEM_DRAIN);
        registration.addCraftingStation(MECHANICAL_CRAFTING, AllItems.MECHANICAL_CRAFTER);
        registration.addCraftingStation(SPOUT_FILLING, AllItems.SPOUT);
        registration.addCraftingStation(SANDPAPER_POLISHING, AllItems.SAND_PAPER, AllItems.RED_SAND_PAPER);
        registration.addCraftingStation(FAN_BLASTING, AllItems.ENCASED_FAN);
        registration.addCraftingStation(FAN_HAUNTING, AllItems.ENCASED_FAN);
        registration.addCraftingStation(FAN_SMOKING, AllItems.ENCASED_FAN);
        registration.addCraftingStation(FAN_WASHING, AllItems.ENCASED_FAN);
        registration.addCraftingStation(AUTOMATIC_BREWING, AllItems.MECHANICAL_MIXER, AllItems.BASIN);
        registration.addCraftingStation(BLOCK_CUTTING, AllItems.MECHANICAL_SAW);

        registration.addCraftingStation(COOKING, new ItemStack(ModItems.COOKING_POT.get()));
        registration.addCraftingStation(CUTTING, new ItemStack(ModItems.CUTTING_BOARD.get()));
        registration.addCraftingStation(RecipeTypes.CAMPFIRE_COOKING, new ItemStack(ModItems.STOVE.get()));
        registration.addCraftingStation(RecipeTypes.CAMPFIRE_COOKING, new ItemStack(ModItems.SKILLET.get()));
        registration.addCraftingStation(DECOMPOSITION, new ItemStack(ModBlocks.ORGANIC_COMPOST.get()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeMap preparedRecipes = Internal.getClientSyncedRecipes();
        registration.addRecipes(AUTOMATIC_PACKING, AutoCompactingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(PACKING, CompactingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(PRESSING, PressingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(AUTOMATIC_SHAPELESS, AutoMixingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(MIXING, MixingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(MILLING, MillingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(SAWING, SawingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(CRUSHING, CrushingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(MYSTERY_CONVERSION, MysteriousItemConversionCategory.getRecipes());
        registration.addRecipes(ITEM_APPLICATION, ManualApplicationCategory.getRecipes(preparedRecipes));
        registration.addRecipes(DEPLOYING, DeployingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(MECHANICAL_CRAFTING, MechanicalCraftingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(SANDPAPER_POLISHING, SandpaperPolishingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(SEQUENCED_ASSEMBLY, SequencedAssemblyCategory.getRecipes(preparedRecipes));
        registration.addRecipes(FAN_BLASTING, FanBlastingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(FAN_HAUNTING, FanHauntingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(FAN_SMOKING, FanSmokingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(FAN_WASHING, FanWashingCategory.getRecipes(preparedRecipes));
        registration.addRecipes(AUTOMATIC_BREWING, PotionCategory.getRecipes(preparedRecipes));
        IIngredientManager ingredientManager = registration.getIngredientManager();
        Collection<ItemStack> stacks = ingredientManager.getAllIngredients(VanillaTypes.ITEM_STACK);
        registration.addRecipes(DRAINING, DrainingCategory.getRecipes(preparedRecipes, stacks.stream()));
        registration.addRecipes(
            SPOUT_FILLING,
            SpoutFillingCategory.getRecipes(
                preparedRecipes,
                stacks.stream(),
                ingredientManager.getAllIngredients(NeoForgeTypes.FLUID_STACK).stream()
            )
        );
        registerToolboxRecipes(registration);
        registration.addRecipes(BLOCK_CUTTING, BlockCuttingCategory.getRecipes(preparedRecipes));

        FDRecipes fdRecipes = new FDRecipes();
        registration.addRecipes(COOKING, fdRecipes.getCookingPotRecipes());
        registration.addRecipes(CUTTING, fdRecipes.getCuttingBoardRecipes());
        registration.addRecipes(DECOMPOSITION, ImmutableList.of(new DecompositionDummy()));

        registration.addRecipes(RecipeTypes.CRAFTING, DoughRecipeMaker.createRecipe());

        registration.addIngredientInfo(new ItemStack(ModItems.WHEAT_DOUGH.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.dough"));
        registration.addIngredientInfo(new ItemStack(ModItems.STRAW.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.straw"));
        registration.addIngredientInfo(new ItemStack(ModItems.HAM.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.ham"));
        registration.addIngredientInfo(new ItemStack(ModItems.SMOKED_HAM.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.ham"));
        registration.addIngredientInfo(new ItemStack(ModItems.FLINT_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.IRON_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.DIAMOND_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.NETHERITE_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));
        registration.addIngredientInfo(new ItemStack(ModItems.GOLDEN_KNIFE.get()), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.knife"));

        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_CABBAGES.get()), new ItemStack(ModItems.CABBAGE.get()), new ItemStack(ModItems.CABBAGE_LEAF.get())), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_cabbages"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_BEETROOTS.get()), new ItemStack(Items.BEETROOT)), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_beetroots"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_CARROTS.get()), new ItemStack(Items.CARROT)), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_carrots"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_ONIONS.get()), new ItemStack(ModItems.ONION.get())), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_onions"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_POTATOES.get()), new ItemStack(Items.POTATO)), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_potatoes"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_TOMATOES.get()), new ItemStack(ModItems.TOMATO.get())), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_tomatoes"));
        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_RICE.get()), new ItemStack(ModItems.RICE.get()), new ItemStack(ModItems.RICE_PANICLE.get())), VanillaTypes.ITEM_STACK, TextUtils.JEI("info.wild_rice"));
    }

    public static void registerToolboxRecipes(IRecipeRegistration registration) {
        List<Holder<Item>> toolboxes = new ArrayList<>();
        for (Holder<Item> entry : BuiltInRegistries.ITEM.getTagOrEmpty(AllItemTags.TOOLBOXES)) {
            toolboxes.add(entry);
        }
        Ingredient ingredient = Ingredient.of(HolderSet.direct(toolboxes));
        String group = "create.toolbox.color";
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(false);
        CraftingBookInfo bookInfo = new CraftingBookInfo(CraftingBookCategory.MISC, group);
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (DyeColor color : DyeColor.values()) {
            recipes.add(new RecipeHolder<>(
                ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MOD_ID, group + "/" + color)),
                new ShapelessRecipe(
                    commonInfo,
                    bookInfo,
                    new ItemStackTemplate(AllItems.TOOLBOX.pick(color)),
                    List.of(Ingredient.of(Items.DYE.pick(color)), ingredient)
                )
            ));
        }
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    @Override
    public <T> void registerFluidSubtypes(
        ISubtypeRegistration registration,
        IPlatformFluidHelper<T> platformFluidHelper
    ) {
        registration.registerSubtypeInterpreter(
            NeoForgeTypes.FLUID_STACK,
            AllFluids.POTION,
            PotionFluidSubtypeInterpreter.INSTANCE
        );
        registration.registerSubtypeInterpreter(
            NeoForgeTypes.FLUID_STACK,
            AllFluids.POTION.getFlowing(),
            PotionFluidSubtypeInterpreter.INSTANCE
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractSimiContainerScreen.class, new JeiExclusionZones());
        registration.addGhostIngredientHandler(AbstractFilterScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(BlueprintScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(LinkedControllerScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(ScheduleScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(RedstoneRequesterScreen.class, new GhostIngredientHandler<>());
        registration.addGhostIngredientHandler(FactoryPanelSetItemScreen.class, new GhostIngredientHandler<>());
        registration.addGuiContainerHandler(StockKeeperRequestScreen.class, new StockKeeperGuiContainerHandler());
        registration.addRecipeClickArea(CookingPotScreen.class, 89, 25, 24, 17, COOKING);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new BlueprintTransferHandler(), RecipeTypes.CRAFTING);
        registration.addUniversalRecipeTransferHandler(new StockKeeperTransferHandler());
        registration.addRecipeTransferHandler(CookingPotMenu.class, ModMenuTypes.COOKING_POT.get(), COOKING, 0, 6, 9, 36);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        StockKeeperRequestScreen.setSearchSync(new JeiStockSearchSync(
            jeiRuntime.getIngredientFilter(),
            jeiRuntime.getIngredientListOverlay()
        ));
    }
}
