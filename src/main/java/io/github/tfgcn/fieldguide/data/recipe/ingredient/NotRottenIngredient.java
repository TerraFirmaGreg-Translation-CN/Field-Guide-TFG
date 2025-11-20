package io.github.tfgcn.fieldguide.data.recipe.ingredient;

import io.github.tfgcn.fieldguide.asset.ItemImageResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import io.github.tfgcn.fieldguide.render.TextureRenderer;
import lombok.Data;

@Data
public class NotRottenIngredient implements Ingredient {
    private String type;

    private Ingredient ingredient;
    
    @Override
    public ItemImageResult toItemImage(TextureRenderer renderer) {
        return ingredient.toItemImage(renderer);
    }
}