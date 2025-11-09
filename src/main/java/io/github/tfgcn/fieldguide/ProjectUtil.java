package io.github.tfgcn.fieldguide;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class ProjectUtil {
    
    private static final Pattern SEARCH_STRIP_PATTERN = Pattern.compile("\\$\\([^)]*\\)");
    
    /**
     * 标准化资源路径为 domain:path 格式
     */
    public static String resourceLocation(String path) {
        if (!path.contains(":")) {
            return "minecraft:" + path;
        }
        return path;
    }
    
    /**
     * 从字典中按优先级查找键
     */
    public static <T> T anyOf(Map<String, T> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        throw new InternalError("None of " + java.util.Arrays.toString(keys) + " in " + map);
    }
    
    /**
     * 递归遍历目录下的所有文件
     */
    public static Collection<File> walk(String path) {
        File directory = new File(path);
        return FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }
    
    /**
     * 加载HTML模板
     */
    public static String loadHtml(String templateName) throws IOException {
        File templateFile = new File(pathJoin("assets/templates", templateName + ".html"));
        return FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
    }
    
    /**
     * 写入HTML文件
     */
    public static void writeHtml(String html, String... pathParts) throws IOException {
        String path = pathJoin(pathParts);
        File file = new File(path);
        FileUtils.forceMkdirParent(file);
        FileUtils.writeStringToFile(file, html, StandardCharsets.UTF_8);
    }
    
    /**
     * 写入JSON文件
     */
    public static void writeJson(Object data, String... pathParts) throws IOException {
        String path = pathJoin(pathParts);
        File file = new File(path);
        FileUtils.forceMkdirParent(file);
        // TODO
    }
    
    /**
     * 路径连接（跨平台）
     */
    public static String pathJoin(String... parts) {
        return Paths.get("", parts).normalize().toString();
    }
    
    /**
     * 清理搜索文本，移除 $(...) 模式
     */
    public static String searchStrip(String input) {
        return SEARCH_STRIP_PATTERN.matcher(input).replaceAll("");
    }
    
    /**
     * 条件检查，不满足时抛出异常
     */
    public static void require(boolean condition, String reason) {
        require(condition, reason, false);
    }
    
    public static void require(boolean condition, String reason, boolean quiet) {
        if (!condition) {
            throw new InternalError(reason, quiet);
        }
    }
}