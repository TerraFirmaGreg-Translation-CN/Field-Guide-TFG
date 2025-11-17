package io.github.tfgcn.fieldguide.data.tfc.page;

import io.github.tfgcn.fieldguide.data.patchouli.page.IPageDoubleRecipe;
import lombok.Data;

@Data
public class PageHeating extends IPageDoubleRecipe {

    public PageHeating() {
        super("tfc:heating");
    }
}
