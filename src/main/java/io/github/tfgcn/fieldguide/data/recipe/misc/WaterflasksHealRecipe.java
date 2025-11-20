package io.github.tfgcn.fieldguide.data.recipe.misc;

import io.github.tfgcn.fieldguide.data.recipe.CraftingRecipe;
import io.github.tfgcn.fieldguide.data.recipe.ShapedCraftingRecipe;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WaterflasksHealRecipe extends CraftingRecipe {
    private ShapedCraftingRecipe recipe;
}