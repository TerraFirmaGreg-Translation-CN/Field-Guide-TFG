package io.github.tfgcn.fieldguide.book.page;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.book.adapter.PageSpotlightItemAdapter;
import lombok.Data;

import java.util.List;

@Data
public class PageSpotlight extends IPageWithText {

    @JsonAdapter(PageSpotlightItemAdapter.class)
    private List<PageSpotlightItem> item;

    /**
     * A custom title to show instead on top of the item.
     * If this is empty or not defined, it'll use the item's name instead.
     */
    private String title;

    @SerializedName("link_recipe")
    private Boolean linkRecipe;
}
