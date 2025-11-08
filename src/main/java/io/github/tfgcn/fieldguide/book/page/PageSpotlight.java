package io.github.tfgcn.fieldguide.book.page;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.item.SpotlightItem;
import io.github.tfgcn.fieldguide.item.SpotlightItemAdapter;
import lombok.Data;

import java.util.List;

@Data
public class PageSpotlight extends AbstractPageWithText {

    @JsonAdapter(SpotlightItemAdapter.class)
    private List<SpotlightItem> item;

    /**
     * A custom title to show instead on top of the item.
     * If this is empty or not defined, it'll use the item's name instead.
     */
    private String title;

    @SerializedName("link_recipe")
    private Boolean linkRecipe;
}
