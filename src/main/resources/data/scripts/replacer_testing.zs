import crafttweaker.api.recipe.replacement.type.ManagerFilteringRule;
import crafttweaker.api.recipe.replacement.Replacer;
import crafttweaker.api.recipe.type.Recipe;
import crafttweaker.api.world.Container;
import crafttweaker.api.util.random.Percentaged;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.ingredient.IIngredient;
import stdlib.List;

Replacer.create().filter(ManagerFilteringRule.of(<recipetype:mohistmc:cooking>)).replace<IItemStack>(<recipecomponent:mohistmc:recipe_component/cooking_pot_container>, <item:minecraft:oak_leaves>, <item:minecraft:stone>).execute();

Replacer.create().replace<IIngredient>(<recipecomponent:mohistmc:recipe_component/cutting_board_tool>, <tag:items:c:tools/knife>, <tag:items:c:tools/axe>).execute();

// Replacer.create().filter(ManagerFilteringRule.of(<recipetype:mohistmc:cutting>)).replace<List<Percentaged<IItemStack>>>(<recipecomponent:crafttweaker:output/chanced_items>, [<item:minecraft:white_dye>*2 % 100], [<item:minecraft:white_dye> % 60, <item:minecraft:white_dye> % 30]).execute();