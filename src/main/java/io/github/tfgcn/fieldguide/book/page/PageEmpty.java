package io.github.tfgcn.fieldguide.book.page;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.book.BookPage;
import lombok.Data;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
@Data
public class PageEmpty extends BookPage {

    @SerializedName("draw_filler")
    private boolean filler = true;
}