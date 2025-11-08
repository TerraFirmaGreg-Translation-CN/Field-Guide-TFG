package io.github.tfgcn.fieldguide;

import lombok.Getter;

@Getter
public class PatchouliBook {
    private final String modId;
    private final String bookId;
    private final String source;
    
    public PatchouliBook(String modId, String bookId, String source) {
        this.modId = modId;
        this.bookId = bookId;
        this.source = source;
    }

    @Override
    public String toString() {
        return "Book: " + modId + ":" + bookId + " (from " + source + ")";
    }
}