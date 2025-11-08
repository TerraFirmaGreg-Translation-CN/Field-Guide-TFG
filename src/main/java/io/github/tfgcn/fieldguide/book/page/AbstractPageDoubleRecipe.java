package io.github.tfgcn.fieldguide.book.page;

import lombok.Data;

@Data
public class AbstractPageDoubleRecipe extends AbstractPageWithText {
    private String recipeType;

    private String recipe;
    private String recipe2;
    private String title;

    AbstractPageDoubleRecipe() {}
    protected AbstractPageDoubleRecipe(String recipeType) {
        this.recipeType = recipeType;
    }
}