package io.github.tfgcn.fieldguide;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Versions {
    public static final String MC_VERSION = "1.20.1";
    public static final String FORGE_VERSION = "47.4.6";

    public static final List<String> LANGUAGES = Arrays.asList(
        "en_us", "ja_jp", "pt_br", "ko_kr", "uk_ua", 
        "zh_cn", "zh_hk", "zh_tw", "ru_ru"
    );
}