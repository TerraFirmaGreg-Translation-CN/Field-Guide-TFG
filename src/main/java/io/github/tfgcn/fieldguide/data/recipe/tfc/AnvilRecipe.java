package io.github.tfgcn.fieldguide.data.recipe.tfc;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.data.recipe.BaseRecipe;
import io.github.tfgcn.fieldguide.data.recipe.RecipeResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import lombok.Data;

import java.util.List;

@Data
public class AnvilRecipe extends BaseRecipe {
    private Ingredient input;
    private RecipeResult result;
    private int tier;
    private List<String> rules;
    @SerializedName("apply_forging_bonus")
    private Boolean applyForgingBonus;
}