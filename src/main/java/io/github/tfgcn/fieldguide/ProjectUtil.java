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