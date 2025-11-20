package io.github.tfgcn.fieldguide.data.recipe.tfc;

import io.github.tfgcn.fieldguide.data.recipe.CraftingRecipe;
import io.github.tfgcn.fieldguide.data.recipe.ShapelessCraftingRecipe;
import lombok.Data;

@Data
public class DamageInputsShapelessCraftingRecipe extends CraftingRecipe {
    private ShapelessCraftingRecipe recipe;
}