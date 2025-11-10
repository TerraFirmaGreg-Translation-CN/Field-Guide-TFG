package io.github.tfgcn.fieldguide.book.page;

import lombok.Data;

import java.util.Map;

@Data
public class PageMultiblockData {
    private Map<String, String> mapping;
    private String[][] pattern;
    private boolean symmetrical;
    private int[] offset;
}
