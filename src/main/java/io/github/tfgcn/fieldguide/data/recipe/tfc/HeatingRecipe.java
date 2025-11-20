package io.github.tfgcn.fieldguide.data.recipe.tfc;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.data.recipe.BaseRecipe;
import io.github.tfgcn.fieldguide.data.recipe.RecipeResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import lombok.Data;

@Data
public class HeatingRecipe extends BaseRecipe {
    private Ingredient ingredient;
    @SerializedName("result_item")
    private RecipeResult resultItem;
    private int temperature;
    private Float chance;
}