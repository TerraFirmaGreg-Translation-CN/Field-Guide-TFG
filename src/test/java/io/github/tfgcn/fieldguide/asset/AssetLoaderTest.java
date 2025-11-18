package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.Constants;
import io.github.tfgcn.fieldguide.data.patchouli.Book;
import io.github.tfgcn.fieldguide.data.patchouli.BookCategory;
import io.github.tfgcn.fieldguide.data.patchouli.BookEntry;
import io.github.tfgcn.fieldguide.gson.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class AssetLoaderTest {

    static AssetLoader loader;

    //@BeforeAll
    static void init() {
        Path path = Paths.get("Modpack-Modern");
        loader = new AssetLoader(path);
    }

    @Test
    void test() {
        Assertions.assertTrue(true);
    }

    //@Test
    void testLoadBook() throws IOException {
        Book book = loadBook();
        Assertions.assertNotNull(book);
        book.report();

        for (String lang : Constants.LANGUAGES) {
            Book zhBook = loadBook(book, lang);
            zhBook.report();
        }
    }

    private Book loadBook() throws IOException {
        String bookPath = Constants.getBookPath();
        Asset bookAsset = loader.getAsset(bookPath);
        Assertions.assertNotNull(bookAsset);

        Book book = JsonUtils.readFile(bookAsset.getInputStream(), Book.class);
        Assertions.assertNotNull(book);
        book.setLanguage(Constants.EN_US);
        book.setAssetSource(bookAsset);

        // load categories
        String categoryDir = Constants.getCategoryDir();
        List<Asset> assets = loader.listAssets(categoryDir);
        Assertions.assertNotNull(assets);
        Assertions.assertFalse(assets.isEmpty());
        for (Asset asset : assets) {
            BookCategory category = JsonUtils.readFile(asset.getInputStream(), BookCategory.class);
            Assertions.assertNotNull(category);
            category.setAssetSource(categoryDir, asset);

            book.addCategory(category);
        }

        // load entries
        String entryDir = Constants.getEntryDir();
        assets = loader.listAssets(entryDir);
        Assertions.assertNotNull(assets);
        Assertions.assertFalse(assets.isEmpty());
        for (Asset asset : assets) {
            BookEntry entry = JsonUtils.readFile(asset.getInputStream(), BookEntry.class);
            Assertions.assertNotNull(entry);
            entry.setAssetSource(entryDir, asset);

            book.addEntry(entry);
        }

        book.sort();
        return book;
    }

    private Book loadBook(Book defaultBook, String lang) throws IOException {
        String bookPath = Constants.getBookPath();
        Asset bookAsset = loader.getAsset(bookPath);
        Assertions.assertNotNull(bookAsset);

        Book book = JsonUtils.readFile(bookAsset.getInputStream(), Book.class);
        Assertions.assertNotNull(book);
        book.setLanguage(lang);
        book.setAssetSource(bookAsset);

        String categoryDir = Constants.getCategoryDir(lang);
        String fallbackCategoryDir = Constants.getCategoryDir();
        for (BookCategory category : defaultBook.getCategories()) {
            String path = Constants.getCategoryPath(lang, category.getId());
            Asset asset = loader.getAsset(path);
            if (asset != null) {
                BookCategory localizedCategory = JsonUtils.readFile(asset.getInputStream(), BookCategory.class);
                localizedCategory.setAssetSource(categoryDir, asset);
                book.addCategory(localizedCategory);
            } else {
                // fallback
                path = Constants.getCategoryPath(category.getId());
                asset = loader.getAsset(path);
                BookCategory localizedCategory = JsonUtils.readFile(asset.getInputStream(), BookCategory.class);
                localizedCategory.setAssetSource(fallbackCategoryDir, asset);
                book.addCategory(localizedCategory);
            }
        }

        String entryDir = Constants.getEntryDir(lang);
        String fallbackEntryDir = Constants.getEntryDir();
        for (BookEntry entry : defaultBook.getEntries()) {
            String path = Constants.getEntryPath(lang, entry.getId());
            Asset asset = loader.getAsset(path);
            if (asset != null) {
                BookEntry localizedEntry = JsonUtils.readFile(asset.getInputStream(), BookEntry.class);
                localizedEntry.setAssetSource(entryDir, asset);
                book.addEntry(localizedEntry);
            } else {
                // fallback
                path = Constants.getEntryPath(entry.getId());
                asset = loader.getAsset(path);
                BookEntry localizedEntry = JsonUtils.readFile(asset.getInputStream(), BookEntry.class);
                localizedEntry.setAssetSource(fallbackEntryDir, asset);
                book.addEntry(localizedEntry);
            }
        }

        book.sort();
        return book;
    }

}
