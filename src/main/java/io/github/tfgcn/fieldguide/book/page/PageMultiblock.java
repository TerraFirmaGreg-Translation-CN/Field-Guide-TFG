package io.github.tfgcn.fieldguide.book.page;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.book.Multiblock;
import lombok.Data;

@Data
public class PageMultiblock extends AbstractPageWithText {

    private String name;

    private String multiblock_id;

    /**
     * @see <a href="https://vazkiimods.github.io/Patchouli/docs/patchouli-basics/multiblocks">Defining Multiblocks</a>
     */
    private Multiblock multiblock;

    @SerializedName("enable_visualize")
    private boolean enableVisualize;
}
