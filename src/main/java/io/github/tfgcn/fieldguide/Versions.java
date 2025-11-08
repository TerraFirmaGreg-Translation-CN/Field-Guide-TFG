package io.github.tfgcn.fieldguide;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Versions {
    // 当前版本信息
    public static final String VERSION = "v4.0.9-beta";
    public static final String MC_VERSION = "1.20.1";
    public static final String FORGE_VERSION = "47.4.6";
    
    // 支持的语言
    public static final List<String> LANGUAGES = Arrays.asList(
        "en_us", "ja_jp", "pt_br", "ko_kr", "uk_ua", 
        "zh_cn", "zh_hk", "zh_tw", "ru_ru"
    );

    // 计算得到的常量
    public static final String TFC_VERSION = MC_VERSION + " - " + VERSION;
    public static final boolean IS_RESOURCE_PACK = !"1.18.2".equals(MC_VERSION);
    public static final boolean IS_PLURAL_REGISTRIES = "1.18.2".equals(MC_VERSION) || "1.20.1".equals(MC_VERSION);
    
    /**
     * 获取注册表路径（处理复数形式）
     */
    public static String registry(String path) {
        return IS_PLURAL_REGISTRIES ? path + "s" : path;
    }
    
    public static void main(String[] args) {
        System.out.println(VERSION);
    }
}