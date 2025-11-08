package io.github.tfgcn.fieldguide.book.page;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PageLink extends PageText {

    private String url;

    @SerializedName("link_text")
    private String linkText;
}
