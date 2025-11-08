package io.github.tfgcn.fieldguide.book.page;

import lombok.Data;

import java.util.List;

@Data
public class PageRelations extends AbstractPageWithText {

    private List<String> entries;

    private String title;
}
