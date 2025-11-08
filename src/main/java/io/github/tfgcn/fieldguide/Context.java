package io.github.tfgcn.fieldguide;

import com.google.gson.JsonObject;
import io.github.tfgcn.fieldguide.asset.Asset;
import io.github.tfgcn.fieldguide.asset.AssetLoader;
import io.github.tfgcn.fieldguide.book.BookCategory;
import io.github.tfgcn.fieldguide.book.BookEntry;
import io.github.tfgcn.fieldguide.item.ItemImageResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Context {
    // 静态缓存，所有 Context 实例共享
    private static final Map<String, String> IMAGE_CACHE = new HashMap<>();
    
    // 实例字段
    private final AssetLoader assetLoader;
    private final String outputRootDir;
    private final String rootDir;
    private final boolean debugI18n;
    
    private String outputDir;
    private String lang;

    // 数据结构
    private Map<String, BookCategory> categories = new HashMap<>();
    private Map<String, BookEntry> entries = new HashMap<>();
    private List<Map.Entry<String, BookCategory>> sortedCategories = new ArrayList<>();

    // 所有权管理
    private Map<String, String> categoryOwners = new HashMap<>();
    private Map<String, String> addonCategories = new HashMap<>();

    // 翻译和键绑定
    private Map<String, String> langKeys = new HashMap<>();
    private Map<String, String> keybindings = new HashMap<>();

    // ID 计数器
    private Map<String, Integer> lastUid = new HashMap<>();

    // 统计信息
    private int recipesFailed = 0;
    private int recipesPassed = 0;
    private int recipesSkipped = 0;
    private int itemsPassed = 0;
    private int itemsFailed = 0;
    private int blocksPassed = 0;
    private int blocksFailed = 0;
    
    // 搜索树
    private List<Map<String, Object>> searchTree = new ArrayList<>();
    
    public Context(AssetLoader assetLoader, String outputRootDir, String rootDir, boolean debugI18n) {
        this.assetLoader = assetLoader;
        this.outputRootDir = outputRootDir;
        this.outputDir = outputRootDir;
        this.rootDir = rootDir;
        this.debugI18n = debugI18n;

        // 初始化计数器
        lastUid.put("content", 0);
        lastUid.put("image", 0);
        lastUid.put("item", 0);
        lastUid.put("block", 0);
        lastUid.put("fluid", 0);
    }
    
    /**
     * 切换到指定语言
     */
    public Context withLang(String lang) {
        this.lang = lang;
        this.outputDir = ProjectUtil.pathJoin(outputRootDir, lang);
        
        this.categories = new HashMap<>();
        this.entries = new HashMap<>();
        this.sortedCategories = new ArrayList<>();
        this.langKeys = new HashMap<>();
        
        loadTranslations(lang);
        
        this.keybindings = new HashMap<>();
        for (String key : I18n.KEYS) {
            String bindingKey = key.substring("field_guide.".length());
            this.keybindings.put(bindingKey, translate(key));
        }
        
        return this;
    }

    public String getSourcePath(String path) {
        // if in resource pack, first folder is "data", otherwise "assets"
        // for now only support "assets"
        return String.format("%s/tfc/patchouli_books/field_guide/%s/%s", "assets", getLang(), path);
    }

    public List<Asset> listAssets(String resourcePath) throws IOException {
        return assetLoader.listAssets(resourcePath);
    }

    /**
     * 加载翻译文件
     */
    private void loadTranslations(String lang) {
        // 先加载 en_us
        loadLocalLang("en_us");
        // 再加载当前语言
        loadLocalLang(lang);
    }
    
    /**
     * 加载本地语言文件
     */
    private void loadLocalLang(String lang) {
        try {
            File langFile = new File(ProjectUtil.pathJoin("assets/lang", lang + ".json"));
            if (langFile.exists()) {
                String content = FileUtils.readFileToString(langFile, StandardCharsets.UTF_8);
                Map<String, String> data = JsonUtils.fromJson(content, Map.class);
                
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    this.langKeys.put("field_guide." + entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException e) {
            log.warn("Local Translation: {} : {}", lang, e.getMessage());
        }
    }
    
    /**
     * 生成唯一ID
     */
    public String nextId(String prefix) {
        int count = lastUid.getOrDefault(prefix, 0) + 1;
        lastUid.put(prefix, count);
        return prefix + count;
    }
    
    public String nextId() {
        return nextId("content");
    }
    
    /**
     * 添加条目到分类
     */
    public void addEntry(String categoryId, String entryId, BookEntry entry, Map<String, Object> search) {
        try {
            this.entries.put(entryId, entry);
            this.categories.get(categoryId).getEntries().add(entryId);
            this.searchTree.add(search);
        } catch (Exception e) {
            // FIXME
            log.warn("Add Entry: {} : {}, {}", categoryId, entryId, e.getMessage());
        }
    }
    
    /**
     * 对分类和条目进行排序
     */
    public void sort() {
        // 排序分类
        this.sortedCategories = new ArrayList<>(this.categories.entrySet());
        this.sortedCategories.sort((a, b) -> {
            BookCategory catA = a.getValue();
            BookCategory catB = b.getValue();
            if (catA.getSort() != catB.getSort()) {
                return Integer.compare(catA.getSort(), catB.getSort());
            }
            return a.getKey().compareTo(b.getKey());
        });

        // 对每个分类下的条目排序
        for (Map.Entry<String, BookCategory> entry : this.sortedCategories) {
            BookCategory cat = entry.getValue();
            List<String> sortedEntryNames = new ArrayList<>(cat.getEntries());
            sortedEntryNames.sort((a, b) -> {
                BookEntry entryA = this.entries.get(a);
                BookEntry entryB = this.entries.get(b);
                if (entryA.getSort() != entryB.getSort()) {
                    return Integer.compare(entryA.getSort(), entryB.getSort());
                }
                return a.compareTo(b);
            });
            
            List<Map.Entry<String, BookEntry>> sortedEntries = new ArrayList<>();
            for (String entryName : sortedEntryNames) {
                sortedEntries.add(Map.entry(entryName, this.entries.get(entryName)));
            }
            cat.setSortedEntries(sortedEntries);
        }
    }
    
    /**
     * 格式化文本
     */
    public void formatText(List<String> buffer, Map<String, Object> data, String key, Map<String, Object> search) {
        if (data.containsKey(key)) {
            String text = (String) data.get(key);
            TextFormatter.formatText(buffer, text, this.keybindings);
            
            if (search != null) {
                Map<String, Object> searchData = new HashMap<>(search);
                searchData.put("content", text);
                this.searchTree.add(searchData);
            }
        }
    }
    
    public void formatText(List<String> buffer, Map<String, Object> data) {
        formatText(buffer, data, "text", null);
    }
    
    /**
     * 格式化标题
     */
    public void formatTitle(List<String> buffer, Map<String, Object> data, String key, Map<String, Object> search) {
        if (data.containsKey(key)) {
            String title = (String) data.get(key);
            String stripped = TextFormatter.stripVanillaFormatting(title);
            buffer.add("<h5>" + stripped + "</h5>\n");
            
            if (search != null) {
                Map<String, Object> searchData = new HashMap<>(search);
                searchData.put("content", stripped);
                this.searchTree.add(searchData);
            }
        }
    }
    
    public void formatTitle(List<String> buffer, Map<String, Object> data) {
        formatTitle(buffer, data, "title", null);
    }
    
    /**
     * 带图标的标题格式化
     */
    public void formatTitleWithIcon(List<String> buffer, String iconSrc, String iconName, 
                                   Map<String, Object> data, String key, String tag, 
                                   String tooltip, Map<String, Object> search) {
        String title = iconName;
        if (data.containsKey(key)) {
            title = TextFormatter.stripVanillaFormatting((String) data.get(key));
            if (iconName == null || iconName.isEmpty()) {
                iconName = title;
            }
            if (search != null) {
                Map<String, Object> searchData = new HashMap<>(search);
                searchData.put("content", iconName);
                this.searchTree.add(searchData);
            }
        }
        
        if (tooltip == null) {
            tooltip = title;
        }
        
        String html = String.format("""
            <div class="item-header">
                <span href="#" data-bs-toggle="tooltip" title="%s">
                    <img src="%s" alt="%s" />
                </span>
                <%s>%s</%s>
            </div>
            """, iconName, iconSrc, tooltip, tag, title, tag);
        
        buffer.add(html);
    }
    
    public void formatTitleWithIcon(List<String> buffer, String iconSrc, String iconName, Map<String, Object> data) {
        formatTitleWithIcon(buffer, iconSrc, iconName, data, "title", "h5", null, null);
    }
    
    /**
     * 居中对齐文本
     */
    public void formatCenteredText(List<String> buffer, Map<String, Object> data, String key) {
        buffer.add("<div style=\"text-align: center;\">");
        formatText(buffer, data, key, null);
        buffer.add("</div>");
    }
    
    public void formatCenteredText(List<String> buffer, Map<String, Object> data) {
        formatCenteredText(buffer, data, "text");
    }
    
    /**
     * 带提示的文本
     */
    public void formatWithTooltip(List<String> buffer, String text, String tooltip) {
        String html = String.format("""
            <div style="text-align: center;">
                <p class="text-muted"><span href="#" data-bs-toggle="tooltip" title="%s">%s</span></p>
            </div>
            """, tooltip, text);
        buffer.add(html);
    }
    
    /**
     * 格式化配方
     */
    public void formatRecipe(List<String> buffer, Map<String, Object> data, String key) {
        if (data.containsKey(key)) {
            String recipeId = (String) data.get(key);
            String text = String.format("%s: <code>%s</code>", translate(I18n.RECIPE), recipeId);
            formatWithTooltip(buffer, text, translate(I18n.RECIPE_ONLY_IN_GAME));
        }
    }
    
    public void formatRecipe(List<String> buffer, Map<String, Object> data) {
        formatRecipe(buffer, data, "recipe");
    }
    
    /**
     * 翻译方法
     */
    public String translate(String... keys) {
        if (debugI18n) {
            return "{" + keys[0] + "}";
        }
        
        for (String key : keys) {
            if (langKeys.containsKey(key)) {
                return langKeys.get(key);
            }
        }
        
        throw new InternalError("Missing Translation Keys " + Arrays.toString(keys));
    }
    
    /**
     * 图片转换
     */
    public String convertImage(String image) {
        if (IMAGE_CACHE.containsKey(image)) {
            return IMAGE_CACHE.get(image);
        }

        try {
            Asset asset = assetLoader.loadResource(image, "assets", ".png");
            BufferedImage img = ImageIO.read(asset.getInputStream());

            int width = img.getWidth();
            int height = img.getHeight();

            if (width != height) {
                log.warn("Image is not square. Automatically resizing, but there may be losses. ({} x {}): {}", width, height, image);
            }

            if (width % 256 != 0) {
                log.warn("Image size is not a multiple of 256. Automatically resizing, but there may be losses. ({} x {}): {}",
                        width, height, image);
                img = resizeImage(img, 400, 400);
            }

            ProjectUtil.require(width == height && width % 256 == 0,
                    "Image size must be square and multiple of 256: " + image);

            int size = width * 200 / 256;
            BufferedImage cropped = img.getSubimage(0, 0, size, size);

            if (size != 400) {
                cropped = resizeImage(cropped, 400, 400);
            }

            String ref = saveImage(nextId("image"), cropped);
            IMAGE_CACHE.put(image, ref);
            return ref;
        } catch (Exception e) {
            throw new InternalError("Failed to convert image: " + image + " - " + e.getMessage());
        }
    }
    
    /**
     * 图标转换
     */
    public String convertIcon(String image) {
        if (IMAGE_CACHE.containsKey(image)) {
            return IMAGE_CACHE.get(image);
        }
        
        try {
            BufferedImage img = assetLoader.loadTexture(image);

            int width = img.getWidth();
            int height = img.getHeight();

            ProjectUtil.require(width == 16 && height == 16,
                    "Icon must be 16x16: " + image);

            // 调整到64x64以匹配物品图标尺寸
            BufferedImage resized = resizeImage(img, 64, 64);
            String ref = saveImage(nextId("image"), resized);
            IMAGE_CACHE.put(image, ref);
            return ref;
        } catch (Exception e) {
            throw new InternalError("Failed to convert icon: " + image + " - " + e.getMessage());
        }
    }
    
    /**
     * 图片缩放工具方法
     */
    private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resized;
    }


    /**
     * Saves an image to a location based on an identifier.
     * @param id the resource path identifier
     * @param image the image to save
     * @return the relative id to that location.
     */
    public String saveImage(String id, BufferedImage image) {
        // Parse resource location (remove namespace if present)
        String processedPath;
        if (id.contains(":")) {
            processedPath = id.split(":")[1];
        } else {
            processedPath = id;
        }

        // Replace slashes with underscores and join with '_images'
        String filename = processedPath.replace("/", "_");
        String outputPath = Paths.get("_images", filename).toString();

        // Ensure .png extension
        if (!outputPath.toLowerCase().endsWith(".png")) {
            outputPath += ".png";
        }

        File outputFile = new File(outputRootDir, outputPath);
        try {
            // Create output directory if it doesn't exist
            FileUtils.createParentDirectories(outputFile);

            // Save the image
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + outputFile.getAbsolutePath(), e);
        }

        // Return relative path
        return Paths.get("../../", outputPath).toString();
    }

    /**
     * Loads an item image, based on a specific keyed representation of an item.
     * The key may be an item ID ('foo:bar'), a tag ('#foo:bar'), or a csv list of item IDs ('foo:bar,foo:baz')
     * Using a global cache, the image will be generated, and saved to the _images/ directory.
     * For items that aren't render-able (for various reasons), this will use a placeholder image.
     * @param item
     * @param placeholder
     * @return
     *     path: The path to the item image (for use in href="", or src="")
     *     name: The translated name of the item (if a single item), or a best guess (if a tag), or None (if csv)
     */
    public ItemImageResult getItemImage(String item, boolean placeholder) {

        if (item.endsWith(".png")) {
            // This is not an item image, it must be a image directly
            return new ItemImageResult(convertIcon(item), null);
        }

        if (item.startsWith("tag:")) {
            item = "#" + item.substring(4);
            log.info("tag: {}", item);
        }

        // TODO get from cache

        if (item.indexOf('{') > 0) {
            log.warn("Item with NBT: {}", item);
            throw new InternalError("Item with NBT: " + item);
        }


        String name = null;
        String key = null;// translation key, if this needs to be re-translated
        List<String> items;

        if (item.startsWith("#")) {
            name = translate(I18n.TAG) + item;
            items = loadItemTag(item.substring(1));
        } else if (item.contains(",")) {
            items = Arrays.asList(item.split(","));
        } else {
            items = Collections.singletonList(item);
        }

        if (items.size() == 1) {
            key = items.get(0).replace('/', '.').replace(':', '.');
            name = translate("item." + key, "block." + key);
        }

        try {
            // Create image for each item.
            List<BufferedImage> images = items.stream().map(itemId -> createItemImage(this, itemId)).toList();


        } catch (Exception e) {
            log.warn("Failed to create item image: {}", item, e);
            if (placeholder) {
                // # Fallback to using the placeholder image
                return new ItemImageResult("_images/item_placeholder.png", name);
            } else {
                throw e;
            }
        }

        ItemImageResult result = new ItemImageResult(null, null);
        return result;
    }

    public List<String> loadItemTag(String tag) {
        // TODO
        return List.of();
    }

    BufferedImage createItemImage(Context context, String itemId) {
        loadItemModel(itemId);
        // TODO

        return null;
    }

    private void loadItemModel(String itemId) {
        Asset asset = assetLoader.loadResource(itemId, "models/item", "assets", ".json");

        if (asset == null) {
            log.warn("Item model not found: {}", itemId);
            throw new InternalError("Item model not found: " + itemId);
        }
        try {
            JsonObject model = JsonUtils.readFile(asset.getInputStream(), JsonObject.class);
            log.info("Item {}, path:{}, source:{}, model: {}", itemId, asset.getPath(), asset.getSource(), model);
        } catch (Exception e) {
            log.warn("Failed to load item model: {}", itemId, e);
            throw new InternalError("Failed to load item model: " + itemId);
        }
        // TODO
    }
}