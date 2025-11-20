package io.github.tfgcn.fieldguide.data.recipe;

import io.github.tfgcn.fieldguide.asset.ItemImageResult;
import io.github.tfgcn.fieldguide.render.TextureRenderer;

public interface Ingredient {
    ItemImageResult toItemImage(TextureRenderer renderer);
}