package io.github.tfgcn.fieldguide.book.page.tfc;

import io.github.tfgcn.fieldguide.book.page.AbstractPageDoubleRecipe;
import lombok.Data;

@Data
public class PageHeating extends AbstractPageDoubleRecipe {

    public PageHeating() {
        super("tfc:heating");
    }
}
