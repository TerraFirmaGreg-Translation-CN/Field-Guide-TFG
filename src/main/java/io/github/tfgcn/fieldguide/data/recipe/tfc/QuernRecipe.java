package io.github.tfgcn.fieldguide.data.recipe.tfc;

import io.github.tfgcn.fieldguide.data.recipe.BaseRecipe;
import io.github.tfgcn.fieldguide.data.recipe.RecipeResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import lombok.Data;

@Data
public class QuernRecipe extends BaseRecipe {
    private Ingredient ingredient;
    private RecipeResult result;
}