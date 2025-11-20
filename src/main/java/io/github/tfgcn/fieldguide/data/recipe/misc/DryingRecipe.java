package io.github.tfgcn.fieldguide.data.recipe.misc;

import io.github.tfgcn.fieldguide.data.recipe.BaseRecipe;
import io.github.tfgcn.fieldguide.data.recipe.RecipeResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import lombok.Data;

@Data
public class DryingRecipe extends BaseRecipe {
    private Ingredient ingredient;
    private RecipeResult result;
}