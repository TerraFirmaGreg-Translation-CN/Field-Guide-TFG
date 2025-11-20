package io.github.tfgcn.fieldguide.data.recipe.tfc;

import io.github.tfgcn.fieldguide.data.recipe.BaseRecipe;
import io.github.tfgcn.fieldguide.data.recipe.RecipeResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import lombok.Data;

import java.util.List;

@Data
public class GlassworkingRecipe extends BaseRecipe {
    private List<String> operations;
    private Ingredient batch;
    private RecipeResult result;
}