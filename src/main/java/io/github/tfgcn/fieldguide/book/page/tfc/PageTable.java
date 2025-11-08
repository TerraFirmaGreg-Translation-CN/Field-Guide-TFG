package io.github.tfgcn.fieldguide.book.page.tfc;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.book.page.AbstractPageWithText;
import lombok.Data;

import java.util.List;

@Data
public class PageTable extends AbstractPageWithText {
    private List<StyledStringElement> strings; // 包含 text 和 bold 字段的元素
    private List<LegendItem> legend;
    private Integer columns;
    @SerializedName("first_column_width")
    private Integer firstColumnWidth;
    @SerializedName("column_width")
    private Integer columnWidth;
    @SerializedName("row_height")
    private Integer rowHeight;
    @SerializedName("left_buffer")
    private Integer leftBuffer;
    @SerializedName("top_buffer")
    private Integer topBuffer;

    private String title;

    @SerializedName("draw_background")
    private boolean drawBackground;
}
