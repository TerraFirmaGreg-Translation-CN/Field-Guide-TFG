package io.github.tfgcn.fieldguide.data.recipe.tfc;

import io.github.tfgcn.fieldguide.data.recipe.CraftingRecipe;
import io.github.tfgcn.fieldguide.data.recipe.ShapedCraftingRecipe;
import lombok.Data;

@Data
public class DamageInputsShapedCraftingRecipe extends CraftingRecipe {
    private ShapedCraftingRecipe recipe;
}