package io.github.tfgcn.fieldguide.data.recipe.ingredient;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.asset.ItemImageResult;
import io.github.tfgcn.fieldguide.data.recipe.Ingredient;
import io.github.tfgcn.fieldguide.render.TextureRenderer;
import lombok.Data;

@Data
public class FluidItemIngredient implements Ingredient {
    private String type;
    @SerializedName("fluid_ingredient")
    private FluidIngredient fluidIngredient;

    @Override
    public ItemImageResult toItemImage(TextureRenderer renderer) {
        // 根据流体类型返回对应的桶物品
        if ("minecraft:water".equals(fluidIngredient.getIngredient())) {
            return renderer.getItemImage("minecraft:water_bucket", true);
        }
        // 处理其他流体
        return renderer.getItemImage(fluidIngredient.getIngredient() + "_bucket", true);
    }
}