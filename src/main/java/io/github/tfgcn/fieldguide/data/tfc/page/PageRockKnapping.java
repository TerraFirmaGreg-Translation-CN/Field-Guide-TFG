package io.github.tfgcn.fieldguide.data.tfc.page;

import io.github.tfgcn.fieldguide.data.patchouli.page.IPageDoubleRecipe;
import lombok.Data;

import java.util.List;

@Data
public class PageRockKnapping extends IPageDoubleRecipe {

    private List<String> recipes;

    public PageRockKnapping() {
        super("tfc:rock_knapping");
    }
}
