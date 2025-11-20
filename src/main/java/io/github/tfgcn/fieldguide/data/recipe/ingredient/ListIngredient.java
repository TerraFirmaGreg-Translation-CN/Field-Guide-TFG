package io.github.tfgcn.fieldguide.data.recipe.ingredient;

import io.github.tfgcn.fieldguide.asset.ItemImageResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import io.github.tfgcn.fieldguide.render.TextureRenderer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListIngredient extends ArrayList<Ingredient> implements Ingredient {
    private String type;

    @Override
    public ItemImageResult toItemImage(TextureRenderer renderer) {
        StringBuilder items = new StringBuilder();
        for (Ingredient child : this) {
            if (child instanceof ItemIngredient) {
                if (!items.isEmpty()) {
                    items.append(",");
                }
                items.append(((ItemIngredient) child).getItem());
            }
        }
        return renderer.getItemImage(items.toString(), true);
    }
}