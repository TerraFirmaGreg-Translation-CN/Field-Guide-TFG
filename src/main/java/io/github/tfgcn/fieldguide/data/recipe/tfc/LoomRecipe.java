package io.github.tfgcn.fieldguide.data.recipe.tfc;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.data.recipe.BaseRecipe;
import io.github.tfgcn.fieldguide.data.recipe.RecipeResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import lombok.Data;

@Data
public class LoomRecipe extends BaseRecipe {
    private LoomIngredient ingredient;
    private RecipeResult result;
    @SerializedName("steps_required")
    private int stepsRequired;
    @SerializedName("in_progress_texture")
    private String inProgressTexture;

    @Data
    public static class LoomIngredient {
        private Ingredient ingredient;
        private Integer count;
    }
}