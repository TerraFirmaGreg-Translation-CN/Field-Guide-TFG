package io.github.tfgcn.fieldguide.data.tfc.page;

import io.github.tfgcn.fieldguide.data.patchouli.page.IPageDoubleRecipe;
import lombok.Data;

@Data
public class PageBarrel extends IPageDoubleRecipe {

    public PageBarrel() {
        super("tfc:barrel");
    }
}
