package io.github.tfgcn.fieldguide;

import io.github.tfgcn.fieldguide.asset.AssetLoader;
import io.github.tfgcn.fieldguide.asset.ItemImageResult;
import io.github.tfgcn.fieldguide.data.patchouli.Book;
import io.github.tfgcn.fieldguide.data.patchouli.BookCategory;
import io.github.tfgcn.fieldguide.data.patchouli.BookEntry;
import io.github.tfgcn.fieldguide.data.patchouli.BookPage;
import io.github.tfgcn.fieldguide.data.patchouli.page.PageTemplate;
import io.github.tfgcn.fieldguide.exception.InternalException;
import io.github.tfgcn.fieldguide.localization.Language;
import io.github.tfgcn.fieldguide.localization.LazyLocalizationManager;
import io.github.tfgcn.fieldguide.localization.LocalizationManager;
import io.github.tfgcn.fieldguide.render.TextFormatter;
import io.github.tfgcn.fieldguide.render.TextureRenderer;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static io.github.tfgcn.fieldguide.Constants.FIELD_GUIDE;

@Slf4j
public class Main2 implements Callable<Integer>  {

    @CommandLine.Option(
            names = {"-i", "--tfg-dir"},
            required = true,
            description = {"The dir of TerraFirmaGreg modpack.",
                    "Support environment TFG_DIR",
                    "e.g. \"/Users/yanmaoyuan/games/tfg-0.11.7\""},
            defaultValue = "${env:TFG_DIR}",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    protected String inputDir;

    @CommandLine.Option(
            names = {"-o", "--out-dir"},
            description = "The dir of output. e.g. \"./output\"",
            defaultValue = "./output",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    protected String outputDir;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Main2());
        System.exit(cmd.execute(args));
    }

    @Override
    public Integer call() throws Exception {
        log.info("Start parsing fallback..., tfg: {}, out: {}", inputDir, outputDir);

        // The TerraFirmaGreg modpack directory
        String modpackPath = inputDir.replace("\\", "/");

        AssetLoader assetLoader = new AssetLoader(Paths.get(modpackPath));

        LocalizationManager localizationManager = new LazyLocalizationManager(assetLoader);

        TextureRenderer textureRenderer = new TextureRenderer(assetLoader, localizationManager, outputDir);

        // Load en_us book as a fallback
        Book fallback = assetLoader.loadBook(FIELD_GUIDE);

        for (Language lang : Language.values()) {
            Book book = assetLoader.loadBook(FIELD_GUIDE, lang.getKey(), fallback);
            render(book, lang, assetLoader, localizationManager, textureRenderer);
        }
        return 0;
    }

    public void render(Book book, Language lang, AssetLoader assetLoader, LocalizationManager localizationManager, TextureRenderer textureRenderer) {

        // prepare
        localizationManager.switchLanguage(lang);
        String name = localizationManager.translate(book.getName());
        book.setName(name);
        String landingText = localizationManager.translate(book.getLandingText());
        book.setLandingText(landingText);
        log.info("Start parsing lang: {}, {} {} - {}", lang, name, book.getSubtitle(), landingText);

        // render categories
        for (BookCategory category : book.getCategories()) {
            // remove "§."
            category.setName(TextFormatter.stripVanillaFormatting(category.getName()));
            // 格式化描述文本
            List<String> descriptionBuffer = new ArrayList<>();
            TextFormatter.formatText(descriptionBuffer, category.getDescription(), localizationManager.getKeybindings());
            category.setDescription(String.join("", descriptionBuffer));

            System.out.printf("%s\n%s\n\n", category.getName(), category.getDescription());
        }

        // render entries
        for (BookEntry entry : book.getEntries()) {
            entry.setName(TextFormatter.stripVanillaFormatting(entry.getName()));

            try {
                ItemImageResult itemSrc = textureRenderer.getItemImage(entry.getIcon(), false);
                if (itemSrc != null) {
                    entry.setIconPath(itemSrc.getPath());
                    entry.setIconName(itemSrc.getName());
                } else {
                    log.error("Item image is null for entry: {}", entry.getId());
                }
            } catch (Exception e) {
                log.error("Failed to get item image for entry: {}", entry.getId());
            }

            System.out.printf("[%s]%s <img src=\"%s\" alt=\"%s\">\n", entry.getIcon(), entry.getName(), entry.getIconPath(), entry.getIconName());
        }

        // 搜索树
        List<Map<String, String>> searchTree = new ArrayList<>();

        // render pages
        for (BookEntry entry : book.getEntries()) {

            Map<String, String> search = new HashMap<>();
            search.put("content", "");
            search.put("entry", entry.getName());
            search.put("url", "./" + entry.getCategoryId() + "/" + entry.getRelId() + ".html");

            // 解析页面
            List<BookPage> pages = entry.getPages();
            if (pages != null) {
                for (BookPage page : pages) {
                    if (page instanceof PageTemplate) {
                        log.debug("Page: {}, {}", page.getType(), page.getJsonObject());
                    }
                    try {
                        // renderPage(entry.getBuffer(), page, search);
                    } catch (InternalException e) {
                        log.error("Failed to parse page: {}", page, e);
                    }
                }
            }

            searchTree.add(search);

            // render inner html
            if (!entry.isRendered()) {
                entry.setInnerHtml(String.join("", entry.getBuffer()));
                entry.setRendered(true);
            }
        }

    }
}