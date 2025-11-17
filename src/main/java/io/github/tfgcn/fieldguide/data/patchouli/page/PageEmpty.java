package io.github.tfgcn.fieldguide.data.patchouli.page;

import com.google.gson.annotations.SerializedName;
import io.github.tfgcn.fieldguide.data.patchouli.BookPage;
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