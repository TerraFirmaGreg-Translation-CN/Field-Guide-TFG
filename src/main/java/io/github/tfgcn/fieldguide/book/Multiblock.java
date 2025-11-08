package io.github.tfgcn.fieldguide.book;

import lombok.Data;

import java.util.Map;

@Data
public class Multiblock {
    private Map<String, String> mapping;
    private String[][] pattern;
    private boolean symmetrical;
    private int[] offset;
}
