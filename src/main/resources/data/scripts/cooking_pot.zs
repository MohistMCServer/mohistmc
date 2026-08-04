/*
Farmers Delight Cooking Pot
All parameters after the inputs are optional!
The CookingPotRecipeBookTab refers to a constant value.
*/

<recipetype:mohistmc:cooking>.addRecipe("cooking_pot_test", <item:minecraft:enchanted_golden_apple>,
    [<item:minecraft:gold_block>, <item:minecraft:apple>], <constant:mohistmc:cooking_pot_recipe_book_tab:meals>, <item:minecraft:air>,
    100, 400);

<recipetype:mohistmc:cooking>.addRecipe("cake_recipe", <item:minecraft:cake>, [<item:minecraft:egg>, <item:minecraft:sugar>, <item:minecraft:milk_bucket>], <constant:mohistmc:cooking_pot_recipe_book_tab:meals>, <item:minecraft:air>,  200, 1000);

val flower_tag = <tag:items:crafttweaker:flower_tag>;
flower_tag.add(<item:minecraft:pink_tulip>);
flower_tag.add(<item:minecraft:allium>);

<recipetype:mohistmc:cooking>.addRecipe("azalea_making", <item:minecraft:flowering_azalea>, [flower_tag], <constant:mohistmc:cooking_pot_recipe_book_tab:meals>, <item:minecraft:oak_leaves>, 0, 100);

<recipetype:mohistmc:cooking>.remove(<item:mohistmc:cooked_rice>);

