package io.github.tfgcn.fieldguide.render;

import freemarker.template.*;
import io.github.tfgcn.fieldguide.Context;
import io.github.tfgcn.fieldguide.I18n;
import io.github.tfgcn.fieldguide.book.BookCategory;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class HtmlRenderer {
    private final Configuration cfg;
    private final String outputDir;
    
    public HtmlRenderer(String templateDir, String outputDir) throws IOException {
        this.outputDir = outputDir;
        
        Files.createDirectories(Paths.get(outputDir));

        // 配置 FreeMarker
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(new File(templateDir));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setAutoEscapingPolicy(Configuration.DISABLE_AUTO_ESCAPING_POLICY);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }
    
    public void generatePage(String templateName, String outputDir, String outputFileName, Map<String, Object> data)
            throws IOException, TemplateException {
        
        Template template = cfg.getTemplate(templateName);
        Path outputPath = Paths.get(outputDir, outputFileName);

        FileUtils.createParentDirectories(outputPath.toFile());
        try (Writer out = new OutputStreamWriter(
                Files.newOutputStream(outputPath), StandardCharsets.UTF_8)) {
            template.process(data, out);
        }
    }
    
    public static void main(String[] args) {
        try {
            HtmlRenderer generator = new HtmlRenderer("assets/templates", "output");

            // 准备数据模型
            Map<String, Object> data = new HashMap<>();
            data.put("long_title", "TerraFirmaCraft Field Guide");
            data.put("short_description", "Complete guide for TerraFirmaCraft mod");
            data.put("preview_image", "preview.png");
            data.put("root", ".");
            data.put("title", "Field Guide");
            data.put("index", "index.html");
            data.put("text_version", "Version");
            data.put("tfc_version", "1.18.2");
            data.put("current_lang", "English");
            data.put("langs", generateLanguageDropdown());
            data.put("text_contents", "Contents");
            data.put("text_index", "Index");
            data.put("contents", generateTableOfContents());
            data.put("location", generateBreadcrumb("Home"));
            data.put("page_content", generatePageContent());
            data.put("text_api_docs", "API Documentation");
            data.put("text_github", "GitHub");
            data.put("text_discord", "Discord");
            data.put("current_lang_key", "en");
            
            // 生成页面
            generator.generatePage("index.ftl", "output", "index.html", data);
            
            System.out.println("Static site generated successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateLanguageDropdown(List<String> languages, Context context) {
        return languages.stream()
                .map(lang -> String.format(
                        """
                        <a href="../%s/" class="dropdown-item">%s</a>
                        """,
                        lang,
                        context.translate(String.format(I18n.LANGUAGE_NAME, lang))
                ))
                .collect(Collectors.joining("\n"));
    }

    public static String indexBreadcrumbModern(String relativePath) {
        String iconHtml = "<i class=\"bi bi-house-fill\"></i>";

        if (relativePath == null || relativePath.trim().isEmpty()) {
            return "<li class=\"breadcrumb-item\">" + iconHtml + "</li>";
        } else {
            return """
                <li class="breadcrumb-item">
                    <a href="%s">%s</a>
                </li>
                """.formatted(relativePath, iconHtml);
        }
    }

    public static String generateTableOfContents(List<Map.Entry<String, BookCategory>> sortedCategories) {
        return sortedCategories.stream()
                .map(entry -> {
                    String catId = entry.getKey();
                    BookCategory category = entry.getValue();
                    return String.format(
                            """
                            <li><a href="./%s/">%s</a></li>
                            """,
                            catId, category.getName()
                    );
                })
                .collect(Collectors.joining("\n"));
    }

    /**
     * 生成主页内容
     */
    public static String generateHomePageContent(Context context, List<Map.Entry<String, BookCategory>> sortedCategories) {
        String splashImage = getSplashLocation();
        String textHome = context.translate(I18n.HOME);
        String textEntries = context.translate(I18n.CATEGORIES);

        // 生成分类卡片
        String categoryCards = sortedCategories.stream()
                .map(entry -> {
                    String catId = entry.getKey();
                    BookCategory category = entry.getValue();
                    return String.format(
                            """
                            <div class="col">
                                <div class="card">
                                    <div class="card-header">
                                        <a href="%s/index.html">%s</a>
                                    </div>
                                    <div class="card-body">%s</div>
                                </div>
                            </div>
                            """,
                            catId, category.getName(), category.getDescription()
                    );
                })
                .collect(Collectors.joining("\n"));

        return String.format(
                """
                <!-- START -->
                <img class="d-block w-200 mx-auto mb-3 img-fluid" src="../_images/%s.png" alt="TerraFirmaCraft Field Guide Splash Image">
                <p>%s</p>
                <p><strong>%s</strong></p>
                <div class="row row-cols-1 row-cols-md-2 g-3">
                %s
                </div><!-- END -->
                """,
                splashImage, textHome, textEntries, categoryCards
        );
    }

    private static String getSplashLocation() {
        // 实现获取 splash 图片位置的方法
        return "splash_image"; // 示例返回值
    }

    private static String generateLanguageDropdown() {
        return "<a class=\"dropdown-item\" href=\"?lang=en\">English</a>" +
               "<a class=\"dropdown-item\" href=\"?lang=zh\">中文</a>";
    }
    
    private static String generateTableOfContents() {
        return "<li><a href=\"getting-started.html\">Getting Started</a></li>" +
               "<li><a href=\"items.html\">Items</a></li>" +
               "<li><a href=\"blocks.html\">Blocks</a></li>";
    }
    
    private static String generateBreadcrumb(String currentPage) {
        return "<li class=\"breadcrumb-item\"><a href=\"index.html\">Home</a></li>" +
               "<li class=\"breadcrumb-item active\">" + currentPage + "</li>";
    }
    
    private static String generatePageContent() {
        return "<h1>Welcome to TerraFirmaCraft Field Guide</h1>" +
               "<p>This is a comprehensive guide for the TerraFirmaCraft mod.</p>";
    }
}