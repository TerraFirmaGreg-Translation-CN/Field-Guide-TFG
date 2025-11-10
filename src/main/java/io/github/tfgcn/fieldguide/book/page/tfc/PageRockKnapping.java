package io.github.tfgcn.fieldguide.book.page.tfc;

import io.github.tfgcn.fieldguide.book.page.IPageDoubleRecipe;
import lombok.Data;

import java.util.List;

@Data
public class PageRockKnapping extends IPageDoubleRecipe {

    private List<String> recipes;

    public PageRockKnapping() {
        super("tfc:rock_knapping");
    }
}
