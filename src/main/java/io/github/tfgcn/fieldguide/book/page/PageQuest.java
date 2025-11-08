package io.github.tfgcn.fieldguide.book.page;

import lombok.Data;

@Data
public class PageQuest extends AbstractPageWithText {

    private String trigger;

    private String title;
}