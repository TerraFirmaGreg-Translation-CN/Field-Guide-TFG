package io.github.tfgcn.fieldguide.data.tfc.page;

import io.github.tfgcn.fieldguide.data.patchouli.page.IPageDoubleRecipe;
import lombok.Data;

@Data
public class PageDrying extends IPageDoubleRecipe {

    public PageDrying() {
        super("tfc:drying");
    }
}
