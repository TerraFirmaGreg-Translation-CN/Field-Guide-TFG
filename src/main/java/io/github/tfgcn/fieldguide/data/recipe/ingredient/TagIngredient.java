package io.github.tfgcn.fieldguide.data.recipe.ingredient;

import io.github.tfgcn.fieldguide.asset.ItemImageResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import io.github.tfgcn.fieldguide.render.TextureRenderer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagIngredient implements Ingredient {
    private String tag;
    
    @Override
    public ItemImageResult toItemImage(TextureRenderer renderer) {
        return renderer.getItemImage("#" + tag, true);
    }
}